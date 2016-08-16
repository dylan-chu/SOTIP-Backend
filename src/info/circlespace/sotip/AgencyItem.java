package info.circlespace.sotip;


public class AgencyItem {

	public final static String TBL_NAME = "SotipAgcs";
	public final static String COL_CODE = "Code";
	public final static String COL_NAME = "Name";
	public final static String COL_MAIN_URL = "MainUrl";
	public final static String COL_E_CONTACT = "EContact";
	public final static String COL_PHONE_NBR = "PhoneNbr";
	public final static String COL_MAIL_ADDR = "MailAddr";
	public final static String COL_LATITUDE = "Latitude";
	public final static String COL_LONGITUDE = "Longitude";
	public final static String COL_UPD_DATE = "UpdDate";

	String mCode;
	String mName;
	String mMainUrl;
	String mContactOnline;
	String mPhoneNbr;
	String mMailAddr;
	double mLat;
	double mLong;
	String mUpdatedDate;
	
	
	public AgencyItem( String code ) {
		mCode = code;
	}
	
	
	public String getAc() {
		return mCode;
	}
	
	public void setAc( String code ) {
		mCode = code;
	}
	
	
	public String getNm() {
		return mName;
	}
	
	public void setNm( String name ) {
		mName = name;
	}
	
	
	public String getMu() {
		return mMainUrl;
	}
	
	public void setMu( String url ) {
		mMainUrl = url;
	}
	
	
	public String getEc() {
		return mContactOnline;
	}
	
	public void setEc( String contactOnline ) {
		mContactOnline = contactOnline;
	}
	
	
	public String getPn() {
		return mPhoneNbr;
	}
	
	public void setPn( String nbr ) {
		mPhoneNbr = nbr;
	}
	
	
	public String getMa() {
		return mMailAddr;
	}
	
	public void setMa( String mailAddr ) {
		mMailAddr = mailAddr;
	}
	
	
	public double getLat() {
		return mLat;
	}
	
	public void setLat( double lat ) {
		mLat = lat;
	}
	
	
	public double getLng() {
		return mLong;
	}
	
	public void setLng( double lng ) {
		mLong = lng;
	}
	
	
	public String getUd() {
		return mUpdatedDate;
	}
	
	public void setUd( String date ) {
		mUpdatedDate = date;
	}

}
