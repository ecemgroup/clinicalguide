package org.get.oxicam.clinicalguide.xml.stats;

import java.util.ArrayList;

public class Stats {
    
    public final ArrayList<AbstractStatsQuestion> questions;
    public final String name;
    public Stats(ArrayList<AbstractStatsQuestion> questions,  String name){
	this.questions = questions;
	this.name = name;
    }

}
