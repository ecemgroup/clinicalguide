package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

public class StatsQuestionPercentage extends AbstractStatsQuestion{

    public final StatsQuestionCount mainTarget;
    public final ArrayList<StatsQuestionCount> otherTargets;
    public StatsQuestionPercentage(ClinicalGuideActivity mActivity, StatsQuestionCount mainTarget, ArrayList<StatsQuestionCount> otherTargets, ArrayList<StatsConstraint> constraints, StatsTimespan timespan, StatsCompareConstraint compConstraint){
	super(mActivity, "percentage", timespan, constraints, compConstraint);
	this.otherTargets = otherTargets;
	this.mainTarget = mainTarget;
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
	int countLen = otherTargets.size();
	ArrayList<ArrayList<StatsAnswerHolder>> totalAnswers = new ArrayList<ArrayList<StatsAnswerHolder>>();
	int totalCount = 0;
	int targetCount = 0;
	for (int i = 0; i < countLen; i++) {
	    StatsQuestionCount origCount;
	    StatsQuestionCount newCount;
	    origCount = otherTargets.get(i);
	    origCount.constraints.addAll(constraints);
	    newCount  = origCount;
	    if(timespan != null){
		newCount = new StatsQuestionCount(mActivity, origCount.label, origCount.subject, origCount.constraints, timespan, origCount.compConstraint);
	    }
	    
	    ArrayList<StatsAnswerHolder> arrHolder = newCount.getResultAsValue();
	    int value = newCount.computeValue(arrHolder);
	    totalCount += value;
	    if(origCount == mainTarget){
		targetCount = value;
	    }
	    totalAnswers.add(arrHolder);
	}
	
	int answersLen = totalAnswers.size();
	for(int i = 0; i < answersLen; i++){
	    ArrayList<StatsAnswerHolder> answers = totalAnswers.get(i);
	    retVal.addAll(answers);
	}
	
	double percentage;
	if(totalCount == 0){
	    percentage = 0;
	} else {
	    percentage = (double)targetCount / totalCount * 100;
	}
	QueryResultTable table = new QueryResultTable(new QueryResultCell("Percentage", String.format("%.2f",percentage) + "%"));
	retVal.add(new StatsAnswerHolder(table, null, null));
	
	return retVal;
    }

}
