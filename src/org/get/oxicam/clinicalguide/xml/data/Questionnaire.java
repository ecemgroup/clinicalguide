package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Questionnaire implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final ArrayList<Question> questions;
	public final String questionnaireId;
	public final String type;
	public final String title;
	
	public Questionnaire(String questionnaireId, String title, String type, ArrayList<Question> questions) {
		this.questions = questions;
		this.title = title;
		this.questionnaireId = questionnaireId;
		this.type = type;
	}
}
