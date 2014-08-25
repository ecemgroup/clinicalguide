package org.get.oxicam.clinicalguide.xml.stats;


public class StatsSubject {

    public final String columnname;
    public final String tablename;
    public StatsSubject(String tablename, String columnname){
	this.tablename = tablename;
	this.columnname = columnname;
    }
}
