package org.get.oxicam.clinicalguide.xml.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.xml.DateHelper;
import org.get.oxicam.clinicalguide.xml.data.FormQuery;
import org.get.oxicam.clinicalguide.xml.forms.Form;
import org.get.oxicam.clinicalguide.xml.query.QueryHelper;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

import android.util.Log;

public abstract class AbstractStatsQuestion {

    public static final int QUESTION_TYPE_COUNT = 0;
    public static final int QUESTION_TYPE_LIST = 1;
    public static final int QUESTION_TYPE_MIN = 2;
    public static final int QUESTION_TYPE_MAX = 3;
    public static final int QUESTION_TYPE_RATIO = 4;
    public static final int QUESTION_TYPE_PERCENTAGE = 5;
    public static final int QUESTION_TYPE_AVERAGE = 6;

    public final String type;
    public final StatsTimespan timespan;
    public final ArrayList<StatsConstraint> constraints;
    public final StatsCompareConstraint compConstraint;
    protected final ClinicalGuideActivity mActivity;
    protected boolean fromForm;
    protected Form form;

    public AbstractStatsQuestion(ClinicalGuideActivity mActivity, String type,
	    StatsTimespan timespan,
	    ArrayList<StatsConstraint> constraints,
	    StatsCompareConstraint compConstraint) {
	this.type = type;
	this.timespan = timespan;
	this.mActivity = mActivity;
	this.constraints = constraints;
	this.compConstraint = compConstraint;
	form = null;
	fromForm = false;
    }
    
    /**
     * Gets the result of the question that is inside stats inside clinicalguidestats.xml  as values. (useful if you want to do something with the values)
     * Use this if you want to get the values and use the values for anything else.
     * @param additionalConstraint Any additional constraint you want to have in the stats. Put null if none.
     * @return An arraylist of StatsAnswerHolder that will contain the values of the result of the question
     */
    public abstract ArrayList<StatsAnswerHolder> getResultAsValue(
	    ArrayList<StatsConstraint> additionalConstraint);

    
    /**
     * Gets the result of the question that is inside stats inside clinicalguidestats.xml as a single string. (useful for just printing)
     * Use this if you want to get the values and use the values for anything else.
     * @param additionalConstraint Any additional constraint you want to have in the stats. Put null if none.
     * @return An single string that is formatted depending on the question.
     */
    public abstract String getResultAsString(
	    ArrayList<StatsConstraint> additionalConstraint);

    /**
     * Gets the result of the question that is inside a stats that is inside clinicalguideforms.xml
     * and add any additional constraints.
     * @param f The Form used where the stats of this question resides
     * @param arrFc Any additional constraints, put null if none.
     * @return Result of the question.
     */
    public ArrayList<StatsAnswerHolder> getResultFromFormAsValue(Form f,
	    ArrayList<StatsConstraint> arrFc) {
	form = f;
	fromForm = true;
	ArrayList<StatsConstraint> arr = new ArrayList<StatsConstraint>();
	if (arrFc != null) {
	    arr.addAll(arrFc);
	}
	if (form.constraints != null) {
	    arr.addAll(form.constraints);
	}
	return getResultAsValue(arr);
    }

    /**
     * Gets the result of the question that is inside a stats that is inside clinicalguideforms.xml
     * @param f The Form used where the stats of this question resides
     * @return Result of the question.
     */
    public ArrayList<StatsAnswerHolder> getResultFromFormAsValue(Form f) {
	form = f;
	fromForm = true;
	return getResultAsValue(form.constraints);
    }

    /**
     * Gets the result of the question that is inside a stats that is inside clinicalguideforms.xml
     * with add any additional constraints. Result will be String and the formatting will depend on the type of the question.
     * @param f The Form used where the stats of this question resides
     * @param arrFc Any additional constraints, put null if none.
     * @return Result of the question.
     */
    public String getResultFromFormAsString(Form f,
	    ArrayList<StatsConstraint> arrFc) {
	form = f;
	fromForm = true;
	ArrayList<StatsConstraint> arr = new ArrayList<StatsConstraint>();
	if (arrFc != null) {
	    arr.addAll(arrFc);
	}
	if (form.constraints != null) {
	    arr.addAll(form.constraints);
	}
	return getResultAsString(arr);
    }
    
    /**
     * Gets the result of the question that is inside a stats that is inside clinicalguideforms.xml
     * Result will be String and the formatting will depend on the type of the question.
     * @param f The Form used where the stats of this question resides
     * @return Result of the question.
     */
    public String getResultFromFormAsString(Form f) {
	form = f;
	fromForm = true;
	return getResultAsString(form.constraints);
    }

    
    /**
     * Gets the result of the question that is inside stats inside clinicalguidestats.xml  as values. (useful if you want to do something with the values)
     * Use this if you want to get the values and use the values for anything else.
     * @return An arraylist of StatsAnswerHolder that will contain the values of the result of the question
     */
    public ArrayList<StatsAnswerHolder> getResultAsValue() {
	return getResultAsValue(null);
    }

