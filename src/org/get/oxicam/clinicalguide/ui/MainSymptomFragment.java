package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.PatientDetails;
import org.get.oxicam.clinicalguide.xml.CGParser;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.Symptom;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class MainSymptomFragment extends ListFragment {

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private ArrayList<Symptom> mMainSymptoms;
    private PatientDetails mPatient;

    /**
     * Initializes the MainSymptomFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Primary Symptom or Task");
	CGParser parser = mActivity.getXmlParser();
	mMainSymptoms = parser.getMainSymptoms();

	if (getArguments() != null) {
	    Object o = getArguments().get(
		    PatientsFragment.ARG_KEY_SELECTED_PATIENT);
	    if (o != null) {
		mPatient = (PatientDetails) o;
	    }
	}

	ArrayList<MainSymptomListItem> items = new ArrayList<MainSymptomListItem>();
	for (Symptom symptom : mMainSymptoms) {
	    items.add(new MainSymptomListItem(symptom));
	}
	populateList(items);
    }

    /**
     * Called if a item in the ListView is clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	// FIXME: only handle cough or difficult breathing (index 0) for now
	// the others are not yet implemented...
	Symptom mainSymptom = mMainSymptoms.get(position);
	int symptomId = Integer.parseInt(mainSymptom.symptomId.substring(4,
		mainSymptom.symptomId.length()));
	if (symptomId < 1000) {
	    
	    Assessment q = new Assessment();
	    q.starttime = System.currentTimeMillis();
	    q.patient = mPatient;
	    q.mainSymptom = mainSymptom.symptomId;
	    q.primarySymptom = mainSymptom.name;
	    if (symptomId < 100) { // IMCI symptom
		q.questionnaires.addAll(mActivity.getXmlParser()
			.getGeneralQuestionnaires());
	    }
	    q.questionnaires.addAll(mainSymptom.questionnaires);

	    Bundle args = new Bundle();
	    args.putSerializable(QuestionnaireFragment.ARG_KEY_QUESTIONNAIRES,
		    q);
	    Fragment frag = Fragment.instantiate(mActivity,
		    QuestionnaireFragment.class.getName(), args);
	    mActivity.setContent(frag);
	}
    }

    private void populateList(ArrayList<MainSymptomListItem> items) {
	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (MainSymptomListItem item : items) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("icon", item.symptom.icon);
	    map.put("symptom", item.symptom);
	    map.put("accessory", "\uE75E");
	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "icon", "symptom", "accessory" };
	int[] targetIds = { R.id.itemIcon, R.id.itemTitle, R.id.accessoryView };

	// create the ListAdapter
	mAdapter = new SimpleAdapter(getActivity(), itemData,
		R.layout.mainsymptom_list_item, sourceTags, targetIds);
	mAdapter.setViewBinder(new MainSymptomBinder());
	setListAdapter(mAdapter);
    }

    class MainSymptomBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data,
		String textRepresentation) {
	    if (view.getId() == R.id.itemTitle) {
		Symptom symptom = (Symptom) data;
		TextView textView = (TextView) view;
		textView.setText(symptom.name);
		if (symptom.questionnaires.size() == 0) {
		    textView.setTextColor(Color.GRAY);
		}
	    } else if (view.getId() == R.id.itemIcon) {
		ImageView icon = (ImageView) view;
		if (data != null) {
		    Drawable img = (Drawable) data;
		    icon.setImageDrawable(img);
		    icon.getLayoutParams().height = 100;
		} else {
		    icon.setVisibility(View.GONE);
		}
	    } else if (view.getId() == R.id.accessoryView) {
		TextView textView = (TextView) view;
		Typeface font = Typeface.createFromAsset(mActivity.getAssets(),
			"fontello.ttf");
		textView.setTypeface(font);
	    } else {
		return false;
	    }
	    return true;
	}
    }

}
