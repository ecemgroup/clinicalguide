package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.ParserHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class StatsQuestionFactory {

    private static int typeInt = -1;
    /**
     * Creates the specific question object depending on the type attribute of
     * the question
     * 
     * @param e Element with a tagname "question"
     * @return A child class of FormStatsQuestion depending on the attributes and childnodes.
     */
    public static AbstractStatsQuestion createQuestion(Element e, ClinicalGuideActivity mActivity) {
	if (!e.getTagName().equalsIgnoreCase("question")) {
	    throw new IllegalArgumentException("Not a question tag");
	}
	
	String type = ParserHelper.requiredAttributeGetter(e, "type");
	typeInt = AbstractStatsQuestion.getQuestionTypeInt(type);
	switch (typeInt) {
	case AbstractStatsQuestion.QUESTION_TYPE_COUNT:
	    return createCountQuestion(e, mActivity);
	case AbstractStatsQuestion.QUESTION_TYPE_LIST:
	    return createListQuestion(e, mActivity);
	case AbstractStatsQuestion.QUESTION_TYPE_MIN:
	case AbstractStatsQuestion.QUESTION_TYPE_MAX:
	    return createExtremaQuestion(e, mActivity);
	case AbstractStatsQuestion.QUESTION_TYPE_PERCENTAGE:
	    return createPercentageQuestion(e, mActivity);
	case AbstractStatsQuestion.QUESTION_TYPE_RATIO:
	    return createRatioQuestion(e, mActivity);
	case AbstractStatsQuestion.QUESTION_TYPE_AVERAGE:
	    return createAverageQuestion(e, mActivity);   
	default:
	    throw new IllegalArgumentException("Question type not existing");
	}
    }
    
    private static AbstractStatsQuestion createListQuestion(Element e, ClinicalGuideActivity mActivity){
	AbstractStatsQuestion retVal = null;
	
	ArrayList<StatsSubject> arrSubj = new ArrayList<StatsSubject>();
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();
	StatsCompareConstraint compareConstraint = null;
	NodeList subjects = e.getElementsByTagName("subject");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");
	
	int subjLen = subjects.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1 || compLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan or 1 comparecontraint in a question");
	}
	
	for(int i = 0; i < subjLen; i++){
	    arrSubj.add(ParserHelper.getStatsSubjectDetails((Element) subjects.item(i)));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	
	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	

	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	retVal = new StatsQuestionList(mActivity, arrSubj, e.getAttribute("distinct").equalsIgnoreCase("true"), arrConst, qTimespan, compareConstraint);    
	
	return retVal;
    }
    
    private static AbstractStatsQuestion createRatioQuestion(Element e, ClinicalGuideActivity mActivity){
	AbstractStatsQuestion retVal = null;
	
	ArrayList<StatsQuestionCount> arrCounts = new ArrayList<StatsQuestionCount>();
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();

	StatsCompareConstraint compareConstraint = null;
	NodeList counts = e.getElementsByTagName("count");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	
	int subjLen = counts.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan in a question");
	}
	
	for(int i = 0; i < subjLen; i++){
	    arrCounts.add(createSubCountQuestion((Element) counts.item(i), mActivity));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	

	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	
	
	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	
	retVal = new StatsQuestionRatio(mActivity, arrCounts, arrConst, qTimespan, compareConstraint);
	return retVal;
    }
    
    private static StatsQuestionPercentage createPercentageQuestion(Element e, ClinicalGuideActivity mActivity){
	StatsQuestionPercentage retVal = null;
	StatsQuestionCount mainTarget = null;
	ArrayList<StatsQuestionCount> arrCounts = new ArrayList<StatsQuestionCount>();
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();

	StatsCompareConstraint compareConstraint = null;
	NodeList mainTargetList = e.getElementsByTagName("target");
	NodeList otherList = e.getElementsByTagName("others");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	
	int subjLen = mainTargetList.getLength();
	int otherLen = otherList.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan in a question");
	}
	
	if(subjLen != 1){
	    throw new IllegalArgumentException("There should be one target in a percentage question");
	}
	
	mainTarget = createSubCountQuestion((Element)mainTargetList.item(0), mActivity);
	arrCounts.add(mainTarget);
	
	for(int i = 0; i < otherLen; i++){
	    arrCounts.add(createSubCountQuestion((Element) otherList.item(i), mActivity));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	

	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	
	
	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	
	retVal = new StatsQuestionPercentage(mActivity, mainTarget, arrCounts, arrConst, qTimespan, compareConstraint);
	return retVal;
    }
    
    private static StatsQuestionCount createCountQuestion(Element e, ClinicalGuideActivity mActivity){
	
	StatsSubject distinctSubj = null;
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();
	StatsCompareConstraint compareConstraint = null;
	NodeList distinctNl = e.getElementsByTagName("distinct");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");
	
	int distinctLen = distinctNl.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1 || compLen > 1 || distinctLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan or 1 comparecontraint in a question");
	}
	
	if(distinctLen == 1){
	    distinctSubj = ParserHelper.getStatsSubjectDetails((Element) distinctNl.item(0));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	
	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	

	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	String label = e.getAttribute("label");
	return new StatsQuestionCount(mActivity, label, distinctSubj, arrConst, qTimespan, compareConstraint);
    }
    
    private static StatsQuestionCount createSubCountQuestion(Element e, ClinicalGuideActivity mActivity){
	
	StatsSubject distinctSubj = null;
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();
	StatsCompareConstraint compareConstraint = null;
	NodeList distinctNl = e.getElementsByTagName("countdistinct");
	NodeList constraints = e.getElementsByTagName("countconstraint");
	NodeList compareConstraintNodeList = e.getElementsByTagName("countcompareconstraint");
	NodeList timespan = e.getElementsByTagName("counttimespan");
	
	int distinctLen = distinctNl.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1 || compLen > 1 || distinctLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan or 1 comparecontraint in a question");
	}
	
	if(distinctLen == 1){
	    distinctSubj = ParserHelper.getStatsSubjectDetails((Element) distinctNl.item(0));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	
	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	

	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	String label = e.getAttribute("label");
	return new StatsQuestionCount(mActivity, label, distinctSubj, arrConst, qTimespan, compareConstraint);
    }
    
    private static AbstractStatsQuestion createAverageQuestion(Element e, ClinicalGuideActivity mActivity){
	AbstractStatsQuestion retVal = null;
	
	ArrayList<StatsSubject> arrSubj = new ArrayList<StatsSubject>();
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();
	StatsCompareConstraint compareConstraint = null;
	NodeList subjects = e.getElementsByTagName("subject");
	NodeList countQuestions = e.getElementsByTagName("count");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");

	int subjLen = subjects.getLength();
	int countLen = countQuestions.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1 || compLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan or 1 comparecontraint in a question");
	}
	
	

	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	

	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	
	
	if(subjLen == 1){
	    StatsSubject subj = ParserHelper.getStatsSubjectDetails((Element) subjects.item(0));
	    retVal = new StatsQuestionAverage(mActivity, subj, arrConst, qTimespan, compareConstraint);
	} else if(countLen == 1){
	    StatsQuestionCount countQuestion =createSubCountQuestion((Element) countQuestions.item(0), mActivity);
	    retVal = new StatsQuestionAverage(mActivity, countQuestion, arrConst, qTimespan, compareConstraint);
	} else {
	    throw new IllegalArgumentException("Average question can only have either 1 <subject> or 1 <count> tag");
	}
	
	for(int i = 0; i < subjLen; i++){
	    arrSubj.add(ParserHelper.getStatsSubjectDetails((Element) subjects.item(i)));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	
	
	
	return retVal;
    }
    
    
    private static StatsQuestionExtrema createExtremaQuestion(Element e, ClinicalGuideActivity mActivity){
	StatsQuestionExtrema retVal = null;
	
	ArrayList<StatsSubject> arrSubj = new ArrayList<StatsSubject>();
	ArrayList<StatsConstraint> arrConst = new ArrayList<StatsConstraint>();
	StatsCompareConstraint compareConstraint = null;
	NodeList subjects = e.getElementsByTagName("subject");
	NodeList countQuestions = e.getElementsByTagName("count");
	NodeList constraints = e.getElementsByTagName("statsconstraint");
	NodeList compareConstraintNodeList = e.getElementsByTagName("compareconstraint");
	NodeList timespan = e.getElementsByTagName("timespan");

	

	String type = ParserHelper.requiredAttributeGetter(e, "type");
	int extremaType = -1;
	type = type.trim();
	if(type.equalsIgnoreCase("max")){
	    extremaType = StatsQuestionExtrema.MAX;
	} else if(type.equalsIgnoreCase("min")){
	    extremaType = StatsQuestionExtrema.MIN;
	}
	int subjLen = subjects.getLength();
	int countLen = countQuestions.getLength();
	int constLen = constraints.getLength();
	int spanLen = timespan.getLength();
	int compLen = compareConstraintNodeList.getLength();
	
	if(spanLen > 1 || compLen > 1){
	    throw new IllegalArgumentException("There can only be one timespan or 1 comparecontraint in a question");
	}
	
	
	if(compLen == 1){
	    Element compConstNode = (Element)compareConstraintNodeList.item(0);
	    compareConstraint = ParserHelper.getCompareConstraintDetails(compConstNode);
	}
	

	StatsTimespan qTimespan = spanLen == 1 ? ParserHelper.getTimespanDetails((Element)timespan.item(0)) : null;
	
	
	if(subjLen == 1){
	    StatsSubject subj = ParserHelper.getStatsSubjectDetails((Element) subjects.item(0));
	    retVal = new StatsQuestionExtrema(mActivity, extremaType, subj, arrConst, qTimespan, compareConstraint);
	} else if(countLen == 1){
	    StatsQuestionCount countQuestion =createSubCountQuestion((Element) countQuestions.item(0), mActivity);
	    retVal = new StatsQuestionExtrema(mActivity, extremaType, countQuestion, arrConst, qTimespan, compareConstraint);
	} else {
	    throw new IllegalArgumentException("Extrema question can only have either 1 <subject> or 1 <count> tag");
	}
	
	for(int i = 0; i < subjLen; i++){
	    arrSubj.add(ParserHelper.getStatsSubjectDetails((Element) subjects.item(i)));
	}
	
	for(int i = 0; i < constLen; i++){
	    arrConst.add(ParserHelper.getStatsConstraintDetails((Element)constraints.item(i)));
	}
	
	
	
	return retVal;
    }

}
