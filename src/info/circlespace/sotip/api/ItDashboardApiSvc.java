package info.circlespace.sotip.api;

import retrofit2.Call;
import retrofit2.http.GET;


public class ItDashboardApiSvc {

	public static final String BASE_URL = "https://itdashboard.gov/api/v1/ITDB2/dataFeeds/";
    public static final String RELATED_URLS_URL = "investmentRelatedURLs";


    public interface UrlsApi {
        @GET(RELATED_URLS_URL)
        Call<UrlsApiRes> getUrls(
        );
    }

}
