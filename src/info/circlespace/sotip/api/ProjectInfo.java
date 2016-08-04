/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip.api;

/**
 * Holds the record for one project from the data pulled from IT Dashboard
 */
public class ProjectInfo {

    // the variable names correspond exactly to the field names returned by the API
    private String uniqueInvestmentIdentifier;
    private int businessCaseID;
    private String agencyCode;
    private String agencyName;
    private String investmentTitle;
    private String uniqueProjectID;
    private int projectID;
    private String agencyProjectID;
    private String projectName;
    private String objectivesExpectedOutcomes;
    private String startDate;
    private String completionDate;
    private double projectLifeCycleCost;
    private String SDLCMethodology;
    private String otherSDLC;
    private String releaseEverySixMo;
    private String releaseEverySixMoComment;
    private String pmExperienceLevel;
    private int scheduleVarianceInDays;
    private double scheduleVariancePercent;
    private String scheduleColor;
    private double costVarianceDollars;
    private double costVariancePercent;
    private String costColor;
    private String projectStatus;
    private String updatedDate;
    private String updatedTime;


    public String getUniqueInvestmentIdentifier() {
        return this.uniqueInvestmentIdentifier;
    }

    public void setUniqueInvestmentIdentifier( String id ) {
        this.uniqueInvestmentIdentifier = id;
    }

    
    public int getBusinessCaseID() {
        return this.businessCaseID;
    }

    public void setBusinessCaseID( int id ) {
        this.businessCaseID = id;
    }

    
    public String getAgencyCode() {
        return this.agencyCode;
    }

    public void setAgencyCode( String code ) {
        this.agencyCode = code;
    }

    
    public String getAgencyName() {
        return this.agencyName;
    }

    public void setAgencyName( String name ) {
        this.agencyName = name;
    }

    
    public String getInvestmentTitle() {
        return this.investmentTitle;
    }

    public void setInvestmentTitle( String title ) {
        this.investmentTitle = title;
    }

    
    public void setUniqueProjectID( String id ) {
        this.uniqueProjectID = id;
    }

    public String getUniqueProjectID() {
        return this.uniqueProjectID;
    }

    
    public int getProjectID() {
        return this.projectID;
    }

    public void setProjectID( int id ) {
        this.projectID = id;
    }

    
    public String getAgencyProjectID() {
        return this.agencyProjectID;
    }

    public void setAgencyProjectID( String id ) {
        this.agencyProjectID = id;
    }

    
    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName( String name ) {
        this.projectName = name;
    }

    
    public String getObjectivesExpectedOutcomes() {
        return this.objectivesExpectedOutcomes;
    }

    public void setObjectivesExpectedOutcomes( String outcomes ) {
        this.objectivesExpectedOutcomes = outcomes;
    }

    
    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate( String date ) {
        this.startDate = date;
    }

    
    public String getCompletionDate() {
        return this.completionDate;
    }

    public void setCompletionDate( String date ) {
        this.completionDate = date;
    }

    
    public double getProjectLifeCycleCost() {
        return this.projectLifeCycleCost;
    }

    public void setProjectLifeCycleCost( double cost ) {
        this.projectLifeCycleCost = cost;
    }

    
    public String getSDLCMethodology() {
        return this.SDLCMethodology;
    }

    public void setSDLCMethodology( String methodology ) {
        this.SDLCMethodology = methodology;
    }

    
    public String getOtherSDLC() {
        return this.otherSDLC;
    }

    public void setOtherSDLC( String methodology ) {
        this.otherSDLC = methodology;
    }

    
    public String getReleaseEverySixMo() {
        return this.releaseEverySixMo;
    }

    public void setReleaseEverySixMo( String flag ) {
        this.releaseEverySixMo = flag;
    }

    
    public String getReleaseEverySixMoComment() {
        return this.releaseEverySixMoComment;
    }

    public void setReleaseEverySixMoComment( String comment ) {
        this.releaseEverySixMoComment = comment;
    }

    
    public String getPmExperienceLevel() {
        return this.pmExperienceLevel;
    }

    public void setPmExperienceLevel( String level ) {
        this.pmExperienceLevel = level;
    }

    
    public int getScheduleVarianceInDays() {
        return  this.scheduleVarianceInDays;
    }

    public void setScheduleVarianceInDays( int days ) {
        this.scheduleVarianceInDays = days;
    }

    
    public double getScheduleVariancePercent() {
        return this.scheduleVariancePercent;
    }

    public void setScheduleVariancePercent( double percent ) {
        this.scheduleVariancePercent = percent;
    }

    
    public String getScheduleColor() {
        return this.scheduleColor;
    }

    public void setScheduleColor( String colour ) {
        this.scheduleColor = colour;
    }

    
    public double getCostVarianceDollars() {
        return this.costVarianceDollars;
    }

    public void setCostVarianceDollars( double amount ) {
        this.costVarianceDollars = amount;
    }

    
    public double getCostVariancePercent() {
        return this.costVariancePercent;
    }

    public void setCostVariancePercent( double percent ) {
        this.costVariancePercent = percent;
    }

    
    public String getCostColor() {
        return this.costColor;
    }

    public void setCostColor( String colour ) {
        this.costColor = colour;
    }

    
    public String getProjectStatus() {
        return this.projectStatus;
    }

    public void setProjectStatus( String status ) {
        this.projectStatus = status;
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
