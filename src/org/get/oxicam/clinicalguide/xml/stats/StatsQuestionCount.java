package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.ParserHelper;
import org.get.oxicam.clinicalguide.xml.data.FormQuery;
import org.get.oxicam.clinicalguide.xml.forms.Form;
import org.get.oxicam.clinicalguide.xml.query.QueryHelper;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultRow;

import android.util.Log;

public class StatsQuestionCount extends AbstractStatsQuestion {

    public final StatsSubject subject;
    public final String label;
    private Form form;
    private boolean fromForm = false;

    public StatsQuestionCount(ClinicalGuideActivity mActivity,
	    String label,
	    StatsSubject subject,
	    ArrayList<StatsConstraint> constraints,
	    StatsTimespan timespan,
	    StatsCompareConstraint compConstraint) {
	super(mActivity, "count", timespan, constraints, compConstraint);
	this.subject = subject;
	
	if(label != null && !label.trim().isEmpty()){
	    this.label = label;
	} else {
	    this.label = "count";
	}
    }

    public String getResultAsString(ArrayList<StatsConstraint> additionalConstraint) {
	String retVal = "";
	ArrayList<StatsAnswerHolder> answers = getResultAsValue(additionalConstraint);
	int len = answers.size();
	for(int i = 0; i < len; i++){
	    StatsAnswerHolder ans = answers.get(i);
	    retVal += ans.toString();
	}
	return retVal;
    }

    @Override
    public ArrayList<StatsAnswerHolder> getResultAsValue(
	    ArrayList<StatsConstraint> additionalConstraint) {
	Set<String> tables = new HashSet<String>();
	String retVal = "";
	String select = " SELECT ";
	String from = " FROM ";
	String where = " WHERE 1=1 ";
	ArrayList<StatsConstraint> freshConstraints = new  ArrayList<StatsConstraint>();
	ArrayList<StatsSubject> checkTables = new ArrayList<StatsSubject>();
	ArrayList<StatsSubject> compTables = new ArrayList<StatsSubject>();
	if(additionalConstraint != null){
	    freshConstraints.addAll(additionalConstraint);
	}
	int subjLen = 0;
	boolean moreThanOneTable = false;
	if(subject == null){
	    select += " COUNT(*) AS \"" + label + "\" ";
	} else {
	    checkTables.add(subject);
	    select += " COUNT(DISTINCT " + subject.tablename + "." + subject.columnname + ") AS \"" + label + "\" ";
	}
	if(constraints != null){
	    freshConstraints.addAll(constraints);
	}
	int constLen = freshConstraints.size();

	checkTables.addAll(freshConstraints);
	if(compConstraint != null){
	    compTables = compConstraint.getTablesAndColumns();
	    checkTables.addAll(compTables);
	}
	
	int checkTablesLen = checkTables.size();
	if (checkTablesLen == 0) {
	    //return "No columns selected.";
	    return queryDatabase(new FormQuery("select COUNT (*) AS everything", "",""));
	}
	if (ParserHelper.moreThanOneTable(checkTables) || timespan != null) {
	    from += " history history_table ";
	    moreThanOneTable = true;
	} else if(checkTables.size() > 0){
	    StatsSubject constraint = checkTables.get(0);
	    from += " " + constraint.tablename.substring(0, constraint.tablename.length() - 6) + " " + constraint.tablename + " ";
	    tables.add(constraint.tablename.substring(0, constraint.tablename.length() - 6));
	} else if(compTables.size() > 0){
	    String tablename = compTables.get(0).tablename;
	    from += " " + tablename.substring(0, tablename.length() - 6) + " " + tablename + " " ;
	    tables.add(tablename.substring(0, tablename.length() - 6));
	}
	
	if(subject != null){
	    from += QueryHelper.joinTable(subject.tablename, tables);
	}
	
	for (int i = 0; i < constLen; i++) {
	    StatsConstraint fc = freshConstraints.get(i);
	    where += " AND " + QueryHelper.addToWhere(mActivity, fc);
	    from += " " + QueryHelper.joinTable(fc.tablename, tables);
	}

	FormQuery fq = addCompareConstraints(tables);

	if (fq != null) {
	    from += " " + fq.getFrom() + " ";
	    where += " AND " + fq.getWhere() + " ";
	}
	fq = new FormQuery(select, from, where);
	Log.v("COUNT QUERY before timespan", fq.toString());
	return getTimeSpanDetails(fq);
    }

    public int computeValue(ArrayList<StatsAnswerHolder> arrHolder){
	int retVal = 0;
	int arrHolderLen = arrHolder.size();
	for(int i = 0; i < arrHolderLen; i++){
	    StatsAnswerHolder holder = arrHolder.get(i);
	    ArrayList<QueryResultRow> arrRows = holder.table.rows;
	    int arrRowsLen = arrRows.size();
	    for(int j = 0; j < arrRowsLen; j++){
		ArrayList<QueryResultCell> arrCell = arrRows.get(j).cells;
		int arrCellLen = arrCell.size();
		for(int k = 0; k < arrCellLen; k++){
		    retVal += Integer.parseInt(arrCell.get(k).getData());
		}
	    }
	}
	return retVal;
    }
}
