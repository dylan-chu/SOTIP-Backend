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

    // the following variable names correspond exactly to the field names returned by the API
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

    
    public String getInvestmentTitle() {
        return investmentTitle;
    }

    public void setInvestmentTitle( String title ) {
        investmentTitle = title;
    }

    
    public void setUniqueProjectID( String id ) {
        uniqueProjectID = id;
    }

    public String getUniqueProjectID() {
        return uniqueProjectID;
    }

    
    public int getProjectID() {
        return projectID;
    }

    public void setProjectID( int id ) {
        projectID = id;
    }

    
    public String getAgencyProjectID() {
        return agencyProjectID;
    }

    public void setAgencyProjectID( String id ) {
        agencyProjectID = id;
    }

    
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName( String name ) {
        projectName = name;
    }

    
    public String getObjectivesExpectedOutcomes() {
        return objectivesExpectedOutcomes;
    }

    public void setObjectivesExpectedOutcomes( String outcomes ) {
        objectivesExpectedOutcomes = outcomes;
    }

    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate( String date ) {
        startDate = date;
    }

    
    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate( String date ) {
        completionDate = date;
    }

    
    public double getProjectLifeCycleCost() {
        return projectLifeCycleCost;
    }

    public void setProjectLifeCycleCost( double cost ) {
        projectLifeCycleCost = cost;
    }

    
    public String getSDLCMethodology() {
        return SDLCMethodology;
    }

    public void setSDLCMethodology( String methodology ) {
        SDLCMethodology = methodology;
    }

    
    public String getOtherSDLC() {
        return otherSDLC;
    }

    public void setOtherSDLC( String methodology ) {
        otherSDLC = methodology;
    }

    
    public String getPmExperienceLevel() {
        return pmExperienceLevel;
    }

    public void setPmExperienceLevel( String level ) {
        pmExperienceLevel = level;
    }

    
    public int getScheduleVarianceInDays() {
        return  scheduleVarianceInDays;
    }

    public void setScheduleVarianceInDays( int days ) {
        scheduleVarianceInDays = days;
    }

    
    public double getScheduleVariancePercent() {
        return scheduleVariancePercent;
    }

    public void setScheduleVariancePercent( double percent ) {
        scheduleVariancePercent = percent;
    }

    
    public String getScheduleColor() {
        return scheduleColor;
    }

    public void setScheduleColor( String colour ) {
        scheduleColor = colour;
    }

    
    public double getCostVarianceDollars() {
        return costVarianceDollars;
    }

    public void setCostVarianceDollars( double amount ) {
        costVarianceDollars = amount;
    }

    
    public double getCostVariancePercent() {
        return costVariancePercent;
    }

    public void setCostVariancePercent( double percent ) {
        costVariancePercent = percent;
    }

    
    public String getCostColor() {
        return costColor;
    }

    public void setCostColor( String colour ) {
        costColor = colour;
    }

    
    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus( String status ) {
        projectStatus = status;
    }

    
    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate( String date ) {
        updatedDate = date;
    }

    
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime( String time ) {
        updatedTime = time;
    }
    
}
