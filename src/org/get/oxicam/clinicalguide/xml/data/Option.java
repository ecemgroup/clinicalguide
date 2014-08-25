package org.get.oxicam.clinicalguide.xml.data;

public class Option {
	
	public final AbstractAnswer answer;
	public final String nextId;
	
	public Option(AbstractAnswer answer, String nextId) {
		this.answer = answer;
		this.nextId = nextId;
	}
}
