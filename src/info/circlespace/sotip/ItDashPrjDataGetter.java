/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;

import info.circlespace.sotip.api.ItDashboardApiSvc;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.api.ProjectsApiRes;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This Lambda function makes an API call to IT Dashboard to get the data from the "projects" data feed.
 * It stores that data in multiple files in a S3 bucket.  The function is triggered by a scheduled event.
 */
public class ItDashPrjDataGetter implements RequestHandler<Object, Object> {
	
	static final int ERROR = -1;
	static final int OK = 0;

	// the name of the S3 bucket
    String mBucketName = "it-dashboard-data";
    // the number of seconds before the API call to IT Dashboard times out
    int mApiTimeout = 60;
    // the format of the updated date from IT Dashboard
    String mDataDateFmt = "MM/dd/yyyy";
    SimpleDateFormat mDataDateFmtr = new SimpleDateFormat( mDataDateFmt );
    // the format of the data desired
    String mDateLimitFmt = "yyyy-MM-dd";
    SimpleDateFormat mDateLimitFmtr = new SimpleDateFormat( mDateLimitFmt );
    // the number of results to write to a file
    int mBatchSize = 500;
    // the default year for any date string that cannot be parsed
    int mDefaultYear = 1900;
    // the base name for any files
    String fileBase = "projects-";
    
	LambdaLogger mLogger;
    AmazonDynamoDBClient mClient;
    
    
	@Override
    public Object handleRequest(Object input, Context context) {
		// the logger writes to CloudWatch logs
        mLogger = context.getLogger();
        mLogger.log("Input: " + input);
        
        List<ProjectInfo> items = pullFromProjectsDataFeed();
        if ( items.isEmpty() ) {
			mLogger.log( "No results to process" );
        	return null;
        }
        
        mLogger.log( "Processing " + items.size() + " results ..." );
        
        // set the Amazon client to point to the region for the DynamoDB tables
        mClient = new AmazonDynamoDBClient();
    	mClient.withRegion(Regions.US_EAST_1);
    	
    	// get the last date there was a successful pull of data from IT Dashboard
    	SotipConfig cfg = getConfig();
    	Date lastUpdDate;
    	
    	try {
    		lastUpdDate = mDateLimitFmtr.parse( cfg.getLastUpdDate() );
    	} catch (ParseException pe) {
    		lastUpdDate = new Date( );
    		lastUpdDate.setYear(mDefaultYear);
    	}
    	
        mLogger.log( "Saving results from " + mDateLimitFmtr.format( lastUpdDate ) );
        
        // use the current date in the name of any created files
        Calendar now = GregorianCalendar.getInstance();
		String today = mDateLimitFmtr.format( now.getTime() );
		String fileKeyBase = fileBase + today;
		
		// save the data in multiple files in S3
		int numFiles = saveInS3(items, fileKeyBase, lastUpdDate);
		if ( numFiles == ERROR ) {
			mLogger.log( "Encountered error saving data on " + today );
			return null;
		}
		
		// in the SotipConfig table, set the date of the last successful pull from IT Dashboard to today 
		saveConfig( today, numFiles );
		mLogger.log( "Successfully saved data from IT Dashboard on " + today );

        return cfg;
    }


