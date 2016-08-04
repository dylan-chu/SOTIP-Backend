/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

/**
 * This class maps to the item in the DynamoDB table called SotipConfig.
 */
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

}
