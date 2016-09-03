package info.circlespace.sotip.api;


public class UrlInfo {

    // the variable names correspond exactly to the field names returned by the API
    private String uniqueInvestmentIdentifier;
    private String investmentURL;
    private String updatedDate;
    private String updatedTime;


    public String getUniqueInvestmentIdentifier() {
        return this.uniqueInvestmentIdentifier;
    }


    public void setUniqueInvestmentIdentifier( String id ) {
        this.uniqueInvestmentIdentifier = id;
    }

    
    public String getInvestmentURL() {
        return this.investmentURL;
    }

    public void setInvestmentURL( String url ) {
        this.investmentURL = url;
    }

    
    public String getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate( String date ) {
        this.updatedDate = date;
    }

    
    public String getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime( String time ) {
        this.updatedTime = time;
    }

}
