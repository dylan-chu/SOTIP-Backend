package info.circlespace.sotip;

import java.util.List;


public class SotipLbdFnOutput {

	private List<AgencyItem> items;

	
	public SotipLbdFnOutput(List<AgencyItem> itms) {
		setItems(itms);
	}

	
	public List<AgencyItem> getItems() {
		return items;
	}

	public void setItems(List<AgencyItem> itms) {
		items = itms;
	}
}
