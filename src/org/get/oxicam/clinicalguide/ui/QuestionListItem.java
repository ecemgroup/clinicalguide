package org.get.oxicam.clinicalguide.ui;

import org.get.oxicam.clinicalguide.xml.data.Question;

/*
 * Data structure for holding information about one list item.
 */
public class QuestionListItem {
	public int icon;
	public Question question;


	public QuestionListItem(int ic, Question question) {
		this.icon = ic;
		this.question = question;
	}
}
