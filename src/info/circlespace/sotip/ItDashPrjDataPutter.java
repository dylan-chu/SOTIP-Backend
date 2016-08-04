/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import info.circlespace.sotip.api.ProjectInfo;

/**
 * This Lambda function is triggered by an update to a DynamoDB table called SotipConfig.  It uses
 * the data passed by the DynamoDB event to determine the name of a file to process in a S3 bucket.
 * It reads in this file and stores the contents of the file into another DynamoDB table called SotipPrjs.
 * The file contains information for projects tracked by IT Dashboard.
 */
public class ItDashPrjDataPutter implements RequestHandler<DynamodbEvent, Object> {

	static final int OK = 0;
	
	LambdaLogger mLogger;
    AmazonDynamoDBClient mClient;
    DynamoDB mDynamoDB;
    
    // name of S3 bucket containing the file to process
	String bucketName = "it-dashboard-data";
	// base name of the file
	String fileKeyBase = "projects-";
	// the number identifying the specific file
	int fileNdx = 0;

	
	@Override
    public Object handleRequest(DynamodbEvent input, Context context) {
		mLogger = context.getLogger();
		mLogger.log("Input: " + input);

		DynamodbStreamRecord record = input.getRecords().get(0);
		mLogger.log(record.getDynamodb().toString());

		// check that the DynamoDB event has values for the changes to the database
		Map<String, AttributeValue> dbEvtValues = record.getDynamodb().getNewImage();
		if ( dbEvtValues == null ) {
			return "values for 'NewImage' was not passed in";
		}

		// the value of 'NumFiles' is used to determine which file in S3 to process
		AttributeValue numFileValue = (AttributeValue) dbEvtValues.get( SotipConfig.COL_NUM_FILES );
		if ( numFileValue == null ) {
			return "value of 'NumFiles' is null";
		}
		
		fileNdx = Integer.parseInt( numFileValue.getN() );
		if ( fileNdx == 0 ) {
			// if the value is zero, we are done processing the files
			return "Nothing to do";
		}
		
		// point the Amazon client at the right region
		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);
		
		// determine the exact name of the file to process and then process it
		String lastUpdDate = ((AttributeValue) dbEvtValues.get( SotipConfig.COL_LAST_UPD_DATE )).getS();
		String fileKey = fileKeyBase + lastUpdDate + "-" + fileNdx;
		int errorCode = processFilesOnS3( bucketName, fileKey );
		
		if ( errorCode != OK ) {
			return "There was a problem processing " + fileKey;
		}
		
		// update the DynamoDB table to trigger this Lambda function again to process the next file
		saveConfig( lastUpdDate, fileNdx-1 );
		
