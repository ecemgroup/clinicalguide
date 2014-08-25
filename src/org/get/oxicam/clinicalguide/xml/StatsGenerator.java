package org.get.oxicam.clinicalguide.xml;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.xml.stats.Stats;
import org.get.oxicam.clinicalguide.xml.stats.AbstractStatsQuestion;

import android.content.Context;

public class StatsGenerator {

    private Context mActivity;
    private Stats stats;

    public StatsGenerator(Context context, Stats stats) {
	mActivity = context;
	this.stats = stats;
    }

    public String getStatsString() {
	String retVal = stats.name + "\n";
	ArrayList<AbstractStatsQuestion> statsQArr = stats.questions;
	int qLen = statsQArr.size();
	for (int j = 0; j < qLen; j++) {
	    AbstractStatsQuestion statsQ = statsQArr.get(j);
	    String str = statsQ.getResultAsString();

	    retVal += str + "\n";
	}

	return retVal;
    }

}
