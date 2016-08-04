/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip;

import java.util.List;

/**
 * Custom event for holding the results that the Lambda function returns
 */
public class SotipLbdFnOutput {

	// a list of project records matching the input date criterion
	private List<ProjectItem> items;

	
	public SotipLbdFnOutput(List<ProjectItem> itms) {
		setItems(itms);
	}

	
	public List<ProjectItem> getItems() {
		return items;
	}

	public void setItems(List<ProjectItem> itms) {
		items = itms;
	}
}
