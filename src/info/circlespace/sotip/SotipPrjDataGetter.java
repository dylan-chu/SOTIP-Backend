/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This Lambda function returns the data in the SotipPrjs DynamoDB table. It accepts a date as input
 * and returns all items in the table which has data that was updated on or after this date.  The triggered
 * by an API call.
 */
public class SotipPrjDataGetter implements RequestHandler<SotipLbdFnInput, SotipLbdFnOutput> {

	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;
	SimpleDateFormat mDateFmtr = new SimpleDateFormat( "yyyy-MM-dd" );

	
    @Override
    public SotipLbdFnOutput handleRequest(SotipLbdFnInput input, Context context) {
        context.getLogger().log("Input: " + input);

        // point the Amazon client to the right region
		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);

		Date dateToCompare;
		
		// parse the input date
		try {
			dateToCompare = mDateFmtr.parse( input.getDate() );
		} catch (ParseException pe) {
			// if the input date cannot be parsed, use a default date of a hundred years ago
			Calendar now = GregorianCalendar.getInstance();
			now.add( Calendar.YEAR, -100 );
			dateToCompare = now.getTime();
		}

		// scan the DynamoDB table with a date parameter
		List<ProjectItem> items = scanTbl( dateToCompare );
		
        return new SotipLbdFnOutput( items );
    }

    
    /**
     * Returns items in the DynamoDB table for IT projects.  Items with data updated on or after a certain
     * date is returned.
     *   
     * @param dateLimit
     * 			the date to compare with the updated date of the items
     *  
     * @return a list of project records
     */
	private List<ProjectItem> scanTbl( Date dateLimit ) {
		List<ProjectItem> items = new ArrayList<ProjectItem>();
		Map<String, AttributeValue> lastKey = null;
		ScanRequest scanRequest;
		
		do {
			// a scan returns at most 1 MB of data
			if ( lastKey == null)
				scanRequest = new ScanRequest().withTableName(ProjectItem.TBL_NAME);
			else {
				// to get all the data, need to pass along the key to start the next scan with
				scanRequest = new ScanRequest().withTableName(ProjectItem.TBL_NAME)
						.addExclusiveStartKeyEntry( ProjectItem.COL_ID, 
								lastKey.get(ProjectItem.COL_ID));
			}
				
			ScanResult result = mClient.scan(scanRequest);
			Date itemDate;
			
			for (Map<String, AttributeValue> item : result.getItems()) {
				String updDate = ((AttributeValue) item.get(ProjectItem.COL_UPDATED_DATE)).getS();
				
				try {
					itemDate = mDateFmtr.parse( updDate );
				} catch ( ParseException pe ) {
					itemDate = new Date();
				}
				
				// exclude all items updated before the input date
				if ( itemDate.before( dateLimit ) )
					continue;
				
				String id = ((AttributeValue) item.get(ProjectItem.COL_ID)).getN();
				String agencyCode = ((AttributeValue) item.get(ProjectItem.COL_AGENCY_CODE)).getS();
				String uniqInvmtId = ((AttributeValue) item.get(ProjectItem.COL_INVESTMENT_ID)).getS();
				String invmtTitle = ((AttributeValue) item.get(ProjectItem.COL_INVESTMENT_TITLE)).getS();
				String name = ((AttributeValue) item.get(ProjectItem.COL_NAME)).getS();
				String objectives = ((AttributeValue) item.get(ProjectItem.COL_OBJECTIVES)).getS();
				String projPerf = ((AttributeValue) item.get(ProjectItem.COL_PROJ_PERF)).getN();
				String status = ((AttributeValue) item.get(ProjectItem.COL_STATUS)).getN();
				String startDate = ((AttributeValue) item.get(ProjectItem.COL_START_DATE)).getS();
				String cmpltdDate = ((AttributeValue) item.get(ProjectItem.COL_CMPLTN_DATE)).getS();
				String schVarColor = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_CATEG)).getN();
				String schVarDays = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_DAYS)).getN();
				String schVarPerc = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_PERC)).getN();
				String lifecycleCost = ((AttributeValue) item.get(ProjectItem.COL_LIFECYCLE_COST)).getN();
				String costVarColor = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_CATEG)).getN();
				String costVarDollars = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_AMT)).getN();
				String costVarPerc = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_PERC)).getN();
				String pmExpLvl = ((AttributeValue) item.get(ProjectItem.COL_PM_EXP_LVL)).getN();
				String sdlcMethod = ((AttributeValue) item.get(ProjectItem.COL_SDLC_METHOD)).getN();
				String otherSdm = ((AttributeValue) item.get(ProjectItem.COL_OTHER_SDM)).getS();
				
				ProjectItem itm = new ProjectItem( Integer.parseInt(id) );
				itm.setAc( agencyCode );
				itm.setUii( uniqInvmtId );
				itm.setIt( invmtTitle );
				itm.setName( name );
				itm.setObj( objectives );
				itm.setPp( Integer.parseInt( projPerf ));
				itm.setPs( Integer.parseInt( status ));
				itm.setSd( startDate );
				itm.setCd( cmpltdDate );
				itm.setSv( Integer.parseInt( schVarColor ));
				itm.setSvd( Integer.parseInt( schVarDays ));
				itm.setSvp( Double.parseDouble( schVarPerc ));
				itm.setLcc( Double.parseDouble( lifecycleCost ));
				itm.setCv( Integer.parseInt( costVarColor ));
				itm.setCvd( Double.parseDouble( costVarDollars ));
				itm.setCvp( Double.parseDouble( costVarPerc ));
				itm.setPm( Integer.parseInt( pmExpLvl ) );
				itm.setSdm( Integer.parseInt( sdlcMethod ) );
				itm.setOsdm( otherSdm );
				itm.setUd( updDate );
				
				items.add(itm);
			}
			
			// extract the key with which to start the next scan
			lastKey = result.getLastEvaluatedKey();
			if ( lastKey == null ) 
				break;
			
		} while ( !lastKey.isEmpty() );
		
		return items;
	}

}
