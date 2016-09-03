package info.circlespace.sotip.api;

import retrofit2.Call;
import retrofit2.http.GET;


public class ItDashboardApiSvc {

	public static final String BASE_URL = "https://itdashboard.gov/api/v1/ITDB2/dataFeeds/";
    public static final String BIZ_CASES_URL = "businessCase";


    public interface BizCasesApi {
        @GET(BIZ_CASES_URL)
        Call<BizCasesApiRes> getBizCases(
        );
    }

}
