package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;

public class Question implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String questionId;
	public final String label;
	public final String answerType;
	public final String min;
	public final Info info;
	public final String link;
	public final Annotation annotation;
	
	public Question(String questionId, String label, String answerType, String min, Info info, Annotation annotation, String link) {
		this.questionId = questionId;
		this.label = label;
		this.answerType = answerType;
		this.info = info;
		this.min = min;
		this.annotation = annotation;
		this.link = link;
	}
}
