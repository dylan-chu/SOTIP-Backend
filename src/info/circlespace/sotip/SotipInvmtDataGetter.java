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


public class SotipInvmtDataGetter implements RequestHandler<SotipLbdFnInput, SotipLbdFnOutput> {

	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;
	SimpleDateFormat mDateFmtr = new SimpleDateFormat( "yyyy-MM-dd" );

	
	@Override
    public SotipLbdFnOutput handleRequest(SotipLbdFnInput input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);

		Calendar now = GregorianCalendar.getInstance();
		Date dateToCompare;
		
		try {
			dateToCompare = mDateFmtr.parse(input.getDate());
		} catch (ParseException pe) {
			now.add( Calendar.YEAR, -100 );
			dateToCompare = now.getTime();
		}

		List<InvestmentItem> items = scanTbl( dateToCompare );
		
        return new SotipLbdFnOutput( items );
    }

	
	private List<InvestmentItem> scanTbl(Date dateLimit) {
		List<InvestmentItem> items = new ArrayList<InvestmentItem>();
		Map<String, AttributeValue> lastKey = null;
		ScanRequest scanRequest;
		
		do {
			if ( lastKey == null)
				scanRequest = new ScanRequest().withTableName(InvestmentItem.TBL_NAME);
			else {
				scanRequest = new ScanRequest().withTableName(InvestmentItem.TBL_NAME)
						.addExclusiveStartKeyEntry(InvestmentItem.COL_ID, lastKey.get(InvestmentItem.COL_ID));
			}
				
			ScanResult result = mClient.scan(scanRequest);
			Date itemDate;		

			for (Map<String, AttributeValue> item : result.getItems()) {
				String updDate = item.get( InvestmentItem.COL_UPD_DATE ).getS();
				try {
					itemDate = mDateFmtr.parse( updDate );
				} catch ( ParseException pe ) {
					itemDate = new Date();
				}
				
				if ( itemDate.before( dateLimit ) )
					continue;

				String id = item.get(InvestmentItem.COL_ID).getS();
				String agencyCode = item.get(InvestmentItem.COL_AGENCY_CODE).getS();
				String title = item.get(InvestmentItem.COL_TITLE).getS();
				String summary = item.get(InvestmentItem.COL_SUMMARY).getS();
				String cioRating = item.get(InvestmentItem.COL_CIO_RATING).getN();
				String numProjs = item.get( InvestmentItem.COL_NUM_PROJS ).getN();
				String lifecycleCost = item.get( InvestmentItem.COL_LIFECYCLE_COST ).getN();
				List<String> contractors = item.get( InvestmentItem.COL_CONTRACTORS).getSS();
				List<String> contractTypes = item.get( InvestmentItem.COL_CONTRACT_TYPES).getSS();
				List<String> urls = item.get( InvestmentItem.COL_URLS ).getSS();
	
				InvestmentItem itm = new InvestmentItem(id);
				itm.setAc( agencyCode );
				itm.setIt(title);
				itm.setSum( summary );
				itm.setCio( Integer.parseInt( cioRating) );
				itm.setNp( Integer.parseInt( numProjs ));
				itm.setLcc( Double.parseDouble( lifecycleCost ));
				itm.addContractors( contractors );
				itm.addContractTypes( contractTypes );
				itm.addUrls( urls );
				itm.setUd( updDate );
				
				items.add(itm);
			}
			
			lastKey = result.getLastEvaluatedKey();
			
			if ( lastKey == null ) 
				break;
		
		} while ( !lastKey.isEmpty() );

		return items;
	}
	
}
