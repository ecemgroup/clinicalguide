package org.get.oxicam.clinicalguide.xml.query;

import java.util.ArrayList;

public class QueryResultRow {
    public final ArrayList<QueryResultCell> cells;
    public QueryResultRow(ArrayList<QueryResultCell> cells){
	this.cells = cells;
    }
}
