package info.circlespace.sotip;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName="SotipConfig")
public class SotipConfig {
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
