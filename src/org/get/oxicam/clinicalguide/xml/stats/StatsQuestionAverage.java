package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.ParserHelper;
import org.get.oxicam.clinicalguide.xml.data.FormQuery;
import org.get.oxicam.clinicalguide.xml.query.QueryHelper;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultRow;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

import android.util.Log;

public class StatsQuestionAverage extends AbstractStatsQuestion{

    public final StatsSubject subject;
    public final StatsQuestionCount countQuestion;
    
    /**
     * Constructs a stats average using a subject. (Example: Average duration of assessment)
     * @param mActivity ClinicalGuideActivity
     * @param subject The subject to get the average from
     * @param constraints Any constraints
     * @param timespan Timespan of the question
     * @param compConstraint any CompareConstraint
     */
    public StatsQuestionAverage(ClinicalGuideActivity mActivity, StatsSubject subject, ArrayList<StatsConstraint> constraints,  StatsTimespan timespan, StatsCompareConstraint compConstraint){
	super(mActivity, "average", timespan, constraints, compConstraint);
	this.subject = subject;
	countQuestion = null;
    }
    
    /**
     * Constructs a stats average using a StatsQuestionCount. (Example: Average Dots patient "last year per day")
     * @param mActivity ClinicalGuideActivity
     * @param countQuestion The StatsQuestionCount to get the average from
     * @param constraints Any constraints
     * @param timespan Timespan (this timespan will override any counttimespan in the StatsQuestionCount)
     * @param compConstraint any CompareConstraint
     */
    public StatsQuestionAverage(ClinicalGuideActivity mActivity, StatsQuestionCount countQuestion, ArrayList<StatsConstraint> constraints,  StatsTimespan timespan, StatsCompareConstraint compConstraint){
	super(mActivity, "average", timespan, constraints, compConstraint);
	this.subject = null;
	this.countQuestion = countQuestion;
    }

    @Override
    public String getResultAsString(
	    ArrayList<StatsConstraint> additionalConstraint) {
	ArrayList<StatsAnswerHolder> answers = getResultAsValue(additionalConstraint);
	double total = computeValue(answers);
	int count = computeNumber(answers);
	double average;
	if(count == 0){
	    average = 0;
	} else {
	    average = total / count;
	}
	
	answers.add(new StatsAnswerHolder(new QueryResultTable(new QueryResultCell("Total", total + "")), null, null));
	answers.add(new StatsAnswerHolder(new QueryResultTable(new QueryResultCell("Average", String.format("%.2f", average))), null, null));
	String retVal = "";
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
	if(subject != null){
	    return getSubjectAverage(additionalConstraint);
	} else {
	    return getCountAverage(additionalConstraint);
	}
    }
    
    /**
     * 
     * @param additionalConstraint
     * @return
     */
    private ArrayList<StatsAnswerHolder> getCountAverage(
	    ArrayList<StatsConstraint> additionalConstraint){
	
	StatsQuestionCount count = countQuestion;
	count.constraints.addAll(constraints);
	if(timespan != null){
	    count = new StatsQuestionCount(mActivity, count.label, countQuestion.subject, countQuestion.constraints, timespan, countQuestion.compConstraint);
	}
	return count.getResultAsValue();
    }
    
    private ArrayList<StatsAnswerHolder> getSubjectAverage(
	    ArrayList<StatsConstraint> additionalConstraint){
	Set<String> tables = new HashSet<String>();
	String select = " SELECT ";
	String from = " FROM ";
	String where = " WHERE 1=1 ";
	ArrayList<StatsConstraint> freshConstraints = new ArrayList<StatsConstraint>();
	boolean moreThanOneTable = false;
	
	if(constraints != null){
	    freshConstraints.addAll(constraints);
	}
	if(additionalConstraint != null){
	    freshConstraints.addAll(additionalConstraint);
	}
	ArrayList<StatsSubject> checkTables = new ArrayList<StatsSubject>();
	ArrayList<StatsSubject> compTables = new ArrayList<StatsSubject>();
	checkTables.addAll(freshConstraints);
	if(subject != null){
	    checkTables.add(subject);
	}
	if(compConstraint != null){
	    compTables = compConstraint.getTablesAndColumns();
	    checkTables.addAll(compTables);
	}
	if(ParserHelper.moreThanOneTable(checkTables) || timespan != null){
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
	
	int constLen = freshConstraints.size();
	select += QueryHelper.addToSelect(subject);
	if(moreThanOneTable){
	    from += " " + QueryHelper.joinTable(subject.tablename, tables);  
	}
	
	for(int i = 0; i < constLen; i++){
	    StatsConstraint fc = freshConstraints.get(i);
	    from += " " + QueryHelper.joinTable(fc.tablename, tables);
	    where += " AND " + QueryHelper.addToWhere(mActivity, fc);
	}
	
	
	FormQuery fq = addCompareConstraints(tables);
	if(fq != null){
	from += " " + fq.getFrom() + " ";
	where += " AND " + fq.getWhere() + " ";
	}
	fq= new FormQuery(select, from, where);
	
	Log.v("question list query", fq.toString());
	
	return getTimeSpanDetails(fq);
    }
    
    /**
     * gets the total value in the StatsAnswerHolder (adds everything)
     * @param arrHolder ArrayList of results to get the total value from
     * @return the total value found in the ArrayList of StatsAnswerHolder (then divide this with the computeNumber())
     */
    private int computeValue(ArrayList<StatsAnswerHolder> arrHolder){
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
    
    /**
     * Counts the number ti divide the total from
     * @param arrHolder ArrayList of results to get the number of total values.
     * @return The total number to divide to
     */
    private int computeNumber(ArrayList<StatsAnswerHolder> arrHolder){
	int retVal = 0;
	int arrHolderLen = arrHolder.size();
	for(int i = 0; i < arrHolderLen; i++){
	    StatsAnswerHolder holder = arrHolder.get(i);
	    ArrayList<QueryResultRow> arrRows = holder.table.rows;
	    int arrRowsLen = arrRows.size();
	    for(int j = 0; j < arrRowsLen; j++){
		ArrayList<QueryResultCell> arrCell = arrRows.get(j).cells;
		retVal += arrCell.size();
	    }
	}
	return retVal;
    }
}
