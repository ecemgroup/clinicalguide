package org.get.oxicam.clinicalguide.xml.forms;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.xml.DateHelper;
import org.get.oxicam.clinicalguide.xml.stats.Stats;
import org.get.oxicam.clinicalguide.xml.stats.StatsConstraint;

public class Form {
    

    
    public final String name;
    public final String id;
    public final ArrayList<StatsConstraint> constraints;
    public final ArrayList<FormCell> cells;
    public final FormDuration duration;
    public final FormColumn column;
    public final ArrayList<Stats>stats;
    private long startDate;
    private long endDate;
    
    public Form(String name, String id, FormDuration duration, ArrayList<StatsConstraint> constraints, ArrayList<FormCell> cells, FormColumn column, ArrayList<Stats> stats){
	this.name = name;
	this.id = id;
	this.cells = cells;
	this.duration = duration;
	this.column = column;
	this.constraints = constraints;
	this.stats = stats;
	setStartAndAndDate(System.currentTimeMillis());
	
    }
    
    public final void setStartAndAndDate(long endDate){
	this.endDate = endDate;
	startDate = new DateHelper(duration).getStartDay(endDate);
    }
    
    public long getStartDate(){
	return startDate;
    }
    
    public long getEndDate(){
	return endDate;
    }


}