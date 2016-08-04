/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the list of results returned from the API call to IT Dashboard
 */
public class ProjectsApiRes {

	// the returned JSON data has a field called result
    private List<ProjectInfo> result = new ArrayList<ProjectInfo>();


    public void setResults( List<ProjectInfo> result ) {
        this.result = result;
    }

    public List<ProjectInfo> getResult() {
        return result;
    }
}
