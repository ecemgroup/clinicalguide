package org.get.oxicam.clinicalguide.xml.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.DateHelper;
import org.get.oxicam.clinicalguide.xml.stats.StatsComparatorOperator;
import org.get.oxicam.clinicalguide.xml.stats.StatsConstraint;
import org.get.oxicam.clinicalguide.xml.stats.StatsSubject;

public class QueryHelper {

    /**
     * join tables to history table
     * 
     * @param arr
     *            array list of tables to join
     * @param tables
     *            tables joined
     * @return string that can be concatenated to a "FROM" clause of a query
     */
    public static String joinTable(ArrayList<? extends StatsSubject> arr,
	    Set<String> tables) {
	String retVal = "";
	if (arr != null) {
	    int len = arr.size();
	    for (int i = 0; i < len; i++) {
		StatsSubject fc = arr.get(i);
		retVal += joinTable(fc.tablename, tables);
	    }
	}
	return retVal;
    }

    /**
     * Joins a table to history_table
     * 
     * @param tablename
     *            The tablename to join. ie. patient_details_table (_table at
     *            the end is necessary).
     * @param join
     *            The Set of already joined tables. (Prevents joining already
     *            joined tables)
     * @return Returns a String that can be concatenated to a "FROM" clause of a
     *         query.
     */
    public static String joinTable(String tablename, Set<String> join) {
	tablename = tablename.trim();
	String retVal = " ";
	String table = tablename.substring(0, tablename.length() - 6); // -6
								       // because
								       // it
								       // should
								       // end
								       // with
								       // "_table"
	if (table.equalsIgnoreCase("patient_details")
		&& !join.contains("patient_details")) {
	    retVal += " JOIN " + table + " " + tablename + " ON "
		    + " history_table.patient = " + tablename + ".id ";
	    join.add("patient_details");
	} else if (table.equalsIgnoreCase("user") && !join.contains("user")) {
	    retVal += " JOIN " + table + " " + tablename + " ON "
		    + " history_table.user_id = " + tablename + ".id ";
	    join.add("user");

	} else if (table.equalsIgnoreCase("history_answers")
		&& !join.contains("history_answers")) {
	    retVal += " JOIN " + table + " " + tablename + " ON "
		    + " history_table.id = " + tablename + ".assessment ";

	    join.add("history_answers");

	} else if (table.equalsIgnoreCase("history_treatment")
		&& !join.contains("history_treatment")) {
	    retVal += " JOIN " + table + " " + tablename + " ON "
		    + " history_table.id = " + tablename + ".assessment ";
	    join.add("history_treatment");

	}
	return retVal;
    }

    public static String addToWhere(ClinicalGuideActivity mActivity, ArrayList<StatsConstraint> arrFc){
	String retVal = "";
	if(arrFc != null){
	    int len = arrFc.size();
	    for(int i = 0; i < len;){
		retVal += " " + addToWhere(mActivity, arrFc.get(i));
		if(++i < len){
		    retVal += " AND ";
		}
	    }
	}
	return retVal;
    }
    
    public static String addToWhere(ClinicalGuideActivity mActivity,
	    StatsConstraint fc) {
	String retVal = "";
	if (fc.data.equalsIgnoreCase("current_user")) {
	    retVal = " " + fc.tablename + "." + fc.columnname + " "+ fc.operator.toString() + " '" + mActivity.getUser().id + "' ";
	} else if (fc.tablename.equalsIgnoreCase("patient_details_table")
		&& fc.columnname.equalsIgnoreCase("age")) {
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar c1 = Calendar.getInstance();
	    Calendar c2 = Calendar.getInstance();
	    c1 = DateHelper.getMidnight(c1);
	    c1 = DateHelper.getMidnight(c2);
	    c1.add(Calendar.YEAR, Integer.parseInt(fc.data) * -1); // upperbound
	    c2.add(Calendar.YEAR, Integer.parseInt(fc.data) * -1 - 1); // lowerbound
	    switch (fc.operator.typeInt) {
	    case StatsComparatorOperator.NOT_EQUAL:
		retVal = " " + fc.tablename + ".birthdate > '"
			+ fmt.format(new Date(c1.getTimeInMillis())) + "' OR "
			+ fc.tablename + ".birthdate <= '"
			+ fmt.format(new Date(c2.getTimeInMillis())) + "' ";
		break;
	    case StatsComparatorOperator.LESS_THAN:
		retVal = " " + fc.tablename + ".birthdate > '"
			+ fmt.format(new Date(c1.getTimeInMillis())) + "' ";
		break;
	    case StatsComparatorOperator.LESS_THAN_EQUAL:
		retVal = " " + fc.tablename + ".birthdate >= '"
			+ fmt.format(new Date(c2.getTimeInMillis())) + "' ";
		break;
	    case StatsComparatorOperator.GREATER_THAN_EQUAL:
		retVal = " " + fc.tablename + ".birthdate <= '"
			+ fmt.format(new Date(c1.getTimeInMillis())) + "' ";
		break;
	    case StatsComparatorOperator.GREATER_THAN:
		retVal = " " + fc.tablename + ".birthdate < '"
			+ fmt.format(new Date(c2.getTimeInMillis())) + "' ";
		break;
	    default: // default is equal
		retVal = " " + fc.tablename + ".birthdate <= '"
			+ fmt.format(new Date(c1.getTimeInMillis())) + "' AND "
			+ fc.tablename + ".birthdate > '"
			+ fmt.format(new Date(c2.getTimeInMillis())) + "' ";
		break;
	    }
	    return retVal;

	} else {
	    return " " + fc.tablename + "." + fc.columnname + " "
		    + fc.operator.toString() + " '" + fc.data + "' ";
	}

	return retVal;
    }

    public static String addToSelect(StatsSubject subject) {
	if (subject.columnname.equalsIgnoreCase("age")
		&& subject.tablename.equalsIgnoreCase("patient_details_table")) {
	    return " " + subject.tablename + ".birthdate AS patient_age ";
	} else {
	    return " " + subject.tablename + "." + subject.columnname + " AS "
		    + subject.tablename + "_" + subject.columnname;
	}
    }

}
