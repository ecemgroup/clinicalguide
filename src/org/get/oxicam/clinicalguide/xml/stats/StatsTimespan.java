package org.get.oxicam.clinicalguide.xml.stats;

import org.get.oxicam.clinicalguide.xml.forms.FormDuration;

public class StatsTimespan {

    public static final int GROUP_DAILY = 0;
    public static final int GROUP_WEEKLY = 1;
    public static final int GROUP_MONTHLY = 2;
    public final int type;
    public final int group;
    public final int adjust;
    
    public StatsTimespan(String type, String group, int adjust){
	this.adjust = adjust;
	this.type = FormDuration.changeStringTypeToInt(type);
	this.group = changeGroupFromStringToInt(group);
    }
    
    public String changeGroupFromIntToString(int i){
	switch(i){
	case GROUP_DAILY:
	    return "daily";
	case GROUP_MONTHLY:
	    return "monthly";
	case GROUP_WEEKLY:
	    return "weekly";   
	default:
	    return "ERROR";
	}
    }
    
    public int changeGroupFromStringToInt(String group){
	group = group.trim();
	if(group.equalsIgnoreCase("weekly")){
	    return GROUP_WEEKLY;
	} else if(group.equalsIgnoreCase("daily")){
	    return GROUP_DAILY;
	} else if(group.equalsIgnoreCase("monthly")){
	    return GROUP_MONTHLY;
	} else {
	    return -1;
	}
    }
}