    /**
     * Gets the result of the question that is inside stats inside clinicalguidestats.xml as a single string. (useful for just printing)
     * Use this if you want to get the values and use the values for anything else.
     * @return An single string that is formatted depending on the question.
     */
    public String getResultAsString() {
	return getResultAsString(null);
    }


    /**
     * Changes string question type to int (eg. "count" will be FormStatsQuestion.QUESTION_TYPE_COUNT")
     * @param type "count", "list", "max", "percentage", "ratio", "average"
     * @return the int representation of the type or -1 if the type is not valid
     */
    public static int getQuestionTypeInt(String type) {
	int retVal = -1;
	type = type.trim();
	if (type.equalsIgnoreCase("count")) {
	    retVal = QUESTION_TYPE_COUNT;
	} else if (type.equalsIgnoreCase("list")) {
	    retVal = QUESTION_TYPE_LIST;
	} else if (type.equalsIgnoreCase("min")) {
	    retVal = QUESTION_TYPE_MIN;
	} else if (type.equalsIgnoreCase("max")) {
	    retVal = QUESTION_TYPE_MAX;
	} else if (type.equalsIgnoreCase("percentage")) {
	    retVal = QUESTION_TYPE_PERCENTAGE;
	} else if (type.equalsIgnoreCase("ratio")) {
	    retVal = QUESTION_TYPE_RATIO;
	} else if (type.equalsIgnoreCase("average")) {
	    retVal = QUESTION_TYPE_AVERAGE;
	}
	return retVal;
    }
    /**
     * Changes int question type to string (eg. FormStatsQuestion.QUESTION_TYPE_COUNT" will be "count" )
     * @param type FormStatsQuestion.QUESTION_TYPE_COUNT, etc...
     * @return the string representation of the type or null if the type is not valid
     */
    public static String getQuestionTypeString(int type) {
	String retVal = null;
	switch (type) {
	case QUESTION_TYPE_COUNT:
	    retVal = "count";
	    break;
	case QUESTION_TYPE_LIST:
	    retVal = "list";
	    break;
	case QUESTION_TYPE_MAX:
	    retVal = "max";
	    break;
	case QUESTION_TYPE_MIN:
	    retVal = "min";
	    break;
	case QUESTION_TYPE_PERCENTAGE:
	    retVal = "percentage";
	    break;
	case QUESTION_TYPE_RATIO:
	    retVal = "ratio";
	    break;
	case QUESTION_TYPE_AVERAGE:
	    retVal = "average";
	    break;
	}

	return retVal;
    }

    /**
     * Gets the timespan details to be added to the constraint. If this is not called, timespan is ignored as a constraint.
     * @param fq FormQuery so far without the timespan constraint.
     * @return The result of the query with the added timespan constraint.
     */
    protected ArrayList<StatsAnswerHolder> getTimeSpanDetails(FormQuery fq) {
	StatsTimespan timeMap = timespan;
	if (timeMap == null) {
	    return queryDatabase(fq);
	}
	int timeType = timeMap.type;
	int timeGroup = timeMap.group;
	int adjust = timeMap.adjust;
	long start = 0;
	long end = 0;

	if (fromForm) {
	    start = form.getStartDate();
	    end = form.getEndDate();
	    fromForm = false;
	} else {
	    DateHelper dh = new DateHelper(timeType, adjust);
	    start = dh.getStartDay();
	    end = dh.getEndDay();
	}

	return getQueryDetails(fq, start, end, timeGroup);
    }

