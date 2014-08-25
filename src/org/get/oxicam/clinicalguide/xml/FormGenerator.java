package org.get.oxicam.clinicalguide.xml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.xml.forms.Form;
import org.get.oxicam.clinicalguide.xml.forms.FormCell;
import org.get.oxicam.clinicalguide.xml.forms.FormColumn;
import org.get.oxicam.clinicalguide.xml.query.QueryHelper;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultRow;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;
import org.get.oxicam.clinicalguide.xml.stats.AbstractStatsQuestion;
import org.get.oxicam.clinicalguide.xml.stats.Stats;
import org.get.oxicam.clinicalguide.xml.stats.StatsAnswerHolder;
import org.get.oxicam.clinicalguide.xml.stats.StatsConstraint;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class FormGenerator {

    private Context mActivity;
    private Form form;
    private Database db;
    private long formStartDate;
    private long formEndDate;
    private ArrayList<Object> columnInfo;
    private final static String END_COLUMN = "columnEnd";

    public FormGenerator(Context context, Form form) {
	this.mActivity = context;
	this.form = form;
	db = new Database(mActivity);
	formStartDate = form.getStartDate();
	formEndDate = form.getEndDate();
	columnInfo = new ArrayList<Object>();
	if (mActivity instanceof ClinicalGuideActivity) {
	    form.constraints.add(new StatsConstraint("user_table", "id", ((ClinicalGuideActivity) mActivity).getUser().id + "", null));
	}
    }

    /**
     * Gets the result of the form.
     * 
     * @return A String representation of the result of the form.
     */
    public String getFormResult() {
	String retVal = "";
	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	String userName = "";

	if (mActivity instanceof ClinicalGuideActivity) {
	    userName = "Name of nurse: " + ((ClinicalGuideActivity) mActivity).getUser().username  + "\n";
	}
	String cells = getCellString();
	String columns = getColumnString();
	String stats = getStatsString(form.stats, null);
	String startDate = "Report start date: "
		+ fmt.format(new Date(formStartDate)) + "\n";
	String endDate = "Report end date: "
		+ fmt.format(new Date(formEndDate)) + "\n";
	retVal += "====" + form.name + "====\n";
	retVal += userName;
	retVal += startDate;
	retVal += endDate;
	retVal += "\nCELLS\n============\n";
	retVal += cells;
	retVal += "\nCOLUMNS\n============\n";
	retVal += columns;
	retVal += "\nSTATS\n============\n";
	retVal += stats;
	Log.v("FORM RESULTS", "====" + form.name + "====\n");
	Log.v("FORM RESULTS", startDate);
	Log.v("FORM RESULTS", endDate);
	Log.v("FORM RESULTS", userName);
	Log.v("FORM RESULTS", "\nCELLS\n============\n");
	Log.v("FORM RESULTS", cells);
	Log.v("FORM RESULTS", "\nCOLUMNS\n============\n");
	Log.v("FORM RESULTS", columns);
	Log.v("FORM RESULTS", "\nSTATS\n============\n");
	Log.v("FORM RESULTS", stats);
	return retVal;
    }
    
    /**
     * returns the result in a html formated string.
     * getFormResult() should be called before this, otherwise it might not get all information
     * @return a html string
     */
    public String getHTMLResult() {
	// run getFormResult() to populate the form
	//getFormResult();

	String userName = "";
	if (mActivity instanceof ClinicalGuideActivity) {
	    userName = ((ClinicalGuideActivity) mActivity).getUser().username;
	}
	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	StringBuilder retVal = new StringBuilder();
	retVal.append("<div title=\"formTitle\">").append(form.name)
		.append("</div>\n");
	// start date
	retVal.append("<div title=\"startDate\">")
		.append(fmt.format(new Date(formStartDate))).append("</div>\n");
	// end date
	retVal.append("<div title=\"endDate\">")
		.append(fmt.format(new Date(formEndDate))).append("</div>\n");
	// user name
	retVal.append("<div title=\"userName\">").append(userName)
		.append("</div>\n");
	// cells
	retVal.append(getHTMLCellString());
	// columns
	retVal.append(getHTMLColumnString());
	// stats
	retVal.append("<div title=\"stats\">\n");
	for (Stats fs : form.stats) {
	    retVal.append(getHTMLStatsString(fs));
	}
	retVal.append("</div>");
	return retVal.toString();
    }


    private String getHTMLCellString() {
	ArrayList<FormCell> cells = form.cells;
	int cellLength = cells.size();
	StringBuilder str = new StringBuilder();
	str.append("<div title=\"cells\">\n");
	for (int i = 0; i < cellLength; i++) {
	    str.append("<div title=\"").append(cells.get(i).name).append("\">")
		    .append(getCellValue(cells.get(i), cells.get(i).type))
		    .append("</div>\n");
	}
	str.append("</div>\n");
	return str.toString();
    }

    private String getHTMLColumnString() {
	StringBuilder sb = new StringBuilder();
	HashSet<String> tbHeader = new HashSet<String>();
	LinkedHashSet<String> usedHeader = new LinkedHashSet<String>();
	ArrayList<String> values = new ArrayList<String>();
	ArrayList<StatsAnswerHolder> sah = new ArrayList<StatsAnswerHolder>();
	for (Object o : columnInfo) {
	    if (o instanceof String) {
		if (o.equals(END_COLUMN)) {
		    values.add(END_COLUMN);
		} else {
		    String[] pieces = ((String) o).split(":");
		    if (pieces.length > 1) {
			usedHeader.add(pieces[0]);
			values.add(pieces[1]);
		    }
		}
	    } else {
		StringBuilder statsString = new StringBuilder();
		for (Stats fs : form.column.stats) {
		    usedHeader.add(fs.name);
		    for (AbstractStatsQuestion fsq : fs.questions) {
			sah = fsq.getResultFromFormAsValue(form,
				(ArrayList<StatsConstraint>) o);
			statsString = new StringBuilder();
			for (StatsAnswerHolder ssah : sah) {
			    tbHeader = new HashSet<String>();
			    statsString.append("<table border=\"1\">\n");
			    SimpleDateFormat fmt = new SimpleDateFormat(
				    "yyyy-MM-dd");
			    String startTime = "";
			    String endTime = "";
			    if (ssah.end != null && ssah.start != null) {
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTimeInMillis(ssah.start);
				c2.setTimeInMillis(ssah.end);
				startTime = fmt.format(new Date(ssah.start));
				if (c1.get(Calendar.DAY_OF_YEAR) != c2
					.get(Calendar.DAY_OF_YEAR)) {
				    endTime = fmt.format(new Date(ssah.end));
				}
			    }
			    if (!startTime.equals("") || !endTime.equals("")) {
				statsString.append("<tr>\n");
				if (!startTime.equals("")) {
				    statsString
					    .append("<td title=\"startTime\">");
				    statsString.append(startTime).append(
					    "</td>\n");
				}
				if (!endTime.equals("")) {
				    statsString
					    .append("<td title=\"endTime\">");
				    statsString.append(endTime).append(
					    "</td>\n");
				}
				statsString.append("</tr>\n");
			    }
			    StringBuilder tds = new StringBuilder();
			    for (QueryResultRow qrr : ssah.table.rows) {
				// tds = new StringBuilder();
				if (tbHeader.size() == 0) {
				    statsString.append("<tr>\n");
				}
				tds.append("<tr>\n");
				for (QueryResultCell qrc : qrr.cells) {
				    if (tbHeader.add(qrc.getColumnname())) {
					statsString.append("<th>");
					statsString.append(qrc.getColumnname());
					statsString.append("</th>\n");
				    }
				    tds.append("<td>");
				    tds.append(qrc.getData());
				    tds.append("</td>\n");
				}
				if (usedHeader.size() > 0) {
				    statsString.append("</tr>\n");
				}
				tds.append("</tr>\n");
			    }
			    statsString.append(tds);
			    statsString.append("</table>\n");
			}
		    }
		}
		values.add(statsString.toString());
	    }
	}
	sb.append("<div title=\"columns\">\n");
	sb.append("<table border=\"1\">\n");
	sb.append("<tr>\n");
	for (String header : usedHeader) {
	    sb.append("<th>").append(header).append("</th>\n");
	}
	sb.append("</tr>\n");
	for (int i = 0; i < values.size(); i += usedHeader.size()) {
	    sb.append("<tr>\n");
	    for (int j = i; j < values.size(); j++) {
		if (values.get(j).equals(END_COLUMN)) {
		    sb.append("</tr>\n");
		    i++;
		    break;
		} else {
		    sb.append("<td>").append(values.get(j)).append("</td>\n");
		}
	    }
	}
	sb.append("</table>\n");
	sb.append("</div>\n");
	return sb.toString();
    }

    /**
     * returns a HTML String representation of the stats of a form.
     * 
     * @return string representation of stats of a form in HTML.
     */
    private String getHTMLStatsString(Stats fs) {
	StringBuilder sb = new StringBuilder();
	StringBuilder tds;
	boolean added = false;
	ArrayList<StatsAnswerHolder> sah = new ArrayList<StatsAnswerHolder>();
	HashSet<String> usedHeader = new HashSet<String>();
	for (AbstractStatsQuestion fsq : fs.questions) {
	    sah = fsq.getResultFromFormAsValue(form);
	    sb.append("<table border=\"1\">\n");
	    sb.append("<tr><td title=\"question\">\n");
	    sb.append(fs.name).append("\n");
	    sb.append("</td></tr>\n");
	    for (StatsAnswerHolder ssah : sah) {
		sb.append("<tr><td>\n");
		usedHeader.clear();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "";
		String endTime = "";
		if (ssah.end != null && ssah.start != null) {
		    Calendar c1 = Calendar.getInstance();
		    Calendar c2 = Calendar.getInstance();
		    c1.setTimeInMillis(ssah.start);
		    c2.setTimeInMillis(ssah.end);
		    startTime = fmt.format(new Date(ssah.start));
		    if (c1.get(Calendar.DAY_OF_YEAR) != c2
			    .get(Calendar.DAY_OF_YEAR)) {
			endTime = fmt.format(new Date(ssah.end));
		    }
		}
		tds = new StringBuilder();
		sb.append("<table border=\"1\">\n");
		if (!startTime.equals("") || !endTime.equals("")) {
		    sb.append("<tr>\n");
		    if (!startTime.equals("")) {
			sb.append("<td title=\"startTime\">").append(startTime)
				.append("</td>\n");
		    }
		    if (!endTime.equals("")) {
			sb.append("<td title=\"endTime\">").append(endTime)
				.append("</td>\n");
		    }
		    sb.append("</tr>\n");
		}
		for (QueryResultRow qrr : ssah.table.rows) {
		    if (usedHeader.size() == 0) {
			sb.append("<tr>\n");
		    }
		    tds.append("<tr>\n");
		    for (QueryResultCell qrc : qrr.cells) {
			if (usedHeader.add(qrc.getColumnname())) {
			    sb.append("<th>");
			    sb.append(qrc.getColumnname());
			    sb.append("</th>\n");
			}
			tds.append("<td>");
			tds.append(qrc.getData());
			tds.append("</td>\n");
		    }
		    if (usedHeader.size() > 0 && added == false) {
			sb.append("</tr>\n");
			added = true;
		    }
		    tds.append("</tr>\n");
		}
		sb.append(tds);
		sb.append("</table>");
		sb.append("</td></tr>\n");
	    }
	    // sah.get(0).table.rows.get(0).cells.get(0).getColumnname();
	    sb.append("</table>\n");
	}
	return sb.toString();
    }

    private String getCellString() {
	ArrayList<FormCell> cells = form.cells;
	int cellLength = cells.size();
	String str = "";
	for (int i = 0; i < cellLength; i++) {
	    String temp = populateCell(cells.get(i));
	    str += temp + "\n";
	}
	return str;
    }


    private String getColumnString() {
	HashSet<String> joinedTables = new HashSet<String>();
	String select = "SELECT ";
	String from = " FROM history history_table "
		+ QueryHelper.joinTable(form.constraints, joinedTables);
	String where = " WHERE history_table.end_time <= " + formEndDate
		+ " AND history_table.end_time >= " + formStartDate;
	
	if (mActivity instanceof ClinicalGuideActivity) {
	    String additionalConstraint = QueryHelper.addToWhere((ClinicalGuideActivity)mActivity, form.constraints);
	    where += additionalConstraint.trim().isEmpty() ? "" : " AND " + additionalConstraint;
	}

	FormColumn column = form.column;
	if (column == null) {
	    return "";
	}
	int columnLength = column.data.size();

	ArrayList<HashMap<String, String>> arrMap = column.savedItems;
	int arrLen = arrMap.size();
	for (int i = 0; i < arrLen;) {
	    HashMap<String, String> map = arrMap.get(i);
	    String savedCol = map.get("columnname");
	    String savedTable = map.get("tablename");
	    if (savedCol != null && savedTable != null) {
		from += " " + QueryHelper.joinTable(savedTable, joinedTables);
		select += " " + savedTable + "." + savedCol + " " + " AS \""
			+ "SAVEDCOLUMN_" + savedTable + "_" + savedCol + "\"";
	    }
	    if (++i < arrLen) {
		select += ", ";
	    }

	}

	if (arrLen > 0 && columnLength > 0) {
	    select += ", ";
	}

	for (int i = 0; i < columnLength;) {
	    HashMap<String, String> data = column.data.get(i);
	    String tablename = data.get("tablename");

	    from += QueryHelper.joinTable(tablename, joinedTables);

	    String columnname = data.get("columnname");

	    if (tablename.equalsIgnoreCase("patient_details_table")
		    && columnname.equalsIgnoreCase("age")) {
		select += " " + tablename + ".birthdate AS \"patient_age\"";
	    } else {

		select += " " + tablename + "." + columnname + " " + " AS "
			+ tablename + "_" + columnname + " ";
	    }
	    if (++i < columnLength) {
		select += ", ";
	    }

	}

	String columnOutput = "";
	QueryResultTable columnResults = db.executeSQLstatement(select + from
		+ where);
	ArrayList<QueryResultRow> rows = columnResults.rows;
	int rowLen = rows.size();
	for (int i = 0; i < rowLen; i++) {
	    ArrayList<QueryResultCell> cells = rows.get(i).cells;
	    ArrayList<HashMap<String, String>> saved = new ArrayList<HashMap<String, String>>();

	    int cellLen = cells.size();
	    for (int j = 0; j < cellLen; j++) {
		QueryResultCell cell = cells.get(j);
		if (cell.getColumnname().startsWith("SAVEDCOLUMN_")) {
		    HashMap<String, String> m = new HashMap<String, String>();
		    m.put("tablename", cell.getColumnname());
		    m.put("value", cell.getData());
		    saved.add(m);
		} else if (cell.getColumnname().equalsIgnoreCase("patient_age")) {
		    long bday = DateHelper.changeTextDateToLong(cell.getData());
		    int age = DateHelper.calculateAge(bday);
		    columnOutput += cell.getColumnname() + ": " + age + "\n";
		} else {
		    columnOutput += cell.getColumnname() + ": "
			    + cell.getData() + "\n";
		}
	    }
	    columnOutput += populateStatsForColumn(saved) + "\n";
	    columnOutput += "===========================\n";
	}

	return columnOutput;
    }

    private String populateStatsForColumn(ArrayList<HashMap<String, String>> saved) {
	String retVal = "";
	int len = saved.size();
	ArrayList<StatsConstraint> arrFc = new ArrayList<StatsConstraint>();
	arrFc.addAll(form.constraints);
	for (int i = 0; i < len; i++) {
	    HashMap<String, String> map = saved.get(i);
	    String table = map.get("tablename");
	    String value = map.get("value");
	    table = table.replace("SAVEDCOLUMN_", "");
	    String[] sArr = table.split("_table_", 2);
	    sArr[0] += "_table";
	    arrFc.add(new StatsConstraint(sArr[0], sArr[1], value, null));

	}

	retVal = getStatsString(form.column.stats, arrFc);

	return retVal;
    }

    private String getStatsString(ArrayList<Stats> statsArr,
	    ArrayList<StatsConstraint> arrFc) {
	String retVal = "";
	int len = statsArr.size();
	for (int i = 0; i < len; i++) {
	    Stats stats = statsArr.get(i);
	    retVal += "\n[" + stats.name.toUpperCase() + "]\n";
	    ArrayList<AbstractStatsQuestion> statsQArr = stats.questions;
	    int qLen = statsQArr.size();
	    for (int j = 0; j < qLen; j++) {
		AbstractStatsQuestion statsQ = statsQArr.get(j);
		String str = statsQ.getResultFromFormAsString(form, arrFc);
		retVal += str + "\n";
	    }
	}
	return retVal;
    }
    /**
     * Retuns a String with a format "[name of cell]: [value of cell]"
     * 
     * @param cell
     *            The cell to turn into string
     * @return A String with a format "[name of cell]: [value of cell]"
     */
    private String populateCell(FormCell cell) {
	String name = "";
	String type = "";
	String value = "";
	StringBuilder retVal = new StringBuilder();

	name = cell.name + ": ";
	type = cell.type;
	if (type.equalsIgnoreCase("input")) {
	    return "";
	}
	value = getCellValue(cell, type);
	retVal.append(name).append(value);
	return retVal.toString();
    }

    private String getCellValue(FormCell cell, String type) {
	String retVal = "";
	if (type.equalsIgnoreCase("query")) {
	    String value = cell.value;
	    String userid = "";
	    if (mActivity instanceof ClinicalGuideActivity) {
		userid = ((ClinicalGuideActivity) mActivity).getUser().id + "";
	    }
	    String query = "SELECT " + value + " FROM user  WHERE user.id = '"
		    + userid + "'";
	    QueryResultTable result = db.executeSQLstatement(query);
	    String val = "";
	    Log.v("cell query", query);
	    int size = result.rows.size();
	    if (size < 1) {
		Toast.makeText(mActivity,
			"Sorry! No " + value + " found with the id " + userid,
			Toast.LENGTH_LONG).show();
	    } else {
		val = result.rows.get(0).cells.get(0).getData(); //get first row first column (there should only be one result)
	    }
	    retVal = val;
	} else if (type.equalsIgnoreCase("defined")) {
	    retVal = cell.value;
	} else if (type.equalsIgnoreCase("current_date")) {
	    retVal = cell.getCurrentDateValue();
	} 
	return retVal;
    }

    
    /**
     * Gets the start day of the form depending on its duration.
     * 
     * @return A long representing milliseconds which can be transformed to
     *         date.
     */
    public long getStartDay() {
	long retVal = -1;
	retVal = new DateHelper(form.duration).getStartDay(form.getEndDate());
	return retVal;
    }

}
