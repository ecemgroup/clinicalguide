package org.get.oxicam.clinicalguide.xml.forms;

public class FormDuration {
    public static final int DURATION_YEAR = 0;
    public static final int DURATION_MONTH = 1;
    public static final int DURATION_WEEK = 2;
    public static final int DURATION_DAY = 3;
    public static final int DURATION_DEFINED = 4;
    public final int type;
    public final String detail;
    public FormDuration(int type, String detail){
	this.type = type;
	this.detail = detail;
    }
    
    public static int changeStringTypeToInt(String str){
	str = str.trim();
	if(str.equalsIgnoreCase("week")){
	    return DURATION_WEEK;
	} else if(str.equalsIgnoreCase("month")){
	    return DURATION_MONTH;
	} else if(str.equalsIgnoreCase("year")){
	    return DURATION_YEAR;
	} else if(str.equalsIgnoreCase("day")){
	    return DURATION_DAY;
	} else {
	    return -1;
	}
    }
    
    public static String changeIntTypeToString(int type){
	switch(type){
	case DURATION_DAY:
	    return "day";
	case DURATION_MONTH:
	    return "month";
	case DURATION_WEEK:
	    return "week";
	case DURATION_YEAR:
	    return "year";
	case DURATION_DEFINED:
	    return "defined";    
	default:
		return "error";
	}
    }
}
