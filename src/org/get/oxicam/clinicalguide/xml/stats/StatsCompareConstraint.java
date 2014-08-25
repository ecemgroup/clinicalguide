package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;

public class StatsCompareConstraint {

    public final ArrayList<StatsColumnCompare> leftHandSide;
    public final ArrayList<StatsColumnCompare> rightHandSide;
    public final ArrayList<StatsComparatorOperator> comparator;
    public StatsCompareConstraint(ArrayList<StatsColumnCompare> lhs, ArrayList<StatsColumnCompare> rhs, ArrayList<StatsComparatorOperator> comparator){
	int lhsLen = lhs.size();
	int rhsLen = rhs.size();
	int signLen = comparator.size();
	if(lhsLen != rhsLen && rhsLen != signLen) {
	    throw new IllegalArgumentException(
		    "left hand side, right hand side, and comparator should have the same number of child");
	}
	
	this.leftHandSide = lhs;
	this.rightHandSide = rhs;
	this.comparator = comparator;
    }
    
    /**
     * Gives all the tables and columns used in this compareconstraint. This can be used in identifying if you 
     * only need one or you need more tables when querying.
     * @return ArrayList of StatsSubject that contains all the tablename and columnname used in this CompareConstraint
     */
    public ArrayList<StatsSubject> getTablesAndColumns(){
	ArrayList<StatsSubject> retVal = new ArrayList<StatsSubject>();
	int compLen = leftHandSide.size();
	for(int i = 0; i < compLen; i++){
	    StatsColumnCompare lhs = leftHandSide.get(i);
	    StatsColumnCompare rhs = rightHandSide.get(i);
	    retVal.add(new StatsSubject(lhs.tablename, lhs.columnname));
	    retVal.add(new StatsSubject(rhs.tablename, rhs.columnname));
	}
	
	return retVal;
	
    }
    
}
