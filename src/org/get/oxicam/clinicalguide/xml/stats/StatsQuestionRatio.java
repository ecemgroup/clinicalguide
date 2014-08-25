package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

public class StatsQuestionRatio extends AbstractStatsQuestion {

    public final ArrayList<StatsQuestionCount> counts;

    public StatsQuestionRatio(ClinicalGuideActivity mActivity,
	    ArrayList<StatsQuestionCount> counts,
	    ArrayList<StatsConstraint> constraints,
	    StatsTimespan timespan,
	    StatsCompareConstraint compConstraint) {
	super(mActivity, "ratio", timespan, constraints, compConstraint);
	this.counts = counts;
    }

    public String getResultAsString(ArrayList<StatsConstraint> additionalConstraints) {
	ArrayList<StatsAnswerHolder> answers = getResultAsValue(additionalConstraints);
	String retVal = "";;
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
	ArrayList<StatsAnswerHolder> retVal = new ArrayList<StatsAnswerHolder>();
	
	int countLen = counts.size();
	
	ArrayList<Integer> values = new ArrayList<Integer>();
	ArrayList<ArrayList<StatsAnswerHolder>> totalAnswers = new ArrayList<ArrayList<StatsAnswerHolder>>();
	for (int i = 0; i < countLen; i++) {
	    StatsQuestionCount s = counts.get(i);
	    s.constraints.addAll(constraints);
	    if(timespan != null){
		s = new StatsQuestionCount(mActivity, s.label, s.subject, s.constraints, timespan, s.compConstraint);
	    }
	    ArrayList<StatsAnswerHolder> arr = s.getResultAsValue();
	    values.add(s.computeValue(arr));
	    totalAnswers.add(s.getResultAsValue());
	}
	
	int answersLen = totalAnswers.size();
	for(int i = 0; i < answersLen; i++){
	    ArrayList<StatsAnswerHolder> answers = totalAnswers.get(i);
	    retVal.addAll(answers);
	}
	
	int valuesLen = values.size();
	String result = "";
	for(int i = 0; i < valuesLen;){
	    result += values.get(i);
	    if(++i < valuesLen){
		result += " : ";
	    }
	}
	
	StatsAnswerHolder ratio = new StatsAnswerHolder(new QueryResultTable(new QueryResultCell("Ratio", result)), null, null);
	retVal.add(ratio);
	
	return retVal;
    }

}
