package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PatientHistoryFragment extends ListFragment {

    public static final String ARG_KEY_PATIENT_HISTORY_ID = "patientHistoryId";
    public static final String ARG_KEY_ASSESSMENT_NO = "assessmentNo";

    private ArrayList<Integer> assessments;
    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private long patientId;

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	assessments = new ArrayList<Integer>();
	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Patient History");

	populateList();
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
	int assessmentNo = assessments.get(position);
	Log.w("History", "assessment no: " + assessmentNo);
	Bundle b = new Bundle();

	b.putInt(ARG_KEY_ASSESSMENT_NO, assessmentNo);
	b.putLong(ARG_KEY_PATIENT_HISTORY_ID, patientId);

	Fragment frag = new AssessmentDetailFragment();
	frag.setArguments(b);
	mActivity.setContent(frag);
    }

    private void populateList() {
	Bundle args = getArguments();
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> illness = new ArrayList<String>();
	HashMap<String, Object> item;

	if (args != null) {
	    patientId = args.getLong(ARG_KEY_PATIENT_HISTORY_ID);
	    Database db = new Database(mActivity);
	    assessments = db.getAssessmentListForPatient(patientId);
	    illness = db.getIllnessListForPatient(patientId);
	}

	for (int i = 0; i < assessments.size(); i++) {
	    item = new HashMap<String, Object>();
	    item.put("assessment", i + 1);
	    item.put("illness", illness.get(i));
	    listItem.add(item);
	}

	String[] sourceTags = { "assessment", "illness" };
	int[] targetIds = { R.id.assessNo, R.id.illness };

	mAdapter = new SimpleAdapter(getActivity(), listItem,
		R.layout.patient_history_list_item, sourceTags, targetIds);
	setListAdapter(mAdapter);
    }

    @Override
    public void onResume() {
	super.onResume();
    }

    @Override
    public void onPause() {
	super.onPause();
    }
}
