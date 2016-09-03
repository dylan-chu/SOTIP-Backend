package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;


public class BizCasesApiRes {

    private List<BizCaseInfo> result = new ArrayList<BizCaseInfo>();


    public void setResult( List<BizCaseInfo> result ) {
        this.result = result;
    }

    public List<BizCaseInfo> getResult() {
        return result;
    }

}
