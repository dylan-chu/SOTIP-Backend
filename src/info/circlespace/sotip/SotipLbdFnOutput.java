package info.circlespace.sotip;

import java.util.List;


public class SotipLbdFnOutput {

	private List<ChartItem> items;

	
	public SotipLbdFnOutput(List<ChartItem> itms) {
		setItems(itms);
	}

	
	public List<ChartItem> getItems() {
		return items;
	}

	public void setItems(List<ChartItem> itms) {
		items = itms;
	}
}
