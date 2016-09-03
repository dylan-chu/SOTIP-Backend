package info.circlespace.sotip.api;


public class ContractInfo {

	private String uniqueInvestmentIdentifier;
	private int contractID;
	//private int agencyContractID;
	//private String contractStatus;
	//private String contractNumber;
	//private String evmRequired;
	private String vendorNameUSAspending;
	//private double ActionObligationAmountUSAspending;
	private String typeOfContractUSAspending;
	//private String performanceBasedContractUSAspending;
	//private String contractStartDateUSAspending;
	//private String contractEndDateUSAspending;
	//private String contractCompeteUSAspending;
	//private String contractDescriptionUSAspending;
	//private String timestampContract;

	
	public int getContractID() {
		return this.contractID;
	}
	
	public void setContractID( int id ) {
		this.contractID = id;
	}
	
	
    public String getUniqueInvestmentIdentifier() {
        return this.uniqueInvestmentIdentifier;
    }
    
    public void setUniqueInvestmentIdentifier( String id ) {
        this.uniqueInvestmentIdentifier = id;
    }

    
    public String getVendorNameUSAspending() {
    	return this.vendorNameUSAspending;
    }
    
    public void setVendorNameUSAspending( String vendor ) {
    	this.vendorNameUSAspending = vendor;
    }


    public String getTypeOfContractUSAspending() {
    	return this.typeOfContractUSAspending;
    }
    
    public void setTypeOfContractUSAspending( String contract ) {
    	this.typeOfContractUSAspending = contract;
    }
    
}