	    return "Successfully processed " + fileKey;
    }
	
    
	/**
	 * Reads in the contents of a file in a S3 bucket and puts that content in a DynamoDB table
	 * 
	 * @param bucketName
	 * 			the name of the S3 bucket
	 * 
	 * @param fileKey
	 * 			the name of the file
	 * 
	 * @return a code indicating the success of the processing
	 */
	private int processFilesOnS3( String bucketName, String fileKey ) {
    	int SERVICE_ERROR = 100;
    	int CLIENT_ERROR = 110;
    	int IO_ERROR = 120;

    	AmazonS3 s3 = new AmazonS3Client();
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(usEast1);
        
        try {
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, fileKey));
            List<ProjectInfo> items = processData(object.getObjectContent());
            putProjectItemsInDb( items );
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            //System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
			mLogger.log("Error Message: " + ase.getMessage());
            return SERVICE_ERROR;
            
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
			mLogger.log("Error Message: " + ace.getMessage());
            return CLIENT_ERROR;
            
        } catch (IOException ioe ) {
        	mLogger.log("IOException: " + ioe.toString() );
        	return IO_ERROR;
        }

        return OK;
    }
    
    
	/**
	 * Takes the content of a file as a stream and converts the JSON data it contains
	 * to POJOs
	 * 
	 * @param input
	 * 			the content of a file as a stream
	 * 
	 * return a list of POJOs containing records for IT projects
	 * 
	 * @throws IOException
	 */
	
    private List<ProjectInfo> processData(InputStream input) throws IOException {
        List<ProjectInfo> items = new ArrayList<ProjectInfo>();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        Gson gson = new Gson();
        
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            
            ProjectInfo item = gson.fromJson(line, ProjectInfo.class);
            items.add( item );
        }
        
        input.close();
        reader.close();

        return items;
    }

    
    /**
     * Saves the data for each project as items in the DynamoDB table
     * 
     * @param items
     * 			a list of objects each holding the data for one project
     */
	private void putProjectItemsInDb(List<ProjectInfo> items) {

		Table table = mDynamoDB.getTable( ProjectItem.TBL_NAME );

		for (ProjectInfo item : items) {
			int costRating = 0;
			
			// cost variance is negative for projects over budget
			if ( item.getCostVariancePercent() <= -30d ) {
				costRating = 5;
			} else if ( item.getCostVariancePercent() <= -10d ) {
				costRating = 4;
			} else if ( item.getCostVariancePercent() <= 0 ) {
				costRating = 3;
			} else if ( item.getCostVariancePercent() < 10d ) {
				costRating = 2;
			} else if ( item.getCostVariancePercent() < 30d ) {
				costRating = 1;
			}
			
			int schRating = 0;
			
			// schedule variance is negative for projects behind schedule
			if ( item.getScheduleVariancePercent() <= -30d ) {
				schRating = 5;
			} else if ( item.getScheduleVariancePercent() <= -10d ) {
				schRating = 4;
			} else if ( item.getScheduleVariancePercent() <= 0 ) {
				schRating = 3;
			} else if ( item.getScheduleVariancePercent() < 10d ) {
				schRating = 2;
			} else if ( item.getScheduleVariancePercent() < 30d ) {
				schRating = 1;
			}
			
			// use both variances to determine a rating for the performance of the project
			int perfRating = costRating + schRating;
			
			// convert the performance rating to a code between 0 to 4
			int perfNdx = Math.round( perfRating / 2f ) - 1;
			if ( perfNdx < 0 )
				perfNdx = 0;
			
			// convert the project status from a string to a code
			int statusCode = ProjectInfo.CODE_PROJ_STATUS_IN_PROG;
			
			if ( item.getProjectStatus().equals( ProjectInfo.PROJ_STATUS_COMPLTD )) {
				statusCode = ProjectInfo.CODE_PROJ_STATUS_COMPLTD;
			}
			
			// extract the number representing the pm experience level
			int pmExpLvlNdx = ProjectInfo.getCode( item.getPmExperienceLevel() );
			
			// extract the number representing the software development methodology
			int sdlcMethodNdx = ProjectInfo.NULL_SDM;
			if ( item.getSDLCMethodology() != null ) {
				sdlcMethodNdx = ProjectInfo.getCode( item.getSDLCMethodology() );
			}
			
			Item tblItem = new Item()
					.withPrimaryKey(ProjectItem.COL_ID, item.getProjectID() )
					.withString( ProjectItem.COL_AGENCY_CODE, item.getAgencyCode() )
					.withString( ProjectItem.COL_INVESTMENT_ID, item.getUniqueInvestmentIdentifier() )
					.withString( ProjectItem.COL_INVESTMENT_TITLE, item.getInvestmentTitle() )
					.withString( ProjectItem.COL_NAME, item.getProjectName() )
					.withInt( ProjectItem.COL_PROJ_PERF, perfNdx )
					.withInt( ProjectItem.COL_STATUS, statusCode )
					.withString( ProjectItem.COL_START_DATE, item.getStartDate() )
					.withString( ProjectItem.COL_CMPLTN_DATE, item.getCompletionDate() )
					.withInt( ProjectItem.COL_SCH_VAR_CATEG, schRating )
					.withDouble( ProjectItem.COL_SCH_VAR_PERC, item.getScheduleVariancePercent() )
					.withInt( ProjectItem.COL_SCH_VAR_DAYS, item.getScheduleVarianceInDays() )
					.withDouble( ProjectItem.COL_LIFECYCLE_COST, item.getProjectLifeCycleCost() )
					.withInt( ProjectItem.COL_COST_VAR_CATEG, costRating )
					.withDouble( ProjectItem.COL_COST_VAR_PERC, item.getCostVariancePercent() )
					.withDouble( ProjectItem.COL_COST_VAR_AMT, item.getCostVarianceDollars() )
					.withInt( ProjectItem.COL_PM_EXP_LVL, pmExpLvlNdx )
					.withInt( ProjectItem.COL_SDLC_METHOD, sdlcMethodNdx )
					.withString( ProjectItem.COL_OTHER_SDM, ProjectInfo.transformNull( item.getOtherSDLC() ) )
					.withString( ProjectItem.COL_OBJECTIVES, item.getObjectivesExpectedOutcomes() )
					.withString( ProjectItem.COL_UPDATED_DATE, item.getUpdatedDate() );

			// Write the item to the table
			PutItemOutcome outcome = table.putItem(tblItem);			
		}
		
        mLogger.log( "Number of items put into table: " + items.size() );
	}


    /**
     * Updates the item in the DynamoDB table called SotipConfig.  Updating the item
     * triggers this Lambda function again.
     * 
     * @param updDateStr
     * 			the date of the data pull
     * 
     * @param numFiles
     * 			the number of files created from the pulled data
     */
    private void saveConfig( String updDateStr, int numFiles ) {
		DynamoDB dynamoDB = new DynamoDB(mClient);		
		Table table = dynamoDB.getTable( SotipConfig.TBL_NAME );
		
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#U", SotipConfig.COL_LAST_UPD_DATE);
		expressionAttributeNames.put("#N", SotipConfig.COL_NUM_FILES);

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":val1", updDateStr); 
		expressionAttributeValues.put(":val2", numFiles);

		UpdateItemOutcome outcome = table.updateItem(
				SotipConfig.COL_ID, 
				SotipConfig.CONFIG_KEY, 
				"set #U = :val1, #N = :val2",
				expressionAttributeNames, 
				expressionAttributeValues);
    }

}
