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

public class StatsQuestionExtrema extends AbstractStatsQuestion{

    public static final int MAX = 0;
    public static final int MIN = 1;
    
    public final int type;
    public final StatsSubject subject;
    public final StatsQuestionCount countQuestion;
    
    /**
     * Constructs a stats extrema using a subject. (Example: Max/Min duration of assessment)
     * @param mActivity ClinicalGuideActivity
     * @param type StatsQuestionExtrema.MAX or StatsQuestionExtrema.MIN
     * @param subject The subject to get the max/min from
     * @param constraints Any constraints
     * @param timespan Timespan of the question
     * @param compConstraint any CompareConstraint
     */
    public StatsQuestionExtrema(ClinicalGuideActivity mActivity, int type, StatsSubject subject, ArrayList<StatsConstraint> constraints,  StatsTimespan timespan, StatsCompareConstraint compConstraint){
	super(mActivity, "average", timespan, constraints, compConstraint);
	this.subject = subject;
	countQuestion = null;
	this.type = type;
    }
    
    /**
     * Constructs a stats average using a StatsQuestionCount. (Example: Max/Min Dots patient "last year per day")
     * @param mActivity ClinicalGuideActivity
     * @param type StatsQuestionExtrema.MAX or StatsQuestionExtrema.MIN
     * @param countQuestion The StatsQuestionCount to get the max/min from
     * @param constraints Any constraints
     * @param timespan Timespan of the question
     * @param compConstraint any CompareConstraint
     */
    public StatsQuestionExtrema(ClinicalGuideActivity mActivity, int type, StatsQuestionCount countQuestion, ArrayList<StatsConstraint> constraints,  StatsTimespan timespan, StatsCompareConstraint compConstraint){
	super(mActivity, "average", timespan, constraints, compConstraint);
	this.subject = null;
	this.countQuestion = countQuestion;
	this.type = type;
    }

    public String getResultAsString(
	    ArrayList<StatsConstraint> additionalConstraint) {
	ArrayList<StatsAnswerHolder> answers = getResultAsValue(additionalConstraint);
	int total = computeValue(answers);
	String label = "Undefined";
	if(type == MAX){
	    label = "Maximum";
	} else if(type == MIN){
	    label = "Minimum";
	}
	answers.add(new StatsAnswerHolder(new QueryResultTable(new QueryResultCell(label, total + "")), null, null));
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
     * gets the max/min value in the StatsAnswerHolder
     * @param arrHolder ArrayList of results to get the max/min from
     * @return the max/min value found in the ArrayList of StatsAnswerHolder
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
		if(arrCellLen > 0){
		    
		    if(j == 0 && i == 0)
			retVal = Integer.parseInt(arrCell.get(0).getData());
		    
		    for(int k = 0; k < arrCellLen; k++){
			int value = Integer.parseInt(arrCell.get(k).getData());
			if(type == MAX && value > retVal){
			    retVal = value;    
			} else if(type == MIN && value < retVal){
			    retVal = value;   
			}
		    }
		}
	    }
	}
	return retVal;
    }
    
}

