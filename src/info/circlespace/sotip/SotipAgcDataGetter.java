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


public class SotipAgcDataGetter implements RequestHandler<SotipLbdFnInput, SotipLbdFnOutput> {

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
		
		List<AgencyItem> items = scanTbl( dateToCompare );
		
		return new SotipLbdFnOutput(items);
    }


	private List<AgencyItem> scanTbl(Date dateLimit) {
    	List<AgencyItem> items = new ArrayList<AgencyItem>();
    	
		ScanRequest scanRequest = new ScanRequest().withTableName(AgencyItem.TBL_NAME);
		ScanResult result = mClient.scan(scanRequest);
		Date itemDate;

		for (Map<String, AttributeValue> item : result.getItems()) {
			
			String updDate = item.get( AgencyItem.COL_UPD_DATE ).getS();
			try {
				itemDate = sdf.parse( updDate );
			} catch ( ParseException pe ) {
				itemDate = new Date();
			}
			
			if ( itemDate.before( dateLimit ) )
				continue;
			
			String code = item.get(AgencyItem.COL_CODE).getS();
			String name = item.get(AgencyItem.COL_NAME).getS();
			String mainUrl = item.get(AgencyItem.COL_MAIN_URL).getS();
			String contactOnline = item.get(AgencyItem.COL_E_CONTACT).getS();
			String phoneNbr = item.get(AgencyItem.COL_PHONE_NBR).getS();
			String mailAddr = item.get(AgencyItem.COL_MAIL_ADDR).getS();
			String lat = item.get( AgencyItem.COL_LATITUDE ).getN();
			String lng = item.get( AgencyItem.COL_LONGITUDE ).getN();

			AgencyItem itm = new AgencyItem(code);
			itm.setNm( name );
			itm.setMu(mainUrl);
			itm.setEc( contactOnline );
			itm.setPn( phoneNbr );
			itm.setMa( mailAddr );
			itm.setLat( Double.parseDouble( lat ));
			itm.setLng( Double.parseDouble( lng ));
			itm.setUd( updDate );
			
			items.add(itm);
		}

		return items;
    }
		
}
