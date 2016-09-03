package info.circlespace.sotip.api;


public class Utility {

	public static final String NOT_AVAILABLE = "n/a";
	
	public static String transformNull( String str ) {
		if ( str == null )
			return NOT_AVAILABLE;
		
		return str;
	}
	
}
