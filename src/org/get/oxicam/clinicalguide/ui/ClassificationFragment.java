package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.Treatment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class ClassificationFragment extends ListFragment {

    public static final String ARG_KEY_QUESTIONNAIRES = "questionnaires";

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private ArrayList<Treatment> mTreatments;

    private Assessment mAssessment;

    /**
     * Initializes the ClassificationFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Classification");
	mAssessment = (Assessment) getArguments().get(ARG_KEY_QUESTIONNAIRES);

	mTreatments = mAssessment.getMainSymptom(mActivity).treatments;
	// bugs in here for (Treatment treatment : mTreatments) {
	// if
	// (treatment.treatmentId.equals(mAssessment.recommendedTreatment.treatmentId))
	// {
	// mTreatments.remove(treatment);
	// }
	// }

	ArrayList<ClassificationListItem> items = new ArrayList<ClassificationListItem>();
	for (Treatment treatment : mTreatments) {
	    if (treatment.treatmentId
		    .equals(mAssessment.recommendedTreatment.treatmentId)) {
		continue;
	    }
	    items.add(new ClassificationListItem(treatment));
	}

	getListView().setAdapter(null);

	FrameLayout viewGroupHeader = new FrameLayout(mActivity);
	View header = View.inflate(mActivity,
		R.layout.recommended_classification_header, viewGroupHeader);
	getListView().addHeaderView(header);

	TextView tv = ((TextView) header
		.findViewById(R.id.recommendedItemTitle));
	tv.setText(mAssessment.recommendedTreatment.classification);

	TextView accessoryView = ((TextView) header
		.findViewById(R.id.accessoryView));
	Typeface font = Typeface.createFromAsset(mActivity.getAssets(),
		"fontello.ttf");
	accessoryView.setTypeface(font);
	header.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		continueWithTreatment(mAssessment.recommendedTreatment);
	    }
	});

	populateList(items);
    }

    private void continueWithTreatment(Treatment t) {
	// mAssessment.chosenTreatment = t;
	// Bundle args = new Bundle();
	// args.putSerializable(TreatmentFragment.ARG_KEY_QUESTIONNAIRES,
	// mAssessment);
	// Fragment frag = Fragment.instantiate(mActivity,
	// TreatmentFragment.class.getName(), args);
	// mActivity.setContent(frag);

	mAssessment.chosenTreatment = t;
	FragmentTransaction ft = getFragmentManager().beginTransaction();
	Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	if (prev != null) {
	    ft.remove(prev);
	}
	ft.addToBackStack(null);
	TreatmentConfirmationDialog dialog = new TreatmentConfirmationDialog();
	Bundle args = new Bundle();
	args.putSerializable(TreatmentFragment.ARG_KEY_QUESTIONNAIRES,
		mAssessment);
	dialog.setArguments(args);
	dialog.show(ft, "dialog");
    }

    /**
     * Called if a item in the ListView is clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	int counter = 0;
	int recommendedPosition = mTreatments.size();
	// TODO: maybe there is a better way to find out if recommended
	// treatment is lower in the list
	for (Treatment treatment : mTreatments) {

	    if (treatment.treatmentId
		    .equals(mAssessment.recommendedTreatment.treatmentId)) {
		recommendedPosition = counter;
	    }
	    counter += 1;
	}
	position -= 1; // adjust to array indexing
	if (position >= recommendedPosition) { // jump over recommendedTreatment
	    position += 1;
	}
	continueWithTreatment(mTreatments.get(position));

    }

    private void populateList(ArrayList<ClassificationListItem> items) {
	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (ClassificationListItem item : items) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("treatment", item.treatment);
	    map.put("accessory", "\uE75E");
	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "treatment", "accessory" };
	int[] targetIds = { R.id.itemTitle, R.id.accessoryView };

	// create the ListAdapter
	mAdapter = new SimpleAdapter(getActivity(), itemData,
		R.layout.classification_list_item, sourceTags, targetIds);
	mAdapter.setViewBinder(new ClassificationBinder());
	setListAdapter(mAdapter);
    }

    class ClassificationBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data,
		String textRepresentation) {
	    if (view.getId() == R.id.itemTitle) {
		Treatment treatment = (Treatment) data;
		TextView textView = (TextView) view;
		textView.setText(treatment.classification);
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
