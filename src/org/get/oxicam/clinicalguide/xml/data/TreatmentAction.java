package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;

public class TreatmentAction implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String treatmentActionId;
	public final String label;
	public final Info info;
	
	public TreatmentAction(String treatmentActionId, String label, Info info) {
		this.treatmentActionId = treatmentActionId;
		this.label = label;
		this.info = info;
	}
}
