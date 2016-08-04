/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * This class maps to an item in the DynamoDB table called SotipConfig.
 */
@DynamoDBTable(tableName="SotipConfig")
public class SotipConfig {
	
	// name of DynamoDB table
	static final String TBL_NAME = "SotipConfig";
	// name of hash key of table
	static final String COL_ID = "ConfigId";
	// name of column to hold the latest date that data was pulled from IT Dashboard
	static final String COL_LAST_UPD_DATE = "LastUpdDate";
	// name of column to hold the number of files created from the pulled data
	static final String COL_NUM_FILES = "NumFiles";
	// key of only item in table
	static final String CONFIG_KEY = "SotipPrjs";

	private String mId;
	private String mLastUpdDate;
	private int mNumFiles;
	

	@DynamoDBHashKey(attributeName="ConfigId")
    public String getId() { return mId; }
    public void setId(String id) { mId = id; }
    
    
    @DynamoDBAttribute(attributeName="LastUpdDate")
    public String getLastUpdDate() { return mLastUpdDate; }
    public void setLastUpdDate(String date) { mLastUpdDate = date; }


    @DynamoDBAttribute(attributeName="NumFiles")
    public int getNumFiles() { return mNumFiles; }
    public void setNumFiles(int numFiles) { mNumFiles = numFiles; }

}
