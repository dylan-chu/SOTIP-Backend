package info.circlespace.sotip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import info.circlespace.sotip.api.BizCaseInfo;
import info.circlespace.sotip.api.BizCasesApiRes;
import info.circlespace.sotip.api.ItDashboardApiSvc;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ItDashBizCaseDataUpdater implements RequestHandler<Object, Object> {

	public static final String CONFIG_KEY = "SotipPrjs";
	
	final int EXPECTED_DATE_LEN = 10;
    String mDateLimitFmt = "yyyy-MM-dd";
    SimpleDateFormat mDateLimitFmtr = new SimpleDateFormat( mDateLimitFmt );
    String mToday;
    
	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;

	
	@Override
    public Object handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);

		Calendar today = GregorianCalendar.getInstance();
		mToday = mDateLimitFmtr.format( today.getTime() );

    	Map<String, InvestmentItem> existingItems = scanTbl();
		List<InvestmentItem> items = pullFromBizCasesDataFeed( existingItems );
		putInvestmentsInTbl( items );
		
        return null;
    }

	
	private Map<String, InvestmentItem> scanTbl() {
		Map<String, InvestmentItem> items = new HashMap<String, InvestmentItem>();
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
		

			for (Map<String, AttributeValue> item : result.getItems()) {
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
				String updDate = item.get( InvestmentItem.COL_UPD_DATE ).getS();
	
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
				
				items.put(itm.getUii(), itm);
			}
			
			lastKey = result.getLastEvaluatedKey();
			if ( lastKey == null ) 
				break;
			
		} while ( !lastKey.isEmpty() );

		return items;
	}

	
	private List<InvestmentItem> pullFromBizCasesDataFeed(
			Map<String, InvestmentItem> existing ) {

		List<InvestmentItem> items = new ArrayList<InvestmentItem>();

		OkHttpClient okClient = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS)
				.writeTimeout(60, TimeUnit.SECONDS)
				.build();

		Retrofit retro = new Retrofit.Builder()
				.baseUrl(ItDashboardApiSvc.BASE_URL)
				.client(okClient)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		ItDashboardApiSvc.BizCasesApi dataSvc = retro.create(ItDashboardApiSvc.BizCasesApi.class);
		Call<BizCasesApiRes> call = dataSvc.getBizCases();

		try {
			Response<BizCasesApiRes> response = call.execute();
			BizCasesApiRes apiRes = response.body();
			
			if (apiRes == null) {
				return items;
			}

			List<BizCaseInfo> cases = apiRes.getResult();
			
			if (cases.size() == 0) {
				return items;
			}

			for ( BizCaseInfo bizCase: cases ) {
				String dataDateStr = mToday;
				
				if ( bizCase.getDateOfLastInvestmentDetailUpdate().length() >= EXPECTED_DATE_LEN )
					dataDateStr = dataDateStr.substring( 0, EXPECTED_DATE_LEN );
				
				InvestmentItem invmt = existing.get( bizCase.getUniqueInvestmentIdentifier() );
				if ( invmt == null ) {
					invmt = new InvestmentItem( bizCase.getUniqueInvestmentIdentifier() );
					invmt.setUd( dataDateStr );
				}
				
				Date dataDate = mDateLimitFmtr.parse( dataDateStr );
				Date itemDate = mDateLimitFmtr.parse( invmt.getUd() );
				if ( dataDate.before( itemDate ))
					continue;
				
				invmt.setAc( bizCase.getAgencyCode() );
				invmt.setIt( bizCase.getInvestmentTitleITPortfolio() );
				invmt.setCio( bizCase.getEvaluationByAgencyCIO() );
				invmt.setSum( bizCase.getBriefSummary() );
				invmt.setUd( dataDateStr );
				items.add( invmt );
			}
			
		} catch (Exception e) {
			System.out.println("api call failed: " + e.toString());

		}

		return items;
	}

	
    private void putInvestmentsInTbl(List<InvestmentItem> items) {
		Table table = mDynamoDB.getTable(InvestmentItem.TBL_NAME);
		
		for (InvestmentItem item : items) {
			Item tblItem = new Item()
					.withPrimaryKey(InvestmentItem.COL_ID, item.getUii() )
					.withString(InvestmentItem.COL_AGENCY_CODE, item.getAc())
					.withString(InvestmentItem.COL_TITLE, item.getIt())
					.withString(InvestmentItem.COL_SUMMARY, item.getSum())
					.withNumber(InvestmentItem.COL_CIO_RATING, item.getCio())
					.withNumber(InvestmentItem.COL_NUM_PROJS, item.getNp())
					.withNumber(InvestmentItem.COL_LIFECYCLE_COST, item.getLcc())
					.withStringSet(InvestmentItem.COL_CONTRACTORS, item.getC())
					.withStringSet(InvestmentItem.COL_CONTRACT_TYPES, item.getCt())
					.withStringSet(InvestmentItem.COL_URLS, item.getUrls())
					.withString(InvestmentItem.COL_UPD_DATE, item.getUd() );

			PutItemOutcome outcome = table.putItem(tblItem);
		}
	}
    
}
