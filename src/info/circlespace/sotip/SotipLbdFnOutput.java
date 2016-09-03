package info.circlespace.sotip;

import java.util.List;


public class SotipLbdFnOutput {

	private List<InvestmentItem> items;

	
	public SotipLbdFnOutput(List<InvestmentItem> itms) {
		setItems(itms);
	}

	
	public List<InvestmentItem> getItems() {
		return items;
	}

	public void setItems(List<InvestmentItem> itms) {
		items = itms;
	}
	
}
