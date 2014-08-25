package org.get.oxicam.clinicalguide.xml.stats;



public class StatsConstraint extends StatsSubject{
    
    public final String data;
    public final StatsComparatorOperator operator;
    
    /**
     * Constraint used in queries. This will go in the "where" clause in the query.
     * (Example: Where [tablename].[columnname] [operator] '[data]'
     * @param tablename tablename that is in the database
     * @param columnname a columnname in the tablename
     * @param data the data to constraint to.
     * @param operator equal, not_equal, etc..
     */
    public StatsConstraint(String tablename, String columnname, String data, StatsComparatorOperator operator){
	super(tablename, columnname);
	this.data = data;
	if(operator == null){
	    this.operator = new StatsComparatorOperator(StatsComparatorOperator.EQUAL);
	} else {
	    this.operator = operator;
	}
    }
    
}
