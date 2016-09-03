package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;


public class UrlsApiRes {

    private List<UrlInfo> result = new ArrayList<UrlInfo>();


    public void setResult( List<UrlInfo> result ) {
        this.result = result;
    }

    public List<UrlInfo> getResult() {
        return result;
    }

}
