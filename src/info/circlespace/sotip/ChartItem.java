package info.circlespace.sotip;


public class ChartItem {

	public final static String TBL_NAME = "SotipCharts";
	
	public final static String COL_TYPE = "Type";
	public final static String COL_DATA = "Data";
	public final static String COL_AGENCIES = "Agencies";
	public final static String COL_UPD_DATE = "UpdDate";


	private int type;
	private String data;
	private String agcs;
	
	
	public ChartItem( int type, String data, String agcs ) {
		this.type = type;
		this.data = data;
		this.agcs = agcs;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType( int type ) {
		this.type = type;
	}


	public String getData() {
		return data;
	}
	
	public void setData( String data ) {
		this.data = data;
	}


	public String getAgcs() {
		return agcs;
	}
	
	public void setAgcs( String agcs ) {
		this.agcs = agcs;
	}
}
