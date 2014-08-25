package org.get.oxicam.clinicalguide.xml.stats;

public class StatsColumnCompare extends StatsSubject{

    /**
     * Constructs a StatsColumnCompare which is used for comparing columns (lefthandside and righthandside of compareconstraint in xml)
     * @param tablename The tablename to use(ends with _table)
     * @param columnname the columnname of in the table to use
     */
    public StatsColumnCompare(String tablename, String columnname){
	super(tablename, columnname);
    }
    
    public String toString(){
	return " " + tablename + "." + columnname + " " ;
    }
    
}
