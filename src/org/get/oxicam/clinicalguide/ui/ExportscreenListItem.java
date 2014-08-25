package org.get.oxicam.clinicalguide.ui;

/*
 * Data structure for holding information about one list item.
 */
public class ExportscreenListItem {
	public int icon;
	public int title;
	public int description;


	public ExportscreenListItem(int ic, int tit, int descr) {
		this.icon = ic;
		this.title = tit;
		this.description = descr;
	}
}