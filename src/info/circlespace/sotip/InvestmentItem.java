package info.circlespace.sotip;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class InvestmentItem {

	public final static String TBL_NAME = "SotipInvmts";
	public final static String COL_ID = "Uii";
	public final static String COL_AGENCY_CODE = "Code";
	public final static String COL_TITLE = "Title";
	public final static String COL_SUMMARY = "Summary";
	public final static String COL_CIO_RATING = "CioRating";
	public final static String COL_NUM_PROJS = "NumProjs";
	public final static String COL_LIFECYCLE_COST = "LcCost";
	public final static String COL_CONTRACTORS = "Contractors";
	public final static String COL_CONTRACT_TYPES = "ContractTypes";
	public final static String COL_URLS = "Urls";
	public final static String COL_UPD_DATE = "UpdDate";

	public static final String NOT_AVAILABLE = "n/a";
	public static final int UNKNOWN_NUM = -1;
	public static final double UNKNOWN_AMT = 0.0d;
	
    //private String uniqueInvestmentIdentifier;
    private String uii;
    //private String agencyCode;
    private String ac;
    //private String investmentTitle;
    private String it;
    // summary
    private String sum;
    // number of projects
    private int np;
    // lifecycle cost
    private double lcc;
    // cio rating
    private int cio;
    // contractor
    private Set<String> c;
    // contract type
    private Set<String> ct;
    // url 1
    private Set<String> urls;
    //updated date;
    private String ud;
    
    
    public InvestmentItem( String id ) {
    	this.uii = id;
        this.ac = NOT_AVAILABLE;
        this.it = NOT_AVAILABLE;
        this.sum = NOT_AVAILABLE;
        this.cio = UNKNOWN_NUM;
        this.np = UNKNOWN_NUM;
        this.lcc = UNKNOWN_AMT;
        this.c = new HashSet<String>();
        c.add( NOT_AVAILABLE );
        this.ct = new HashSet<String>();
        ct.add( NOT_AVAILABLE );
        this.urls = new HashSet<String>();
        urls.add( NOT_AVAILABLE );
        this.ud = NOT_AVAILABLE;    	
    }
    
    
    public void setUii(String id) {
    	this.uii = id;
    }
    
    
    public String getUii() {
    	return this.uii;
    }
    
    
    public void setIt( String title ) {
    	this.it = title;
    }
    
    public String getIt() {
    	return this.it;
    }
    
    
    public void setAc( String code ) {
    	this.ac = code;
    }
    
    public String getAc() {
    	return this.ac;
    }
    
    
    public void setSum( String summary ) {
    	this.sum = summary;
    }
    
    public String getSum() {
    	return this.sum;
    }
    
    
    public void setCio( int rating ) {
    	this.cio = rating;
    }
    
    public int getCio() {
    	return this.cio;
    }


    public void setNp( int num ) {
    	this.np = num;
    }
    
    public int getNp() {
    	return this.np;
    }


    public void setLcc( double amt ) {
    	this.lcc = amt;
    }
    
    public double getLcc() {
    	return this.lcc;
    }

    
    public void addContractor( String contractor ) {
    	this.c.add( contractor );
    }
    
    public void addContractors( List<String> contractors ) {
    	for ( String contractor: contractors ) {
    	this.c.add( contractor );
    	}
    }
    
    public void setC( Set<String> contractors ) {
    	this.c = contractors;
    }
    
    public Set<String> getC() {
    	return this.c;
    }
    
    
    public void addContractType( String type ) {
    	this.ct.add( type );
    }
    
    public void addContractTypes( List<String> contractTypes ) {
    	for ( String type: contractTypes ) {
    	this.ct.add( type );
    	}
    }
    
    public void setCt( Set<String> types ) {
    	this.ct = types;
    }
    
    public Set<String> getCt() {
    	return this.ct;
    }
    
    
    public void addUrl( String url ) {
    	this.urls.add( url );
    }
    
    public void addUrls( List<String> urls ) {
    	for ( String url: urls ) {
    		this.urls.add( url );
    	}
    }
    
    public void setUrls( Set<String> urls ) {
    	this.urls = urls;
    }
    
    public Set<String> getUrls() {
    	return this.urls;
    }


    public void setUd( String date ) {
    	this.ud = date;
    }
    
    public String getUd() {
    	return this.ud;
    }

    
	public static String transformNull( String str ) {
		if ( str == null )
			return NOT_AVAILABLE;
		
		return str;
	}
	
}
