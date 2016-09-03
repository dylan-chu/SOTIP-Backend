package info.circlespace.sotip;


public class ProjectItem {

	public final static String TBL_NAME = "SotipPrjs";
	public final static String COL_ID = "Id";
	public final static String COL_AGENCY_CODE = "Code";
	public final static String COL_INVESTMENT_ID = "UniqInvmtId";
	public final static String COL_INVESTMENT_TITLE = "InvmtTitle";
	public final static String COL_NAME = "Name";
	public final static String COL_PROJ_PERF = "ProjPerf";
	public final static String COL_STATUS = "Status";
	public final static String COL_START_DATE = "StartDate";
	public final static String COL_CMPLTN_DATE = "CmpltnDate";
	public final static String COL_SCH_VAR_CATEG = "SchVarCateg";
	public final static String COL_SCH_VAR_PERC = "SchVarPerc";
	public final static String COL_SCH_VAR_DAYS = "SchVarDays";
	public final static String COL_LIFECYCLE_COST = "LifecycleCost";
	public final static String COL_COST_VAR_CATEG = "CostVarCateg";
	public final static String COL_COST_VAR_PERC = "CostVarPerc";
	public final static String COL_COST_VAR_AMT = "CostVarAmt";
	public final static String COL_PM_EXP_LVL = "PmExpLvl";
	public final static String COL_SDLC_METHOD = "SdlcMethod";
	public final static String COL_OTHER_SDM = "OtherSdm";				
	public final static String COL_OBJECTIVES = "Objectives";
	public final static String COL_UPDATED_DATE = "UpdatedDate";		

	
	private int mId;
    private String mAgencyCode;
    private String mUniqInvmtId;
    private String mInvmtTitle;
    private String mName;
    private String mObjectives;
    private int mProjPerf;
    private int mProjStatus;
    private String mStartDate;
    private String mCompletionDate;
    private int mSchVarColor;
    private int mSchVarDays;
    private double mSchVarPerc;
    private double mLifecycleCost;
    private int mCostVarColor;
    private double mCostVarDollars;
    private double mCostVarPerc;
    private int mPmExpLvl;
    private int mSdlcMethod;
    private String mOtherSdm;
    private String mUpdatedDate;
    
    
    public ProjectItem( int id ) {
    	mId = id;
    }
    
    
    public int getId() {
    	return mId;
    }
    

    public void setAc( String code ) {
    	mAgencyCode = code;
    }
    
    public String getAc() {
    	return mAgencyCode;
    }
    
    
    public void setUii( String id ) {
    	mUniqInvmtId = id;
    }
    
    public String getUii() {
    	return mUniqInvmtId;
    }
    
    
    public void setIt( String title ) {
    	mInvmtTitle = title;
    }
    
    public String getIt() {
    	return mInvmtTitle;
    }
    
    
    public void setName( String name ) {
    	mName = name;
    }
    
    public String getName() {
    	return mName;
    }
    
    
    public void setObj( String objectives ) {
    	mObjectives = objectives;
    }
    
    public String getObj() {
    	return mObjectives;
    }
    
    
    public void setPp( int perf ) {
    	mProjPerf = perf;
    }
    
    public int getPp() {
    	return mProjPerf;
    }
    
    
    public void setPs( int status ) {
    	mProjStatus = status;
    }
    
    public int getPs() {
    	return mProjStatus;
    }
    
    
    public void setSd( String date ) {
    	mStartDate = date;
    }
    
    public String getSd() {
    	return mStartDate;
    }
    
    
    public void setCd( String date ) {
    	mCompletionDate = date;
    }
    
    public String getCd() {
    	return mCompletionDate;
    }
    
    
    public void setSv( int code ) {
    	mSchVarColor = code;
    }
    
    public int getSv() {
    	return mSchVarColor;
    }
    
    
    public void setSvd( int days ) {
    	mSchVarDays = days;
    }
    
    public int getSvd() {
    	return mSchVarDays;
    }
    
    
    public void setSvp( double perc ) {
    	mSchVarPerc = perc;
    }
    
    public double getSvp() {
    	return mSchVarPerc;
    }
    
    
    public void setLcc( double amt ) {
    	mLifecycleCost = amt;
    }
    
    public double getLcc() {
    	return mLifecycleCost;
    }
    
    
    public void setCv( int code ) {
    	mCostVarColor = code;
    }
    
    public int getCv() {
    	return mCostVarColor;
    }
    
    
    public void setCvd( double amt ) {
    	mCostVarDollars = amt;
    }
    
    public double getCvd() {
    	return mCostVarDollars;
    }
    
    
    public void setCvp( double perc ) {
    	mCostVarPerc = perc;
    }
    
    public double getCvp() {
    	return mCostVarPerc;
    }
    
    
    public void setPm( int code ) {
    	mPmExpLvl = code;
    }
    
    public int getPm() {
    	return mPmExpLvl;
    }
    
    
    public void setSdm( int code ) {
    	mSdlcMethod = code;
    }
    
    public int getSdm() {
    	return mSdlcMethod;
    }
    
    
    public void setOsdm( String method ) {
    	mOtherSdm = method;
    }
    
    public String getOsdm() {
    	return mOtherSdm;
    }
    
    
    public void setUd( String date ) {
    	mUpdatedDate = date;
    }
    
    public String getUd() {
    	return mUpdatedDate;
    }
}
