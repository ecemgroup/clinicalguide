package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.db.PatientDetails;
import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PatientsFragment extends ListFragment {

	public static final String ARG_KEY_NEXT_FRAGMENT = "nextFragment";
	public static final String ARG_KEY_SELECTED_PATIENT = "patient";
	
	private ClinicalGuideActivity mActivity;
	private SimpleAdapter mAdapter;

	private List<PatientDetails> mPatients;
	
	/**
	 * Initializes the PatientsFragment.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mActivity = (ClinicalGuideActivity) getActivity();
		mActivity.setTitle("Select Patient");

		// get patient data from DB and put it in th list view
//		mPatientData = new PatientDetailsDataSource(mActivity);
//		mPatientData.open();
		
		mPatients = (new Database(mActivity)).getAllPatientDetailRecords();

		setHasOptionsMenu(true);
		populateList();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.patients_fragment, menu);
	//	getMenuInflater().inflate(R.menu.patients_fragment, menu);
	//	EditText et = (EditText) menu.findItem(R.id.menu_subjectinfo);
	//	et.setText("Patient Name, Age",android.widget.TextView.BufferType.NORMAL);
		//hide home button here
		MenuItem item = menu.findItem(R.id.menu_home);
		item.setVisible(false);
		//try to overide xml
	//	item = menu.findItem(R.id.menu_add_patient);
	//    item.setIcon(R.drawable.ic_patient);
	//    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	
		case R.id.menu_add_patient:
			Bundle b = new Bundle();
			b.putInt("viewMode", PatientDetailsFragment.MODE_ADD);
			
			Fragment frag = new PatientDetailsFragment();
			frag.setArguments(b);
			mActivity.setContent(frag);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called if a item in the ListView is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		PatientDetails selectedPatient = mPatients.get(position);
		
		// open next fragment
		Bundle args = getArguments();
		if(args != null) {
			String fragName = args.getString(ARG_KEY_NEXT_FRAGMENT);
			if(fragName != null) {
				Bundle b = new Bundle();
				b.putSerializable(ARG_KEY_SELECTED_PATIENT, selectedPatient);
				Fragment next = Fragment.instantiate(mActivity, fragName);
				next.setArguments(b);
				mActivity.setContent(next);
			}
		}
	}

	private void populateList() {
		// populate a HashMap with all list items
		ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
		for (PatientDetails p : mPatients) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			for (PatientAttribute attribute : p.getAttributes()) {
				if (attribute.name.equals("firstname")) {
					map.put("name", attribute.answer);
				}
				else if (attribute.name.equals("lastname")) {
					map.put("surname", attribute.answer);
				}
				else if (attribute.name.equals("gender")) {
					map.put("gender", attribute.answer);
				}
				Database db = new Database(mActivity);
				float age = db.getPatientAge(p.getPatientID());
				map.put("age", ", " + (int)FloatMath.floor(age));
				map.put("accessory", "\uE75E");
			}
			itemData.add(map);
		}

		// Specify source tag to corresponding target ID mapping
		String[] sourceTags = { "name", "surname", "gender", "age","accessory" };
		int[] targetIds = { R.id.patientName, R.id.patientSurname, R.id.patientGender, R.id.patientAge , R.id.patientAccessoryView};

		// create the ListAdapter
		mAdapter = new SimpleAdapter(getActivity(), itemData,
				R.layout.patients_list_item, sourceTags, targetIds);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onResume() {
//		mPatientData.open();
		super.onResume();
	}

	@Override
	public void onPause() {
//		mPatientData.close();
		super.onPause();
	}
}
