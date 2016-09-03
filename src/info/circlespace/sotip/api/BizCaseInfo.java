package info.circlespace.sotip.api;


public class BizCaseInfo {

    // the variable names correspond exactly to the field names returned by the API
    private String uniqueInvestmentIdentifier;
    private int businessCaseID;
    private String agencyCode;
    private String agencyName;
    private String bureauCode;
    private String bureauName;
    private String investmentTitleITPortfolio;
    private String briefSummary;
    //private String explanationOfChangetoPYorCYfunding;
    private String noEVMExplanation;
    private int evaluationByAgencyCIO;
    private String cioEvaluationComments;
    private String cioEvaluationColor;
    private int consecutiveMonthsWithRedCIOEval;
    private String dateOfLastInvestmentDetailUpdate;
    

    public String getUniqueInvestmentIdentifier() {
        return uniqueInvestmentIdentifier;
    }

    public void setUniqueInvestmentIdentifier( String id ) {
        uniqueInvestmentIdentifier = id;
    }

    
    public int getBusinessCaseID() {
        return businessCaseID;
    }

    public void setBusinessCaseID( int id ) {
        businessCaseID = id;
    }

    
    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode( String code ) {
        agencyCode = code;
    }

    
    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName( String name ) {
        agencyName = name;
    }

    
    public String getBureauCode() {
        return bureauCode;
    }

    private void setBureauCode( String code ) {
        bureauCode = code;
    }

    
    public String getBureauName() {
        return bureauName;
    }
    
    public void setBureauName( String name ) {
        bureauName = name;
    }

    
    public String getInvestmentTitleITPortfolio() {
        return investmentTitleITPortfolio;
    }

    public void setInvestmentTitleITPortfolio( String title ) {
        investmentTitleITPortfolio = title;
    }

    
    public String getBriefSummary() {
        return briefSummary;
    }

    public void setBriefSummary( String summary ) {
        briefSummary = summary;
    }

    //public String getExplanationOfChangetoPYorCYfunding() {
    //    return this.explanationOfChangetoPYorCYfunding;
    //}

    //public void setExplanationOfChangetoPYorCYfunding( String explanation) {
    //    this.explanationOfChangetoPYorCYfunding = explanation;
    //}

    
    public String getNoEVMExplanation() {
        return noEVMExplanation;
    }

    public void setNoEVMExplanation( String explanation) {
        noEVMExplanation = explanation;
    }

    
    public int getEvaluationByAgencyCIO() {
        return evaluationByAgencyCIO;
    }

    public void setEvaluationByAgencyCIO( int evaluation ) {
        evaluationByAgencyCIO = evaluation ;
    }

    
    public String getCioEvaluationComments() {
        return cioEvaluationComments;
    }

    public void setCioEvaluationComments( String comments ) {
        cioEvaluationComments = comments;
    }

    
    public String getCioEvaluationColor() {
        return cioEvaluationColor;
    }

    public void setCioEvaluationColor( String colour) {
        cioEvaluationColor = colour;
    }

    
    public int getConsecutiveMonthsWithRedCIOEval() {
        return consecutiveMonthsWithRedCIOEval;
    }

    public void setConsecutiveMonthsWithRedCIOEval( int num ) {
        consecutiveMonthsWithRedCIOEval = num;
    }


    public String getDateOfLastInvestmentDetailUpdate() {
    	return dateOfLastInvestmentDetailUpdate;
    }
    
    public void setDateOfLastInvestmentDetailUpdate( String date ) {
    	dateOfLastInvestmentDetailUpdate = date;
    }

}
