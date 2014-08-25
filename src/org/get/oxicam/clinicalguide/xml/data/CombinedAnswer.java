package org.get.oxicam.clinicalguide.xml.data;

import java.util.ArrayList;
import java.util.HashMap;

public class CombinedAnswer extends AbstractAnswer {
	
	public final String answerId;
	public final ArrayList<HashMap<String, Object>> answers;
	
	public CombinedAnswer(String answerId, ArrayList<HashMap<String, Object>> answers) {
		this.answerId = answerId;
		this.answers = answers;
	}
}
