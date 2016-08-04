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
 * Custom event for holding a date to be passed in to the Lambda function
 */
public class SotipLbdFnInput {
	
	// records of projects that has been updated on or after this date is returned
	private String date;
	
	
	public String getDate() {
		return date;
	}
	
	public void setDate( String dateStr ) {
		date = dateStr;
	}
}
