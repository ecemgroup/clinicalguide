package org.get.oxicam.clinicalguide.xml.query;

import java.util.ArrayList;

public class QueryResultTable {
    public final ArrayList<QueryResultRow> rows;
    public QueryResultTable(ArrayList<QueryResultRow> rows){
	this.rows = rows;
    }
    
    public QueryResultTable(QueryResultCell cell){
	ArrayList<QueryResultRow> rows = new ArrayList<QueryResultRow>();
	ArrayList<QueryResultCell> cells = new ArrayList<QueryResultCell>();
	cells.add(cell);
	rows.add(new QueryResultRow(cells));
	this.rows = rows;
	
    }
}
