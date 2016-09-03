package info.circlespace.sotip;


public class GroupedDataSet implements SotipDataSet {

	private int mNumSubsets;
	private PerformanceDataSet[] mDataSets;
	
	public GroupedDataSet( int numSubsets ) {
		mNumSubsets = numSubsets;
		mDataSets = new PerformanceDataSet[mNumSubsets];
		for ( int i=0; i<mDataSets.length; i++ ) {
			mDataSets[i] = new PerformanceDataSet();
		}
	}
	
	
	public void addToTally( int subsetNdx, int perfNdx ) {
		mDataSets[subsetNdx].addToTally( perfNdx );
	}
	
	public void addAgency( int subsetNdx, int perfNdx, String agencyCode ) {
		mDataSets[subsetNdx].addAgency( perfNdx, agencyCode);
	}
	
	public PerformanceDataSet[] getDataSets() {
		return mDataSets;
	}

	
	@Override
	public String getDataAsStr() {
		StringBuffer buf = new StringBuffer();
		
		for ( int i=0; i<mNumSubsets; i++ ) {
			if ( i>0)
				buf.append( "-" );
			buf.append( mDataSets[i].getDataAsStr() );
		}
		
		return buf.toString();
	}

	
	@Override
	public String getAgenciesAsStr() {
		StringBuffer buf = new StringBuffer();
		
		for ( int i=0; i<mNumSubsets; i++ ) {
			if ( i>0)
				buf.append( ":" );
			buf.append( mDataSets[i].getAgenciesAsStr() );
		}
		
		return buf.toString();
	}

}
