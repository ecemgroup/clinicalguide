package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.TreatmentAction;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TreatmentFragment extends ListFragment {

    public static final String ARG_KEY_QUESTIONNAIRES = "questionnaires";

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private ArrayList<TreatmentAction> mTreatmentActions;
    private Assessment mAssessment;

    /**
     * Initializes the ClassificationFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();

	mAssessment = (Assessment) getArguments().get(ARG_KEY_QUESTIONNAIRES);
	mActivity.setTitle(mAssessment.chosenTreatment.classification);
	mTreatmentActions = mAssessment.chosenTreatment.treatmentActions;

	ArrayList<TreatmentListItem> items = new ArrayList<TreatmentListItem>();
	for (TreatmentAction action : mTreatmentActions) {
	    items.add(new TreatmentListItem(action));
	}

	ViewGroup viewGroupHeader = new RelativeLayout(mActivity);

	TextView textView1 = new TextView(mActivity);
	textView1.setText(mAssessment.chosenTreatment.classification);
	viewGroupHeader.addView(textView1);
	RelativeLayout.LayoutParams paramsHeader = (RelativeLayout.LayoutParams) textView1
		.getLayoutParams();
	paramsHeader.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	paramsHeader.setMargins(10, 0, 0, 0);
	textView1.setLayoutParams(paramsHeader);

	ViewGroup viewGroupFooter = new RelativeLayout(mActivity);
	Button button = new Button(mActivity);
	button.setText("Finish Assessment");
	button.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Toast.makeText(mActivity,
			"End of asssessment for this patient",
			Toast.LENGTH_LONG).show();

		Database db = new Database(mActivity);
		db.addAssessment(mAssessment, mActivity.getUser());

		mActivity.gotoHomescreen();
	    }
	});
	viewGroupFooter.addView(button);
	RelativeLayout.LayoutParams paramsFooter = (RelativeLayout.LayoutParams) button
		.getLayoutParams();
	paramsFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	paramsFooter.setMargins(0, 0, 10, 0);
	button.setLayoutParams(paramsFooter);
	getListView().addFooterView(viewGroupFooter);

	populateList(items);
    }

    /**
     * Called if a item in the ListView is clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	// mActivity.setChosenTreatment(mTreatments.get(position));
	// mActivity.setTitle(mTreatments.get(position).classification);
	// mActivity.setContent(new QuestionnaireFragment(questionnaires, 0));
    }

    private void populateList(ArrayList<TreatmentListItem> items) {
	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (TreatmentListItem item : items) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("treatmentAction", item.treatmentAction);
	    if (item.treatmentAction.info != null) {
		map.put("infoType", item.treatmentAction.info.type);
		map.put("infoLabel", item.treatmentAction.info.label);
	    }

	    map.put("treatmentActionCheckBox", item.treatmentAction);
	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "treatmentAction", "infoType", "infoLabel",
		"treatmentActionCheckBox" };
	int[] targetIds = { R.id.treatmentAction, R.id.infoBackground,
		R.id.info, R.id.treatmentCheckBox };

	// create the ListAdapter
	mAdapter = new SimpleAdapter(getActivity(), itemData,
		R.layout.treatment_list_item, sourceTags, targetIds);
	mAdapter.setViewBinder(new TreatmentBinder());
	setListAdapter(mAdapter);
    }

    class TreatmentBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data,
		String textRepresentation) {
	    if (view.getId() == R.id.treatmentAction) {
		TreatmentAction treatmentAction = (TreatmentAction) data;
		TextView textView = (TextView) view;
		// System.out.println(treatmentAction.label);
		textView.setText(treatmentAction.label);
	    } else if (view.getId() == R.id.infoBackground) {
		if (data != null) {
		    if (data.equals("info")) {
			view.setBackgroundResource(R.drawable.rounded_edges_info);
		    } else if (data.equals("urgent")) {
			view.setBackgroundResource(R.drawable.rounded_edges_urgent);
		    }
		} else {
		    view.setVisibility(View.GONE);
		}
	    } else if (view.getId() == R.id.info) {
		if (data != null) {
		    ((TextView) view).setText((String) data);
		}
	    } else if (view.getId() == R.id.infoButton) {
		// if (data == null) {
		view.setVisibility(View.INVISIBLE);
		// }
	    } else if (view.getId() == R.id.treatmentCheckBox) {
		CheckBox cb = (CheckBox) view;

		if (data == null) {
		    Toast.makeText(mActivity, "null ", Toast.LENGTH_SHORT)
			    .show();
		    return false;
		}
		final TreatmentAction ta = (TreatmentAction) data;
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView,
			    boolean isChecked) {
			if (isChecked) {
			    mAssessment.chosenTreatment.chosenTreatmentActions
				    .add(ta);
			} else {
			    mAssessment.chosenTreatment.chosenTreatmentActions
				    .remove(ta);
			}
		    }
		});
	    } else {
		return false;
	    }
	    return true;
	}
    }
}
