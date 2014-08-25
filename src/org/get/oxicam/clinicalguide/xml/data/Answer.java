package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;

public class Answer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final Question question;
	public final String value;
	
	public Answer(Question question, String value) {
		this.question = question;
		this.value = value;
	}
}
