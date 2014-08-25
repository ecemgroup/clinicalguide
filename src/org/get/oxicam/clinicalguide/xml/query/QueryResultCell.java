package org.get.oxicam.clinicalguide.xml.query;

public class QueryResultCell {

    private String columnname;
    private String data;
    public QueryResultCell(String columnname, String data){
	this.columnname = columnname;
	this.data = data;
    }
    

    public String getColumnname() {
        return columnname;
    }
    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
