package org.get.oxicam.clinicalguide.xml.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.db.PatientDetails;

public class Assessment implements Serializable {
    private static final long serialVersionUID = 1L;

    public PatientDetails patient;
    public String mainSymptom = "";
    public int currentIndex = 0;
    public ArrayList<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
    public Treatment recommendedTreatment = null;
    public Treatment chosenTreatment = null;
    public HashMap<String, Answer> answers = new HashMap<String, Answer>();
    public long starttime = 0;
    public String primarySymptom = "";

    public Questionnaire getCurrentQuestionnaire() {
	return questionnaires.get(currentIndex);
    }

    public boolean hasNextQuestionnaire() {
	return currentIndex < questionnaires.size() - 1;
    }

    public boolean isFirstQuestionnaire() {
    	return currentIndex == 0;
    }
    
    public Assessment nextQuestionnaire() {
	Assessment q = new Assessment();
	q.patient = patient;
	q.mainSymptom = mainSymptom;
	q.questionnaires = questionnaires;
	q.currentIndex = currentIndex + 1;
	q.recommendedTreatment = recommendedTreatment;
	q.chosenTreatment = chosenTreatment;
	q.answers = answers;
	q.starttime = starttime;
	q.primarySymptom = primarySymptom;
	return q;
    }

    public Symptom getMainSymptom(ClinicalGuideActivity activity) {
	ArrayList<Symptom> ms = activity.getXmlParser().getMainSymptoms();
	for (Symptom s : ms) {
	    if (s.symptomId.equals(mainSymptom)) {
		return s;
	    }
	}
	return null;
    }
}
