package org.get.oxicam.clinicalguide.xml.data;

import java.util.ArrayList;

public class FollowUp {
	public final ArrayList<Questionnaire> questionnaires;
	public final ArrayList<Option> options;
	
	public FollowUp(ArrayList<Questionnaire> questionnaires, ArrayList<Option> options) {
		this.questionnaires = questionnaires;
		this.options = options;
	}
}
