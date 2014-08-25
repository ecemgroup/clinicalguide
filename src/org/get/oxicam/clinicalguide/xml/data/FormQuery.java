package org.get.oxicam.clinicalguide.xml.data;

public class FormQuery {

    private String select;
    private String from;
    private String where;
    
    public FormQuery(String select, String from, String where){
	this.select = select;
	this.from = from;
	this.where = where;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
    
    
    public String toString(){
	return select + from + where;
    }
    
}
