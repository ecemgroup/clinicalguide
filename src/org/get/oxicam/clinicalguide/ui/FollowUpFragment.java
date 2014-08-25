package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.db.PatientDetails;
import org.get.oxicam.clinicalguide.xml.data.Answer;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.Symptom;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FollowUpFragment extends ListFragment {

    private ClinicalGuideActivity mActivity;
    private PatientDetails mPatient;

    private SimpleAdapter mAdapter;

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // return inflater.inflate(R.layout.follow_up_footer, container, false);
    // }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	//reset adapter so that header can be added
	setListAdapter(null);
	
	ArrayList<Answer> lastAnswers = new ArrayList<Answer>();
	String LastDate = null;

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Start Follow-Up");

	if (getArguments() != null) {
	    Object o = getArguments().get(
		    PatientsFragment.ARG_KEY_SELECTED_PATIENT);
	    if (o != null) {
		mPatient = (PatientDetails) o;
		Database db = new Database(mActivity);
		lastAnswers = db.getLastAnswersForPatient(mPatient);
		LastDate = db.getLastAnswerDateForPatient(mPatient);
	    }
	}
	FrameLayout container = new FrameLayout(mActivity);
	View.inflate(getActivity(), R.layout.follow_up_footer, container);
	getListView().addHeaderView(container);

	// adds title
	ViewGroup viewGroupHeader = new RelativeLayout(mActivity);
	TextView textView = new TextView(mActivity);
	textView.setText(getString(R.string.followup_history_title)
		.toUpperCase());
	textView.setTextColor(Color.argb(255, 47, 178, 229));
	textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
	textView.setTextSize(17f);
	textView.setShadowLayer(0f, 0f, 1f, Color.DKGRAY);
	viewGroupHeader.addView(textView);
	RelativeLayout.LayoutParams paramsHeader = (RelativeLayout.LayoutParams) textView
		.getLayoutParams();
	paramsHeader.addRule(RelativeLayout.CENTER_HORIZONTAL);
	paramsHeader.setMargins(10, 0, 0, 0);
	textView.setLayoutParams(paramsHeader);
	getListView().addHeaderView(viewGroupHeader);

	mActivity.findViewById(R.id.btNext).setOnClickListener(
		new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			RadioButton yesButton = (RadioButton) mActivity
				.findViewById(R.id.rbYesOption);
			if (yesButton.isChecked()) {
			    // do new assessment
			    Bundle args = new Bundle();
			    args.putSerializable(
				    PatientsFragment.ARG_KEY_SELECTED_PATIENT,
				    mPatient);
			    Fragment frag = Fragment.instantiate(mActivity,
				    MainSymptomFragment.class.getName(), args);
			    mActivity.setContent(frag);

			} else {
			    // do follow-up questions
			    Assessment q = new Assessment();
			    q.patient = mPatient;

			    // FIXME: get main symptom from history - for now
			    // use hardcoded one
			    Symptom mainSymptom = mActivity.getXmlParser()
				    .getMainSymptoms().get(0);
			    q.mainSymptom = mainSymptom.symptomId;
			    q.primarySymptom = mainSymptom.name;
			    // set questionnaires to ask
			    q.questionnaires.addAll(mActivity.getXmlParser()
				    .getGeneralQuestionnaires());
			    q.questionnaires.addAll(mainSymptom.questionnaires);
			    q.questionnaires
				    .addAll(mainSymptom.followUp.questionnaires);
			    q.starttime = System.currentTimeMillis();
			    // show QuestionnaireFragment
			    Bundle args = new Bundle();
			    args.putSerializable(
				    QuestionnaireFragment.ARG_KEY_QUESTIONNAIRES,
				    q);
			    Fragment frag = Fragment
				    .instantiate(mActivity,
					    QuestionnaireFragment.class
						    .getName(), args);
			    mActivity.setContent(frag);
			    Toast.makeText(mActivity, q.mainSymptom,
				    Toast.LENGTH_SHORT).show();
			}
		    }
		});

	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (Answer answer : lastAnswers) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("question", answer.question.label);
	    map.put("answer", answer.value);
	    String value = null;
	    if (answer.question.answerType.equals("bool")
		    | answer.question.answerType.equals("boolswitch")) {
		if (answer.value.equals("true")) {
		    value = "Yes";
		} else {
		    value = "No";
		}
	    } else {
		value = answer.value;
	    }
	    map.put("answer", value);
	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "question", "answer" };
	int[] targetIds = { R.id.question, R.id.answer, };

	mAdapter = new SimpleAdapter(mActivity, itemData,
		R.layout.question_answer_list_item, sourceTags, targetIds);
	setListAdapter(mAdapter);
    }
}
