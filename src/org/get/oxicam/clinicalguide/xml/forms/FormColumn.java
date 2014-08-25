package org.get.oxicam.clinicalguide.xml.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.get.oxicam.clinicalguide.xml.stats.Stats;

public class FormColumn {

    public final ArrayList<LinkedHashMap<String, String>> data;
    public final ArrayList<HashMap<String, String>> savedItems;
    public final ArrayList<Stats> stats;
    public FormColumn(ArrayList<LinkedHashMap<String, String>> data, ArrayList<HashMap<String, String>>  savedItems, ArrayList<Stats> stats){
	this.data = data;
	this.savedItems = savedItems;
	this.stats = stats;
    }
}
