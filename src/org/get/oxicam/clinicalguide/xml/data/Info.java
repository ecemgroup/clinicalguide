package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;

public class Info implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String type;
	public final String label;
	
	public Info(String type, String label) {
		this.type = type;
		this.label = label;
	}
}
