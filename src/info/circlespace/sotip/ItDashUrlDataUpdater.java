package info.circlespace.sotip;

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

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import info.circlespace.sotip.api.ItDashboardApiSvc;
import info.circlespace.sotip.api.UrlInfo;
import info.circlespace.sotip.api.UrlsApiRes;
import info.circlespace.sotip.api.Utility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ItDashUrlDataUpdater implements RequestHandler<Object, Object> {

	public static final String CONFIG_KEY = "SotipPrjs";

    String mDateLimitFmt = "yyyy-MM-dd";
    SimpleDateFormat mDateLimitFmtr = new SimpleDateFormat( mDateLimitFmt );

    AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;

	
	@Override
    public Object handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);
		
    	SotipConfig cfg = getConfig();
    	Date lastUpdDate;
    	
    	try {
    		lastUpdDate = mDateLimitFmtr.parse( cfg.getLastUpdDate() );
    	} catch (ParseException pe) {
    		Calendar today = GregorianCalendar.getInstance();
    		lastUpdDate = today.getTime();
    		lastUpdDate.setYear(1900);
    	}

		Map<String, InvestmentItem> existingItems = scanTbl();
		List<InvestmentItem> items = pullFromUrlsDataFeed( existingItems, lastUpdDate );
		putInvestmentsInTbl( items );

		return null;
    }

    
	private List<InvestmentItem> pullFromUrlsDataFeed(
			Map<String, InvestmentItem> exists, Date dateLimit ) {
		
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

		ItDashboardApiSvc.UrlsApi dataSvc = retro.create(ItDashboardApiSvc.UrlsApi.class);
		Call<UrlsApiRes> call = dataSvc.getUrls();

		try {
			Response<UrlsApiRes> response = call.execute();
			UrlsApiRes apiRes = response.body();
			if (apiRes == null) {
				System.out.println("response is null");
				return items;
			}

			List<UrlInfo> urls = apiRes.getResult();
			if (urls.size() == 0) {
				System.out.println("0 urls");
				return items;
			} else {
				System.out.println( urls.size() + " urls" );
			}

			for ( UrlInfo url: urls ) {
				InvestmentItem invmt = exists.get( url.getUniqueInvestmentIdentifier() );
				if ( invmt == null ) {
				//	invmt = new InvestmentItem( url.getUniqueInvestmentIdentifier() );
				//	exists.put( invmt.getUii(), invmt );
					continue;
				}
				
				Date itemDate = mDateLimitFmtr.parse( invmt.getUd() );
				if ( itemDate.before( dateLimit ))
					continue;

				invmt.addUrl( Utility.transformNull(
					url.getInvestmentURL() ));
			}
		} catch (Exception e) {
			System.out.println("api call failed: " + e.toString());
			
		}

		for ( String key: exists.keySet()) {
			items.add( exists.get(key));
		}
		
		return items;	
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

	
    private SotipConfig getConfig() {
		DynamoDBMapper mapper = new DynamoDBMapper(mClient);
		SotipConfig cfg = mapper.load(SotipConfig.class, CONFIG_KEY);
    	return cfg;
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