	/**
	 * Makes an API call to IT Dashboard and stores the JSON data that it returns.
	 * 
	 * @return a list of objects representing the data for a set of projects
	 */
	private List<ProjectInfo> pullFromProjectsDataFeed() {
		
		List<ProjectInfo> items = new ArrayList<ProjectInfo>();

		// increasing the timeout period is necessary because the default timeout period is 10 seconds
		OkHttpClient okClient = new OkHttpClient.Builder()
				.connectTimeout(mApiTimeout, TimeUnit.SECONDS)
				.readTimeout(mApiTimeout, TimeUnit.SECONDS)
				.writeTimeout(mApiTimeout, TimeUnit.SECONDS)
				.build();

		// Retrofit simplifies the call to IT Dashboard and the processing of the returned data
		Retrofit retro = new Retrofit.Builder()
				.baseUrl(ItDashboardApiSvc.BASE_URL)
				.client(okClient)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		ItDashboardApiSvc.ProjectsApi dataSvc = retro.create(ItDashboardApiSvc.ProjectsApi.class);
		Call<ProjectsApiRes> call = dataSvc.getProjects();

		try {
			// make the synchronous call to the API
			Response<ProjectsApiRes> response = call.execute();
			ProjectsApiRes apiRes = response.body();
			
			if (apiRes == null) {
				mLogger.log( "Error: API call returned null response");
				return items;
			}

			items = apiRes.getResult();

		} catch (Exception e) {
			mLogger.log( "Error: Api call failed: " + e.toString());

		}
		
		return items;
	}

	
	/**
	 * Saves the returned data as multiple files in S3.
	 * 
	 * @param items
	 * 			the returned data from the API call
	 * 
	 * @param fileKeyBase
	 * 			the base name for any file that will be created in S3
	 * 
	 * @param lastUpdDate
	 * 			the last date the data was successfully pulled from IT Dashboard
	 * 
	 * @return the number of files that was created or an error code
	 */
    private int saveInS3( List<ProjectInfo> items, String fileKeyBase, Date lastUpdDate ) {

    	int numFiles = 0;
    	
    	// set the region of the S3 bucket
    	AmazonS3 s3 = new AmazonS3Client();
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(usEast1);

        try {
        	int count = 0;
        	List<ProjectInfo> itemsToStore = new ArrayList<ProjectInfo>();
			String fileName;
			
        	for ( ProjectInfo item: items ) {

        		// discard any data for projects which have not been updated since the
        		// last date there was a successful call to IT Dashboard
        		try {
        			Date itemUpdDate = mDataDateFmtr.parse( item.getUpdatedDate() );
        			if ( itemUpdDate.before( lastUpdDate ))
        				continue;
        			 
            		item.setUpdatedDate( mDateLimitFmtr.format( itemUpdDate ));
        		} catch ( ParseException pe ) {
        			continue;
        		}
        		
        		itemsToStore.add( item );
        		count++;

        		// write to S3 if there is enough items for a batch
        		if ( count >= mBatchSize ) {
        			numFiles++;
        			fileName = fileKeyBase + "-" + numFiles;
        			mLogger.log( "Saving " + fileName );
        			s3.putObject(new PutObjectRequest(mBucketName, fileName, createDataFile( itemsToStore )));
        			
        			itemsToStore.clear();
        			count = 0;
        		}
        	}
        	
        	// write any residual data to one last file
        	if ( count > 0 ) {
    			numFiles++;
    			fileName = fileKeyBase + "-" + numFiles;
    			mLogger.log( "Saving " + fileName );
    			s3.putObject(new PutObjectRequest(mBucketName, fileName, createDataFile( itemsToStore )));
        	}
        	
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            //System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
			mLogger.log("Error Message: " + ase.getMessage());
            return ERROR;
            
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
			mLogger.log("Error Message: " + ace.getMessage());
            return ERROR;
            
        } catch (IOException ioe ) {
        	mLogger.log("IOException: " + ioe.toString() );
        	return ERROR;
        	
        }

        return numFiles;
    }
    
    
    /**
     * Creates the actual content to write to a file.
     * 
     * @param items
     * 			the data of the projects to include in the file
     * 
     * @return a File object that can be used to store a file to S3
     * 
     * @throws IOException
     */
    private File createDataFile(List<ProjectInfo> items) throws IOException {
    	
		File file = File.createTempFile("temp", ".txt");
        file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		Gson gson = new Gson();
		
		for ( ProjectInfo item: items ) {
			writer.write(gson.toJson( item ) + "\n" );
		}
		
		writer.close();
		
        return file;
    }

    
    /**
     * Retrieves the only item in the DynamoDB table called SotipConfig
     * 
     * @return an object representing the contents of the item
     */
    private SotipConfig getConfig() {
		DynamoDBMapper mapper = new DynamoDBMapper(mClient);
		SotipConfig cfg = mapper.load(SotipConfig.class, SotipConfig.CONFIG_KEY);
    	return cfg;
    }
    
    
    /**
     * Updates the item in the DynamoDB table called SotipConfig.  The current date is set as the date for
     * the last successful for IT Dashboard.  The number of files is also stored.
     * 
     * Updating the item will trigger another Lambda function to begin processing the files that this
     * Lambda function created.
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