    /**
     * This will compute the end time of the current month
     * @param current long representing a date
     * @return returns the long you need to add from your "current" to go to the first day of next month. 
     * 	       Subtract 1 to get the end day of the current month.
     */
    private long getMonthlyIncrement(long current) {
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(current);
	c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
	c.add(Calendar.MONTH, 1);
	long nextMonth = c.getTimeInMillis();
	return nextMonth - current;
    }

    
    /**
     * Final step. Queries the database with all the details needed (constraints, question type, timespan).
     * Adjusts any groupings (Example: timespan is this month, then group daily)
     * @param fq The FormQuery to query database
     * @param start start day of the timespan
     * @param end end day of the timespan
     * @param type type of the grouping. 
     * @return ArrayList of StatsAnswerHolder that will contain the values
     */
    private ArrayList<StatsAnswerHolder> getQueryDetails(FormQuery fq,
	    long start, long end, int type) {
	Database db = new Database(mActivity);
	ArrayList<StatsAnswerHolder> answers = new ArrayList<StatsAnswerHolder>();

	String from = fq.getFrom();
	Date d = new Date(end);
	Calendar c = Calendar.getInstance();
	c.setTime(d);
	int day = c.get(Calendar.DAY_OF_MONTH);
	int year = c.get(Calendar.YEAR);
	int month = c.get(Calendar.MONTH);
	long increment = end;

	GregorianCalendar gc = new GregorianCalendar(year, month, day);
	gc.add(Calendar.DAY_OF_YEAR, 1);
	end = gc.getTimeInMillis() - 1;

	boolean monthly = false;
	switch (type) {
	case StatsTimespan.GROUP_DAILY:
	    increment = 1000 * 60 * 60 * 24;
	    break;
	case StatsTimespan.GROUP_MONTHLY: //more complicated grouping, call a function
	    monthly = true;
	    break;
	case StatsTimespan.GROUP_WEEKLY:
	    increment = 1000 * 60 * 60 * 24 * 7;
	    break;
	default: //this will only go to the for loop once
	    increment = end - start;
	    break;

	}

	for (long i = start; i < end; i += increment) {
	    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	    String currDate = new SimpleDateFormat("yyyy-MM-dd")
		    .format(new Date(i));
	    String select = fq.getSelect();
	    String where = fq.getWhere();
	    long add;
	    if (monthly) {
		increment = getMonthlyIncrement(i); //call a function to get the end day of the month
	    }
	    add = ((i + increment - 1) > end ? (end - 1) : (i + increment - 1));

	    Log.v("i AND increment", (i + " " + increment));
	    where += " AND history_table.end_time >= " + i
		    + " AND history_table.end_time <= " + (add) + " ";
	    Log.w("FORMTIMESPAN", "start: " + new Date(start).toString() + " "
		    + start + "\n end:" + new Date(end).toString() + " "
		    + (end) + "\n" + "current: " + new Date(i).toString()
		    + "\nCurr end: " + new Date(add).toString() + " " + (add)
		    + " " + " \n " + select + from + where + "\n");
	    QueryResultTable arr = db
		    .executeSQLstatement(select + from + where);
	    StatsAnswerHolder answer = new StatsAnswerHolder(arr, i, add);
	    answers.add(answer);

	}

	return answers;
    }

    /**
     * Queries that database using FormQuery object as the statement to query
     * @param fq The FormQuery object which will be used to query database.
     * @return The results of the database query.
     */
    protected ArrayList<StatsAnswerHolder> queryDatabase(FormQuery fq) {
	Database db = new Database(mActivity);
	ArrayList<StatsAnswerHolder> answers = new ArrayList<StatsAnswerHolder>();
	
	QueryResultTable arr = db.executeSQLstatement(fq.toString());
	answers.add(new StatsAnswerHolder(arr, null, null));


	return answers;
    }

    /**
     * Adds constraints in StatsCompareConstraint in from and where clause. 
     * Returns a FormQuery where the formquery.from and formquery.where can be concatenated to your own query. 
     * Your "where" should have end with " and " before concatenating formquery.where
     * @param tables Set of tables that you already have. This will prevent joining the same tables.
     * @return A FormQuery with and empty formquery.select, and formquery.from and formquery.where that can be concatenated to your own query. 
     */
    protected FormQuery addCompareConstraints(Set<String> tables) {
	String from = " ";
	String where = " ";
	if (compConstraint == null) {
	    return null;
	}
	ArrayList<StatsColumnCompare> lhsArr = compConstraint.leftHandSide;
	ArrayList<StatsColumnCompare> rhsArr = compConstraint.rightHandSide;
	ArrayList<StatsComparatorOperator> operators = compConstraint.comparator;

	int len = lhsArr.size();

	for (int i = 0; i < len;) {
	    StatsColumnCompare lhs = lhsArr.get(i);
	    StatsColumnCompare rhs = rhsArr.get(i);
	    StatsComparatorOperator sign = operators.get(i);

	    String lhsWhere;
	    String rhsWhere;
	    lhsWhere = lhs.toString();
	    rhsWhere = rhs.toString();
	    from += " " + QueryHelper.joinTable(lhs.tablename, tables) + " ";
	    from += " " + QueryHelper.joinTable(rhs.tablename, tables) + " ";
	    where += " " + lhsWhere + sign + rhsWhere + " ";
	    if (++i < len) {
		where += " AND ";
	    }
	}
	return new FormQuery("", from, where);
    }
}
