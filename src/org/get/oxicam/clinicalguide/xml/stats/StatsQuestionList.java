package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.ParserHelper;
import org.get.oxicam.clinicalguide.xml.data.FormQuery;
import org.get.oxicam.clinicalguide.xml.query.QueryHelper;

import android.util.Log;

public class StatsQuestionList extends AbstractStatsQuestion {

    public final ArrayList<StatsSubject> subjects;
    public final boolean distinct;
    public StatsQuestionList(ClinicalGuideActivity mActivity,
	    ArrayList<StatsSubject> subjects,
	    boolean distinct,
	    ArrayList<StatsConstraint> constraints,
	    StatsTimespan timespan,
	    StatsCompareConstraint compConstraint) {
	super(mActivity, "list", timespan, constraints, compConstraint);
	this.subjects = subjects;
	this.distinct = distinct;
    }

    public String getResultAsString(
	    ArrayList<StatsConstraint> additionalConstraint) {
	ArrayList<StatsAnswerHolder> answers = getResultAsValue(additionalConstraint);
	String retVal = "";

	int len = answers.size();
	for (int i = 0; i < len; i++) {
	    StatsAnswerHolder ans = answers.get(i);
	    retVal += ans.toString();
	}

	return retVal;
    }

    @Override
    public ArrayList<StatsAnswerHolder> getResultAsValue(
	    ArrayList<StatsConstraint> additionalConstraint) {
	Set<String> tables = new HashSet<String>();
	String select = " SELECT " + (distinct ? "DISTINCT " : "");
	String from = " FROM ";
	String where = " WHERE 1=1 ";
	ArrayList<StatsConstraint> freshConstraints = new ArrayList<StatsConstraint>();
	boolean moreThanOneTable = false;
	int subjLen = subjects.size();

	if (subjLen == 0) {
	    return queryDatabase(new FormQuery("select null", "", ""));
	}

	if (constraints != null) {
	    freshConstraints.addAll(constraints);
	}
	if (additionalConstraint != null) {
	    freshConstraints.addAll(additionalConstraint);
	}
	ArrayList<StatsSubject> checkTables = new ArrayList<StatsSubject>();
	ArrayList<StatsSubject> compTables = new ArrayList<StatsSubject>();
	checkTables.addAll(freshConstraints);
	if (subjects != null) {
	    checkTables.addAll(subjects);
	}
	if (compConstraint != null) {
	    compTables = compConstraint.getTablesAndColumns();
	    checkTables.addAll(compTables);
	}
	if (ParserHelper.moreThanOneTable(checkTables) || timespan != null) {
	    from += " history history_table ";
	    moreThanOneTable = true;
	} else if (checkTables.size() > 0) {
	    StatsSubject constraint = checkTables.get(0);
	    from += " " + constraint.tablename.substring(0, constraint.tablename.length() - 6) + " " + constraint.tablename + " ";
	    tables.add(constraint.tablename.substring(0,  constraint.tablename.length() - 6));
	} else if (compTables.size() > 0) {
	    String tablename = compTables.get(0).tablename;
	    from += " " + tablename.substring(0, tablename.length() - 6) + " " + tablename + " ";
	    tables.add(tablename.substring(0, tablename.length() - 6));
	}

	int constLen = freshConstraints.size();
	// populate select and from
	for (int i = 0; i < subjLen;) {
	    StatsSubject subj = subjects.get(i);
	    select += QueryHelper.addToSelect(subj);
	    if (++i < subjLen) {
		select += " , ";
	    }
	    if (moreThanOneTable) {
		from += " " + QueryHelper.joinTable(subj.tablename, tables);
	    }
	}
	for (int i = 0; i < constLen; i++) {
	    StatsConstraint fc = freshConstraints.get(i);
	    from += " " + QueryHelper.joinTable(fc.tablename, tables);
	    where += " AND " + QueryHelper.addToWhere(mActivity, fc);
	}

	FormQuery fq = addCompareConstraints(tables);
	if (fq != null) {
	    from += " " + fq.getFrom() + " ";
	    where += " AND " + fq.getWhere() + " ";
	}
	fq = new FormQuery(select, from, where);
	Log.v("question list query", fq.toString());

	return getTimeSpanDetails(fq);
    }

}
