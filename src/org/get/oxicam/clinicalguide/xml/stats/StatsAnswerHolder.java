package org.get.oxicam.clinicalguide.xml.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.get.oxicam.clinicalguide.xml.DateHelper;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

public class StatsAnswerHolder {

    public final QueryResultTable table;
    public final Long start;
    public final Long end;
    
    /**
     * Constructs a StatsAnswerHolder.
     * @param table The QueryResult table
     * @param start long representing start date(this is mostly used if there are groupings in the timespan) use null if not used
     * @param end long representing end date(this is mostly used if there are groupings in the timespan) use null if not used.
     */
    public StatsAnswerHolder(QueryResultTable table, Long start,Long end){
	this.table = table;
	this.start = start;
	this.end = end;
	fixBirthdates();
    }
    
    /**
     * Sets the age if the columnname is patient_age. Gives the age depending on the birthdate and the current time
     */
    private void fixBirthdates(){
	int len = table.rows.size();
	for (int i = 0; i < len; i++) {
	    ArrayList<QueryResultCell> cells = table.rows.get(i).cells;
	    int mapLen = cells.size();
	    for(int j = 0; j < mapLen; j++){
		QueryResultCell cell = cells.get(j);
		String colName = cell.getColumnname();
		if(colName.equalsIgnoreCase("patient_age")){
		    long bday = DateHelper.changeTextDateToLong(cell.getData());
		    int age = DateHelper.calculateAge(bday);
		    cell.setData(age + "");
		} 
	    }
	}
    }
    
    /**
     * Very basic toString, Just prints all the details.
     */
    public String toString(){
	
	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	
	String dateStr = "";
	if(end != null && start != null){
	    Calendar c1 = Calendar.getInstance();
	    Calendar c2 = Calendar.getInstance();
	    c1.setTimeInMillis(start);
	    c2.setTimeInMillis(end);
	    dateStr += fmt.format(new Date(start));
	    if(c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)){
		dateStr += " " + fmt.format(new Date(end));
	    }
	}
	String retVal = dateStr + "\n";
	int len = table.rows.size();
	if(len == 0) { retVal += "NONE\n"; }
	for (int j = 0; j < len; j++) {
	    ArrayList<QueryResultCell> map = table.rows.get(j).cells;
	    int mapLen = map.size();
	    for(int k = 0; k < mapLen; k++){
		QueryResultCell cell = map.get(k);
		retVal += " " + cell.getColumnname() + ": " + cell.getData() + "\n"; 
	    }
	    retVal += "\n";
	}
	return retVal;
    }
}
