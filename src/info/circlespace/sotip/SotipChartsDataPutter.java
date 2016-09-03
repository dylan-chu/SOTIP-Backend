package info.circlespace.sotip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SotipChartsDataPutter implements RequestHandler<Object, Object> {

	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;
	String mTodaysDate;
	
	
	@Override
    public Object handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);
		
        Calendar now = GregorianCalendar.getInstance();
		mTodaysDate = sdf.format( now.getTime() );
				
		List<ProjectItem> items = readFromProjectsTbl();
		sumProjsInTbl( items );
		
        return null;
    }

	
	private List<ProjectItem> readFromProjectsTbl() {
		List<ProjectItem> items = new ArrayList<ProjectItem>();
		Map<String, AttributeValue> lastKey = null;
		ScanRequest scanRequest;
		
		do {
			if ( lastKey == null)
				scanRequest = new ScanRequest().withTableName(ProjectItem.TBL_NAME);
			else {
				scanRequest = new ScanRequest().withTableName(ProjectItem.TBL_NAME)
						.addExclusiveStartKeyEntry( ProjectItem.COL_ID, 
								lastKey.get(ProjectItem.COL_ID));
			}
				
			ScanResult result = mClient.scan(scanRequest);
			
			for (Map<String, AttributeValue> item : result.getItems()) {				
				String id = ((AttributeValue) item.get(ProjectItem.COL_ID)).getN();
				String agencyCode = ((AttributeValue) item.get(ProjectItem.COL_AGENCY_CODE)).getS();
				//String uniqInvmtId = ((AttributeValue) item.get(ProjectItem.COL_INVESTMENT_ID)).getS();
				//String invmtTitle = ((AttributeValue) item.get(ProjectItem.COL_INVESTMENT_TITLE)).getS();
				//String name = ((AttributeValue) item.get(ProjectItem.COL_NAME)).getS();
				//String objectives = ((AttributeValue) item.get(ProjectItem.COL_OBJECTIVES)).getS();
				String projPerf = ((AttributeValue) item.get(ProjectItem.COL_PROJ_PERF)).getN();
				String status = ((AttributeValue) item.get(ProjectItem.COL_STATUS)).getN();
				//String startDate = ((AttributeValue) item.get(ProjectItem.COL_START_DATE)).getS();
				//String cmpltdDate = ((AttributeValue) item.get(ProjectItem.COL_CMPLTN_DATE)).getS();
				String schVarColor = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_CATEG)).getN();
				//String schVarDays = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_DAYS)).getN();
				//String schVarPerc = ((AttributeValue) item.get(ProjectItem.COL_SCH_VAR_PERC)).getN();
				//String lifecycleCost = ((AttributeValue) item.get(ProjectItem.COL_LIFECYCLE_COST)).getN();
				String costVarColor = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_CATEG)).getN();
				//String costVarDollars = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_AMT)).getN();
				//String costVarPerc = ((AttributeValue) item.get(ProjectItem.COL_COST_VAR_PERC)).getN();
				String pmExpLvl = ((AttributeValue) item.get(ProjectItem.COL_PM_EXP_LVL)).getN();
				String sdlcMethod = ((AttributeValue) item.get(ProjectItem.COL_SDLC_METHOD)).getN();
				//String otherSdm = ((AttributeValue) item.get(ProjectItem.COL_OTHER_SDM)).getS();
				//String updDate = ((AttributeValue) item.get(ProjectItem.COL_UPDATED_DATE)).getS();
				
				ProjectItem itm = new ProjectItem( Integer.parseInt(id) );
				itm.setAc( agencyCode );
				//itm.setUii( uniqInvmtId );
				//itm.setIt( invmtTitle );
				//itm.setName(name);
				//itm.setObj( objectives );
				itm.setPp( Integer.parseInt( projPerf ));
				itm.setPs( Integer.parseInt( status ));
				//itm.setSd( startDate );
				//itm.setCd( cmpltdDate );
				itm.setSv( Integer.parseInt( schVarColor ));
				//itm.setSvd( Integer.parseInt( schVarDays ));
				//itm.setSvp( Double.parseDouble( schVarPerc ));
				//itm.setLcc( Double.parseDouble( lifecycleCost ));
				itm.setCv( Integer.parseInt( costVarColor ));
				//itm.setCvd( Double.parseDouble( costVarDollars ));
				//itm.setCvp( Double.parseDouble( costVarPerc ));
				itm.setPm( Integer.parseInt( pmExpLvl ) );
				itm.setSdm( Integer.parseInt(sdlcMethod) );
				//itm.setOsdm( otherSdm );
				//itm.setUd( updDate );
				
				items.add(itm);
			}
			
			lastKey = result.getLastEvaluatedKey();
			if ( lastKey == null ) 
				break;
			
		} while ( !lastKey.isEmpty() );
		
		return items;
	}

	
	private void sumProjsInTbl(List<ProjectItem> items) {
		VarianceDataSet costVarData = new VarianceDataSet();
		VarianceDataSet schVarData = new VarianceDataSet();
		PerformanceDataSet cmpltdPerfData = new PerformanceDataSet();
		PerformanceDataSet inProgPerfData = new PerformanceDataSet();
		GroupedDataSet pmLvlData = new GroupedDataSet(9);
		GroupedDataSet sdlcMethodData = new GroupedDataSet( 6 );
	
		
		for ( ProjectItem item: items ) {
			String agencyCode = item.getAc();
			
			costVarData.addToTally( item.getCv() );
			costVarData.addAgency( item.getCv(), agencyCode);
			
			schVarData.addToTally( item.getSv() );
			schVarData.addAgency( item.getSv(), agencyCode);
			
			int perfNdx = item.getPp();
			
			if ( item.getPs() == 100 ) {
				cmpltdPerfData.addToTally( perfNdx );
				cmpltdPerfData.addAgency( perfNdx, agencyCode);
			} else {
				inProgPerfData.addToTally( perfNdx );
				inProgPerfData.addAgency( perfNdx, agencyCode);
			}
			
			pmLvlData.addToTally( item.getPm()-1, perfNdx);
			pmLvlData.addAgency( item.getPm()-1, perfNdx, agencyCode );
			
			if ( item.getSdm() < 7 ) {
				sdlcMethodData.addToTally( item.getSdm()-1, perfNdx);
				sdlcMethodData.addAgency( item.getSdm()-1, perfNdx, agencyCode);
			}
		}
		
		int chartNdx = 1;
		writeChartSumData( costVarData, chartNdx++ );
		writeChartSumData( schVarData, chartNdx++ );
		writeChartSumData( cmpltdPerfData, chartNdx++ );
		writeChartSumData( inProgPerfData, chartNdx++ );
		writeChartSumData( pmLvlData, chartNdx++ );
		writeChartSumData( sdlcMethodData, chartNdx++ );
	}

	
	private void writeChartSumData( SotipDataSet dataSet, int chartType ) {
		
		Table table = mDynamoDB.getTable( ChartItem.TBL_NAME );
		
		Item tblItem = new Item()
				.withPrimaryKey(ChartItem.COL_TYPE, chartType )
				.withString( ChartItem.COL_DATA, dataSet.getDataAsStr() )
				.withString( ChartItem.COL_AGENCIES, dataSet.getAgenciesAsStr() )
				.withString( ChartItem.COL_UPD_DATE, mTodaysDate );

		PutItemOutcome outcome = table.putItem(tblItem);
	}
	
}
