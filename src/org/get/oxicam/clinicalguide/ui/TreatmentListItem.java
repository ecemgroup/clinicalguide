package org.get.oxicam.clinicalguide.ui;

import org.get.oxicam.clinicalguide.xml.data.TreatmentAction;

public class TreatmentListItem {

	public int icon;
	TreatmentAction treatmentAction;
	
	public TreatmentListItem(TreatmentAction treatmentAction) {
		this.treatmentAction = treatmentAction;
	}
}
