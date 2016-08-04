/*
 * Copyright 2016 Dylan Chu
 *
 * Released under the MIT License.
 * A copy of the License is located at
 *
 * http://creativecommons.org/licenses/MIT/
 */
package info.circlespace.sotip.api;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Defines the interfaces used by Retrofit to make the API calls.
 */
public class ItDashboardApiSvc {
	
    public static final String BASE_URL = "https://itdashboard.gov/api/v1/ITDB2/dataFeeds/";
    public static final String PROJECTS_URL = "projects";


    public interface ProjectsApi {
        @GET(PROJECTS_URL)
        Call<ProjectsApiRes> getProjects(
        );
    }

}
