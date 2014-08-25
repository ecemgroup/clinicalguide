package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Treatment implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String treatmentId;
    public final String classification;
    public final ArrayList<TreatmentAction> treatmentActions;
    public final ArrayList<TreatmentAction> chosenTreatmentActions;

    public Treatment(String treatmentId, String classification,
	    ArrayList<TreatmentAction> treatmentActions) {
	this.treatmentId = treatmentId;
	this.classification = classification;
	this.treatmentActions = treatmentActions;
	this.chosenTreatmentActions = new ArrayList<TreatmentAction>();
    }
}
