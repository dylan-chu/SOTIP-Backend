package info.circlespace.sotip;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PerformanceDataSet implements SotipDataSet {

	
	public static final int NUM_CATEGORIES = 5;

	private int[] mDataSet;
	private Set<String>[] mAgencies;
	
	
	public PerformanceDataSet() {
		mDataSet = new int[NUM_CATEGORIES];
		mAgencies = new Set[NUM_CATEGORIES];
		for ( int i=0; i<NUM_CATEGORIES; i++ ) {
			mDataSet[i] = 0;
			mAgencies[i] = new HashSet<String>();
		}
	}

	
	public void addToTally(int ndx) {
		mDataSet[ndx]++;
	}
	
	
	public int[] getTally() {
		return mDataSet;
	}

	
	public void addAgency( int ndx, String agencyCode ) {
		mAgencies[ndx].add( agencyCode );
	}

	
	@Override
	public String getDataAsStr() {
		StringBuffer buf = new StringBuffer();
		
		for ( int i=0; i<NUM_CATEGORIES; i++ ) {
			if ( i>0)
				buf.append( "," );
			buf.append( mDataSet[i] );
		}
		
		return buf.toString();
	}


	@Override
	public String getAgenciesAsStr() {
		StringBuffer buf = new StringBuffer();
		
		for ( int i=0; i<NUM_CATEGORIES; i++ ) {
			Set<String> agencySet = mAgencies[i];
			String[] agencies = agencySet.toArray(new String[agencySet.size()]);
			Arrays.sort( agencies );
			
			if ( i>0)
				buf.append( "-" );

			if ( agencies.length == 0 ) {
				buf.append( 0 );
				continue;
			}
			
			for ( int j=0; j<agencies.length; j++ ) {
				if ( j>0)
					buf.append( "," );
				buf.append( agencies[j] );
			}
			
		}
		
		return buf.toString();
	}

}
