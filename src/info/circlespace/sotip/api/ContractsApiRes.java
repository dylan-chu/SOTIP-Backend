package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;


public class ContractsApiRes {

    private List<ContractInfo> result = new ArrayList<ContractInfo>();


    public void setResult( List<ContractInfo> result ) {
        this.result = result;
    }

    public List<ContractInfo> getResult() {
        return result;
    }

}
