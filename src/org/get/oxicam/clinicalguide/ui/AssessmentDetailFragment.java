package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.xml.data.Answer;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

public class AssessmentDetailFragment extends ListFragment {

    public static final String ARG_KEY_PATIENT_HISTORY_ID = "patientHistoryId";
    public static final String ARG_KEY_ASSESSMENT_NO = "assessmentNo";

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Patient History");

	populateList();
    }

    private void populateList() {
	Bundle args = getArguments();
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	ArrayList<Answer> answers = new ArrayList<Answer>();
	HashMap<String, Object> item;

	if (args != null) {
	    long patientId = args.getLong(ARG_KEY_PATIENT_HISTORY_ID);
	    int assessmentNo = args.getInt(ARG_KEY_ASSESSMENT_NO);
	    Database db = new Database(mActivity);
	    answers = db.getSpecificANswerForPatient(patientId, assessmentNo);
	}

	for (Answer ans : answers) {
	    item = new HashMap<String, Object>();
	    item.put("question", ans.question.label);
	    item.put("answer", ans.value);
	    String value = null;
	    if (ans.question.answerType.equals("bool")
		    | ans.question.answerType.equals("boolswitch")) {
		if (ans.value.equals("true")) {
		    value = "Yes";
		} else {
		    value = "No";
		}
	    } else {
		value = ans.value;
	    }
	    item.put("answer", value);
	    listItem.add(item);
	}

	String[] sourceTags = { "question", "answer" };
	int[] targetIds = { R.id.question, R.id.answer };

	mAdapter = new SimpleAdapter(getActivity(), listItem,
		R.layout.question_answer_list_item, sourceTags, targetIds);
	setListAdapter(mAdapter);
    }
}
