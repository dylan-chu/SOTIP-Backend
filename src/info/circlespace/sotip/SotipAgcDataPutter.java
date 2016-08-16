package info.circlespace.sotip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SotipAgcDataPutter implements RequestHandler<Object, Object> {

	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
	AmazonDynamoDBClient mClient;
	DynamoDB mDynamoDB;

	
	@Override
    public Object handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

		mClient = new AmazonDynamoDBClient();
		mClient.withRegion(Regions.US_EAST_1);
		mDynamoDB = new DynamoDB(mClient);

		List<AgencyItem> items = populateDataItems();
		populateTbl( items );
		
        return null;
    }


	private void populateTbl( List<AgencyItem> items ) {
		TableWriteItems writeItems = new TableWriteItems(AgencyItem.TBL_NAME);
		int count = 0;
		List<Item> itms = new ArrayList<Item>();
		
		Calendar now = GregorianCalendar.getInstance();
		String today = sdf.format( now.getTime() );
		
		for (AgencyItem item : items) {
			Item tblItem = new Item()
					.withPrimaryKey(AgencyItem.COL_CODE, item.getAc() )
					.withString(AgencyItem.COL_NAME, item.getNm())
					.withString(AgencyItem.COL_MAIN_URL, item.getMu())
					.withString(AgencyItem.COL_E_CONTACT, item.getEc())
					.withString(AgencyItem.COL_PHONE_NBR, item.getPn())
					.withString(AgencyItem.COL_MAIL_ADDR, item.getMa())
					.withNumber(AgencyItem.COL_LATITUDE, item.getLat())
					.withNumber(AgencyItem.COL_LONGITUDE, item.getLng())
					.withString(AgencyItem.COL_UPD_DATE, today );

			itms.add( tblItem );
			count++;
			
			if ( count >= 5 ) {
				writeItems.withItemsToPut( itms );
				BatchWriteItemOutcome outcome = mDynamoDB.batchWriteItem(writeItems);
				itms.clear();
				count = 0;
				
				Map<String,List<WriteRequest>> unprocessed = outcome.getUnprocessedItems();
				System.out.println( "did not process " + unprocessed.size() + " results");
			}
		}
		
		writeItems.withItemsToPut( itms );
		BatchWriteItemOutcome outcome = mDynamoDB.batchWriteItem(writeItems);
		
	}
	
	
	private List<AgencyItem> populateDataItems() {
		List<AgencyItem> items = new ArrayList<AgencyItem>();
		
		AgencyItem item = new AgencyItem( "005" );
		item.setNm( "U.S. Department of Agriculture" );
		item.setMu( "http://www.usda.gov" );
		item.setEc( "Ask the Expert;" +
				"http://www.usda.gov/wps/portal/usda/usdahome?navid=ASK_EXPERT2" );
		item.setPn( "(202) 720-2791" );
		item.setMa( "1400 Independence Avenue, SW;" 
				+ "Washington, DC 20250" );
		item.setLat( 38.886960 );
		item.setLng( -77.029945 );
		items.add( item );
		
		item = new AgencyItem( "006" );
		item.setNm( "U.S. Department of Commerce" );
		item.setMu( "http://www.commerce.gov" );
		item.setEc( "Email;" +
				"TheSec@doc.gov" );
		item.setPn( "(202) 482-2000" );
		item.setMa( "1401 Constitution Avenue, NW;" 
				+ "Washington, D.C. 20230" );
		item.setLat( 38.893101 );
		item.setLng( -77.032751 );
		items.add( item );
		
		item = new AgencyItem( "007" );
		item.setNm( "U.S. Department of Defense" );
		item.setMu( "http://www.defense.gov" );
		item.setEc( "Contact Info;" +
				"http://www.defense.gov/Contact" );
		item.setPn( "1-703-571-3343" );
		item.setMa( "1400 Defense Pentagon;" 
				+ "Washington, DC 20301-1400" );
		item.setLat( 38.872066 );
		item.setLng( -77.056315 );
		items.add( item );
		
		item = new AgencyItem( "009" );
		item.setNm( "U.S. Department of Health and Human Services" );
		item.setMu( "http://www.hhs.gov" );
		item.setEc( "HHS Heads of Contracting Activity (HCA) and Key Managers;" +
				"http://www.hhs.gov/grants/grants-business-contacts/hca-and-key-managers/index.html" );
		item.setPn( "1-877-696-6775" );
		item.setMa( "200 Independence Avenue, SW;" 
				+ "Washington, DC 20201" );
		item.setLat( 38.886811 );
		item.setLng( -77.014434 );
		items.add( item );
		
		item = new AgencyItem( "010" );
		item.setNm( "U.S. Department of the Interior" );
		item.setMu( "http://www.doi.gov" );
		item.setEc( "Email;" +
				"feedback@ios.doi.gov" );
		item.setPn( "(202) 208-3100" );
		item.setMa( "1849 C Street, NW;" 
				+ "Washington, DC 20240" );
		item.setLat( 38.894632 );
		item.setLng( -77.042587 );
		items.add( item );
		
		item = new AgencyItem( "011" );
		item.setNm( "U.S. Department of Justice" );
		item.setMu( "http://www.justice.gov" );
		item.setEc( "Email;" +
				"AskDOJ@usdoj.gov" );
		item.setPn( "(202) 514-2000" );
		item.setMa( "950 Pennsylvania Avenue, NW;" 
				+ "Washington, DC 20530-0001" );
		item.setLat( 38.893340 );
		item.setLng( -77.024959 );
		items.add( item );
		
		item = new AgencyItem( "012" );
		item.setNm( "U.S. Department of Labor" );
		item.setMu( "http://www.dol.gov" );
		item.setEc( "Contact By E-mail Info;" +
				"https://www.dol.gov/general/contact/contact-email" );
		item.setPn( "1-866-487-2365" );
		item.setMa( "200 Constitution Avenue, NW;" 
				+ "Washington, DC 20210" );
		item.setLat( 38.893313 );
		item.setLng( -77.014358 );
		items.add( item );
		
		item = new AgencyItem( "014" );
		item.setNm( "U.S. Department of State" );
		item.setMu( "http://www.state.gov" );
		item.setEc( "Contact Request Form;" + 
				"https://register.state.gov/contactus/contactusform" );
		item.setPn( "(202) 647-4000" );
		item.setMa( "2201 C Street, NW;" 
				+ "Washington, DC 20520" );
		item.setLat( 38.894664 );
		item.setLng( -77.048384 );
		items.add( item );
		
		item = new AgencyItem( "015" );
		item.setNm( "U.S. Department of the Treasury" );
		item.setMu( "http://www.treasury.gov" );
		item.setEc( "Send a Message to the Secretary;" + 
				"https://www.treasury.gov/connect/Pages/Message-to-the-Secretary.aspx" );
		item.setPn( "(202) 622-2000" );
		item.setMa( "1500 Pennsylvania Avenue, NW;" 
				+ "Washington, DC 20220" );
		item.setLat( 38.897723 );
		item.setLng( -77.034359 );
		items.add( item );
		
		item = new AgencyItem( "016" );
		item.setNm( "The United States Social Security Administration" );
		item.setMu( "https://www.ssa.gov" );
		item.setEc( "Comments and Questions;" + 
				"https://faq.ssa.gov/ics/support/ticketnewwizard.asp?style=classic" );
		item.setPn( "1-800-772-1213" );
		item.setMa( "Social Security Administration;"
				+ "Office of Public Inquiries"
				+ "1100 West High Rise"
				+ "6401 Security Blvd."
				+ "Baltimore, MD 21235" );
		item.setLat( 39.309897 );
		item.setLng( -76.730710 );
		items.add( item );
		
		item = new AgencyItem( "018" );
		item.setNm( "U.S. Department of Education" );
		item.setMu( "https://www.ed.gov" );
		item.setEc( "Ask a Question;" + 
				"http://answers.ed.gov/" );
		item.setPn( "1-800-872-5327" );
		item.setMa( "400 Maryland Avenue, SW;"
				+ "Washington, DC 20202" );
		item.setLat( 38.886665 );
		item.setLng( -77.018714 );
		items.add( item );
		
		item = new AgencyItem( "019" );
		item.setNm( "U.S. Department of Energy" );
		item.setMu( "http://energy.gov" );
		item.setEc( "Email;" + 
				"the.secretary@hq.doe.gov" );
		item.setPn( "(202) 586-5000" );
		item.setMa( "1000 Independence Avenue, SW;"
				+ "Washington, DC 20585" );
		item.setLat( 38.887200 );
		item.setLng( -77.025569 );
		items.add( item );
		
		item = new AgencyItem( "020" );
		item.setNm( "U.S. Environmental Protection Agency" );
		item.setMu( "https://www.epa.gov" );
		item.setEc( "Ask a Question;" + 
				"https://publicaccess.zendesk.com/hc/en-us/requests/new" );
		item.setPn( "(202) 272-0167" );
		item.setMa( "1200 Pennsylvania Avenue, NW;"
				+ "Washington, DC 20460" );
		item.setLat( 38.893424 );
		item.setLng( -77.029435 );
		items.add( item );
		
		item = new AgencyItem( "021" );
		item.setNm( "U.S. Department of Transportation" );
		item.setMu( "https://www.transportation.gov" );
		item.setEc( "Online Contact Form;" + 
				"https://ntl.custhelp.com/app/ask" );
		item.setPn( "1-855-368-4200" );
		item.setMa( "1200 New Jersey Avenue, SE;"
				+ "Washington, DC 20590" );
		item.setLat( 38.876039 );
		item.setLng( -77.002967 );
		items.add( item );
		
		item = new AgencyItem( "023" );
		item.setNm( "U.S. General Services Administration" );
		item.setMu( "http://www.gsa.gov" );
		item.setEc( "Online Contact Form;" + 
				"http://www.gsa.gov/portal/content/136438" );
		item.setPn( "1-800-488-3111" );
		item.setMa( "1800 F Street, NW;"
				+ "Washington, DC 20405" );
		item.setLat( 38.897164 );
		item.setLng( -77.042570 );
		items.add( item );
		
		item = new AgencyItem( "024" );
		item.setNm( "U.S Department of Homeland Security" );
		item.setMu( "https://www.dhs.gov" );
		item.setEc( "Contact Info;" +
				"http://www.dhs.gov/main-contact-us" );
		item.setPn( "(202) 282-8000" );
		item.setMa( "Mail Operations Program Manager;"
				+ "MGMT/CRSO/Mailstop 0075"
				+ "Department of Homeland Security"
				+ "245 Murray Lane, SW"
				+ "Washington, DC 20528-0075" );
		item.setLat( 38.851115 );
		item.setLng( -77.016467 );
		items.add( item );
		
		item = new AgencyItem( "025" );
		item.setNm( "U.S. Department of Housing and Urban Development" );
		item.setMu( "http://www.hud.gov" );
		item.setEc( "Contact Info;" + 
				"http://portal.hud.gov/hudportal/HUD?src=/contact" );
		item.setPn( "(202) 708-1112" );
		item.setMa( "451 7th Street, SW;"
				+ "Washington, DC 20410" );
		item.setLat( 38.884084 );
		item.setLng( -77.022731 );
		items.add( item );
		
		item = new AgencyItem( "026" );
		item.setNm( "National Aeronautics and Space Administration" );
		item.setMu( "https://www.nasa.gov" );
		item.setEc( "Email;" + 
				"public-inquiries@hq.nasa.gov" );
		item.setPn( "(202) 358-0001" );
		item.setMa( "NASA Headquarters;"
				+ "300 E. Street SW, Suite 5R30"
				+ "Washington, DC 20546" );
		item.setLat( 38.883103 );
		item.setLng( -77.016359 );
		items.add( item );
		
		item = new AgencyItem( "027" );
		item.setNm( "U.S. Office of Personnel Management" );
		item.setMu( "https://www.opm.gov" );
		item.setEc( "Submit a Question;" + 
				"https://www.opm.gov/faqs/submitquestion.aspx" );
		item.setPn( "(202) 606-1800" );
		item.setMa( "1900 E Street, NW;"
				+ "Washington, DC 20415-1000" );
		item.setLat( 38.894931 );
		item.setLng( -77.044034 );
		items.add( item );
		
		item = new AgencyItem( "028" );
		item.setNm( "U.S. Small Business Administration" );
		item.setMu( "https://www.sba.gov" );
		item.setEc( "Email;" + 
				"answerdesk@sba.gov" );
		item.setPn( "1-800-827-5722" );
		item.setMa( "409 3rd Street, SW;"
				+ "Washington, DC 20416" );
		item.setLat( 38.884838 );
		item.setLng( -77.016323 );
		items.add( item );
		
		item = new AgencyItem( "029" );
		item.setNm( "U.S. Department of Veternal Affairs" );
		item.setMu( "http://www.va.gov" );
		item.setEc( "Ask a Question;" + 
				"https://iris.custhelp.com/app/ask" );
		item.setPn( "1-800-827-1000" );
		item.setMa( "810 Vermont Avenue, NW;"
				+ "Washington, DC 20420" );
		item.setLat( 38.900777 );
		item.setLng( -77.035238 );
		items.add( item );
		
		item = new AgencyItem( "184" );
		item.setNm( "U.S. Agency for International Development" );
		item.setMu( "https://www.usaid.gov" );
		item.setEc( "Email;" + 
				"pinquiries@usaid.gov" );
		item.setPn( "(202) 712-4810" );
		item.setMa( "Ronald Reagan Building;"
				+ "Washington, DC 20523-1000" );
		item.setLat( 38.894055 );
		item.setLng( -77.031063 );
		items.add( item );
		
		item = new AgencyItem( "202" );
		item.setNm( "U.S. Army Corps of Engineers" );
		item.setMu( "http://www.usace.army.mil/" );
		item.setEc( "Email;" + 
				"hq-publicaffairs@usace.army.mil" );
		item.setPn( "(202) 761-0011" );
		item.setMa( "441 G Street, NW;"
				+ "Washington, DC 20314-1000" );
		item.setLat( 38.898956 );
		item.setLng( -77.017429 );
		items.add( item );
		
		item = new AgencyItem( "393" );
		item.setNm( "The U.S. National Archives and Records Administration" );
		item.setMu( "https://www.archives.gov" );
		item.setEc( "Ask a Question;" + 
				"https://www.archives.gov/contact/inquire-form.html#part-b" );
		item.setPn( "1-866-272-6272" );
		item.setMa( "8601 Adelphi Road;"
				+ "College Park, MD 20740-6001" );
		item.setLat( 39.001093 );
		item.setLng( -76.959375 );
		items.add( item );
		
		item = new AgencyItem( "422" );
		item.setNm( "National Science Foundation" );
		item.setMu( "http://www.nsf.gov" );
		item.setEc( "Email;" + 
				"info@nsf.gov" );
		item.setPn( "(703) 292-5111" );
		item.setMa( "4201 Wilson Boulevard;"
				+ "Arlington, Virginia 22230" );
		item.setLat( 38.880732 );
		item.setLng( -77.110967 );
		items.add( item );
		
		item = new AgencyItem( "429" );
		item.setNm( "U.S. Nuclear Regulatory Commission" );
		item.setMu( "http://www.nrc.gov" );
		item.setEc( "Contact the Office of Public Affairs;" + 
				"http://www.nrc.gov/about-nrc/public-affairs/contact-opa.html" );
		item.setPn( "1-800-368-5642" );
		item.setMa( "U.S. Nuclear Regulatory Commission;"
				+ "Washington, DC 20555-0001" );
		item.setLat( 39.046189 );
		item.setLng( -77.111180 );
		items.add( item );
		
		return items;
	}
}
