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


public class SotipChartsDataGetter implements RequestHandler<SotipLbdFnInput, SotipLbdFnOutput> {

	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;


	@Override
    public SotipLbdFnOutput handleRequest(SotipLbdFnInput input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);
    	
		Date dateToCompare = null;
		
		try {
			dateToCompare = sdf.parse( input.getDate() );
		} catch ( Exception e ) {
			Calendar now = GregorianCalendar.getInstance();
			now.add( Calendar.YEAR, -100 );
			dateToCompare = now.getTime();
		}

		List<ChartItem> items = scanTbl(dateToCompare);
        
        return new SotipLbdFnOutput( items );
    }

	
	private List<ChartItem> scanTbl(Date dateLimit) {
		List<ChartItem> items = new ArrayList<ChartItem>();

		ScanRequest scanRequest = new ScanRequest().withTableName(ChartItem.TBL_NAME);
		ScanResult result = mClient.scan(scanRequest);
		Date itemDate;
		
		for (Map<String, AttributeValue> item : result.getItems()) {
			String updDate = item.get( ChartItem.COL_UPD_DATE ).getS();
			try {
				itemDate = sdf.parse( updDate );
			} catch ( ParseException pe ) {
				itemDate = new Date();
			}
			
			if ( itemDate.before( dateLimit ) )
				continue;
			
			String type = ((AttributeValue) item.get(ChartItem.COL_TYPE)).getN();
			String data = ((AttributeValue) item.get(ChartItem.COL_DATA)).getS();
			String agcs = ((AttributeValue) item.get(ChartItem.COL_AGENCIES)).getS();
			
			items.add(new ChartItem( Integer.parseInt(type), data, agcs));
		}

		return items;
	}
}
