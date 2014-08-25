package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;

public class PatientAttribute implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final String value;
	public final String type;
	public String answer;
	
	public PatientAttribute(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.answer = null;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
