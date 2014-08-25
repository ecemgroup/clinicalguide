package org.get.oxicam.clinicalguide.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.db.PatientDetails;
import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class PatientDetailsFragment extends Fragment {
	public static final int MODE_EDIT = 1;
	public static final int MODE_ADD = 2;
	public static final String EDIT_PATIENT = "Edit Patient";
	public static final String ADD_PATIENT = "Add Patient";
	public static final String ARG_KEY_PATIENT_HISTORY_ID = "patientHistoryId";

	private PatientDetails mPatient;
	private ClinicalGuideActivity mActivity;
	private int mViewMode = MODE_EDIT;
	// global toast variable to prevent flooding of toast messages
	private Toast toast;

	// private EditText dob=new EditText(mActivity);

	/**
	 * Initializes the PatientDetailsFragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (ClinicalGuideActivity) getActivity();
		View v = inflater.inflate(R.layout.patient_details, container, false);
		RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.layout);

		Object arg = getArguments().get("viewMode");
		if (arg != null) {
			mViewMode = (Integer) arg;
		} else {
			mViewMode = MODE_EDIT;
		}

		if (mViewMode == MODE_ADD) {
			mActivity.setTitle(ADD_PATIENT);
		} else {
			mActivity.setTitle(EDIT_PATIENT);
		}

		arg = getArguments().get(PatientsFragment.ARG_KEY_SELECTED_PATIENT);
		if (arg != null) {
			mPatient = (PatientDetails) arg;
		} else {
			mPatient = new PatientDetails(mActivity.getXmlParser()
					.getPatientDetails());
		}

		
		int lastId = 0;
		
		if (mActivity.getTitle().equals(EDIT_PATIENT)) {
			// Button for "Patient History"
			Button details = new Button(mActivity);
			details.setText(R.string.patient_edit_hist_bt_title);
			details.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getDetails();
				}
			});
			details.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.patient_history, 0, 0, 0);
			lastId += 1; 
			details.setId(lastId);
			
			rl.addView(details);
			
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) details.getLayoutParams();
			params.setMargins(25, 25, 25, 125);
			//params.addRule(RelativeLayout.ALIGN_CENTER);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			
			params.addRule(RelativeLayout.ALIGN_BASELINE, lastId);
			View ruler = new View(mActivity); ruler.setBackgroundColor(0xFF00FF00);
			rl.addView(ruler,
			 new ViewGroup.LayoutParams( 200, 4));
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.BELOW, lastId);
			
						
		}
		
		
		
		ArrayList<PatientAttribute> items = mPatient.getAttributes();
		
		for (int i = 0; i < items.size(); i++) {
			int id = lastId + 1;
			PatientAttribute attrib = items.get(i);
			
			// treat different fields differently
			
			if (attrib.name.toString().equalsIgnoreCase("gender")) { //gender button
				Switch tb = new Switch(mActivity);
				tb.setTextOff("male");
				tb.setTextOn("female");
				tb.setId(id);

				if (attrib.answer != null) {
					if (attrib.answer.toString().equalsIgnoreCase("female")) {
						tb.setChecked(true);
					}
				}

				rl.addView(tb);

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tb
						.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 5, 25, 0);
				if (lastId == 0) {
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					params.addRule(RelativeLayout.BELOW, lastId);
				}

				TextView tv = new TextView(mActivity);
				tv.setText(attrib.value + ":");
				rl.addView(tv);
				params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
				params.setMargins(25, 0, 0, 0);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_BASELINE, id);

				
			} else if (attrib.name.toString().equalsIgnoreCase("birthdate")) {  //birthdate selector
				final EditText dob = new EditText(mActivity);
				dob.setSingleLine();
				dob.setEms(7);
				dob.setEnabled(false);
				dob.setId(id);
				dob.setInputType(InputType.TYPE_CLASS_DATETIME
						| InputType.TYPE_DATETIME_VARIATION_DATE);
				if (attrib.answer != null) {
					dob.setText(attrib.answer);
				} else // if
					// (attrib.name.toString().equalsIgnoreCase("birthdate"))
				{
					// et.setText("dd-mm-yyyy");
				}
				// DatePicker datePicker = (DatePicker) findViewById(
				// R.id.datePicker );
				// et.setClickable(true);
				// Toast.makeText(mActivity, "Heyyyyy",
				// Toast.LENGTH_LONG).show();
				dob.setOnClickListener(new OnClickListener() {

					// @Override
					public void onClick(View arg0) {
						// get set values
						String dateText = ((TextView) dob).getText().toString();
						int year, month, day;

						// Log.w("PatientDetailsFragment", dateText);
						String temp[] = dateText.split("-");
						if (temp.length == 3) {

							year = Integer.valueOf(temp[0]);
							month = Integer.valueOf(temp[1]);
							day = Integer.valueOf(temp[2]);

						} else {
							final Calendar c = Calendar.getInstance();
							year = c.get(Calendar.YEAR);
							month = c.get(Calendar.MONTH);
							day = c.get(Calendar.DAY_OF_MONTH);
						}

						Bundle args = new Bundle();
						args.putInt("year", year);
						args.putInt("month", month);
						args.putInt("day", day);

						DatePickerFragment newFragment = DatePickerFragment
								.newInstance(args);

						// Toast.makeText(mActivity, "HEYYYYYY",
						// Toast.LENGTH_LONG).show();
						
						newFragment.show(getActivity().getFragmentManager(),
								"datePicker");

					}
				});
				rl.addView(dob);

				Button bt = new Button(mActivity);
				// bt.setBackgroundResource(R.drawable.content_event);
				bt.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.content_event, 0, 0, 0);
				bt.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						String dateText = ((TextView) dob).getText().toString();
						int year, month, day;

						String temp[] = dateText.split("-");
						if (temp.length == 3) {

							year = Integer.valueOf(temp[0]);
							month = Integer.valueOf(temp[1]) - 1;
							day = Integer.valueOf(temp[2]);
						} else {
							final Calendar c = Calendar.getInstance();
							year = c.get(Calendar.YEAR);
							month = c.get(Calendar.MONTH);
							day = c.get(Calendar.DAY_OF_MONTH);
						}

						Bundle args = new Bundle();
						args.putInt("day", day);
						args.putInt("month", month);
						args.putInt("year", year);
						DatePickerFragment newFragment = DatePickerFragment
								.newInstance(args);

						// DatePickerFragment newFragment = new
						// DatePickerFragment();
						newFragment.show(getActivity().getFragmentManager(),
								"datePicker");
						// newFragment.init( year, monthOfYear, dayOfMonth, new
						// MyOnDateChangedListener() )

					}
				});
				rl.addView(bt);

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dob
						.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 5, 25, 0);
				if (lastId == 0) {
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					params.addRule(RelativeLayout.BELOW, lastId);
				}

				params = (RelativeLayout.LayoutParams) bt.getLayoutParams();
				//params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.width = 100;
				params.height = 80;
				params.setMargins(5, 5, 15, 5);
				params.addRule(RelativeLayout.ALIGN_BASELINE, id);
				params.addRule(RelativeLayout.LEFT_OF, id);

				TextView tv = new TextView(mActivity);
				tv.setText(attrib.value + ":");
				rl.addView(tv);
				params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
				params.setMargins(25, 0, 0, 0);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_BASELINE, id);

			} else { //regular textfield

				EditText et = new EditText(mActivity);
				et.setSingleLine();
				et.setEms(9);
				et.setId(id);
				if (attrib.answer != null) {
					et.setText(attrib.answer);
				}
				// else if
				// (attrib.name.toString().equalsIgnoreCase("birthdate")) {
				// et.setText("dd-mm-yyyy");
				// }
				// if (attrib.name.toString().equalsIgnoreCase("birthdate")) {
				// et.setInputType(InputType.TYPE_CLASS_DATETIME |
				// InputType.TYPE_DATETIME_VARIATION_DATE);
				// }

				rl.addView(et);

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) et
						.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 5, 25, 0);
				if (lastId == 0) {
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					params.addRule(RelativeLayout.BELOW, lastId);
				}

				TextView tv = new TextView(mActivity);
				tv.setText(attrib.value + ":");
				rl.addView(tv);
				params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
				params.setMargins(25, 0, 0, 0);
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_BASELINE, id);
			}

			lastId = id;
		}

		/* ------------ adds a save button  ------------*/
		Button save = new Button(mActivity);
		save.setText("Save Patient Details");
		
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savePatient();
			}
		});
		save.setCompoundDrawablesWithIntrinsicBounds(R.drawable.content_save,
				0, 0, 0);
		rl.addView(save);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) save
				.getLayoutParams();
		params.setMargins(0, 25, 25, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.BELOW, lastId);
		lastId +=1; save.setId(lastId);
		params.addRule(RelativeLayout.ALIGN_BASELINE, lastId);

		// ------------ delete button---------------------

		if (mActivity.getTitle().equals(EDIT_PATIENT)) {
			// Button for "Patient History"
			

			Button delete = new Button(mActivity);
			delete.setText(R.string.patient_edit_del_bt_title);
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							mActivity);
					alert.setTitle(R.string.patient_edit_del_conf_title);
					alert.setMessage(R.string.patient_edit_del_conf_message);

					alert.setPositiveButton(R.string.patient_edit_del_conf_ok,
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							deletePatient();
						}
					});

					alert.setNegativeButton(R.string.patient_edit_del_conf_cancel,
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
					AlertDialog alertDialog = alert.create();
					alertDialog.show();
				}
			});
			delete.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.content_discard, 0, 0, 0);
			rl.addView(delete);
			params = (RelativeLayout.LayoutParams) delete.getLayoutParams();
			params.setMargins(25, 275, 25, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.BELOW, lastId);
			lastId += 1;
			delete.setId(lastId);
		}
		// initialize the toast message
		// it's initialized here to be able to clear previous toast messsages
		toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		
		return v;
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// dob=(EditText)findViewById(R.id.dob);
	// /* For DOB EditText*/
	// dob.setOnTouchListener(new OnTouchListener(){
	// public boolean onTouch(View v, MotionEvent event) {
	// if(v == dob)
	// showDialog(DATE_DIALOG_ID);
	// return false;
	// }
	// });
	// }

	// private class MyOnDateChangedListener implements OnDateChangedListener {
	// @Override
	// public void onDateChanged(DatePicker view, int year, int monthOfYear, int
	// dayOfMonth) {
	// dob.setText( "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year );
	// }
	// };

	private void savePatient() {
		ArrayList<PatientAttribute> items = mPatient.getAttributes();
		
		for (int i = 0; i < items.size(); i++) {
			int id = i + 1;

			View view = mActivity.findViewById(id);
			PatientAttribute attrib = items.get(i);

			if (view instanceof EditText) {
				EditText et = (EditText) view;

				// do some date clean up %TODO check for short year
				if (attrib.name.toString().equalsIgnoreCase("birthdate")) {
					attrib.answer = parseBirthDate(et.getEditableText()
							.toString());
				} else {
					attrib.answer = et.getEditableText().toString();
				}
			} else if (view instanceof Switch) {
				Switch tb = (Switch) view;
				if (tb.isChecked()) {
					attrib.answer = tb.getTextOn().toString();
				} else {
					attrib.answer = tb.getTextOff().toString();
				}
			}
		}

		Database db = new Database(mActivity);

		// clear previous toast messages
		// Note that toast.cancel() does no work because it 
		// prevents future toast messages from showing as well
		toast.setDuration(0);
		toast.setDuration(toast.LENGTH_SHORT);
		
		if (mViewMode == MODE_ADD) {
			try {
				db.addPatientDetails(items);
			}catch(Exception e) {
				toast.setText(e.getMessage());
				toast.show();
				//Toast.makeText(getActivity(), eee.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			// TODO: change patient record
			try {
				db.updatePatientDetails(mPatient.getPatientID(), items);
			}catch(Exception e) {
				toast.setText(e.getMessage());
				toast.show();
				//Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
				return;
			}
			
		}
		getFragmentManager().popBackStack();

		// Activity a = getActivity();
		//
		// for (PatientAttribute attribute)
		//
		// mPatient.setName(((EditText)a.findViewById(R.id.patient_details_txt_name)).getEditableText().toString());
		// mPatient.setSurname(((EditText)a.findViewById(R.id.patient_details_txt_surname)).getEditableText().toString());
		// mPatient.setAddress(((EditText)a.findViewById(R.id.patient_details_txt_addr)).getEditableText().toString());
		// mPatient.setHomeNumber(((EditText)a.findViewById(R.id.patient_details_txt_home)).getEditableText().toString());
		// mPatient.setWorkNumber(((EditText)a.findViewById(R.id.patient_details_txt_work)).getEditableText().toString());
		// mPatient.setMobileNumber(((EditText)a.findViewById(R.id.patient_details_txt_mobile)).getEditableText().toString());
		// mPatient.setGender(((EditText)a.findViewById(R.id.patient_details_txt_gender)).getEditableText().toString());
		// mPatient.setBirthDate(((EditText)a.findViewById(R.id.patient_details_txt_birthdate)).getEditableText().toString());
		//
		// double weight =
		// Double.parseDouble(((EditText)a.findViewById(R.id.patient_details_txt_weight)).getEditableText().toString());
		// mPatient.setWeight(weight);
		// double height =
		// Double.parseDouble(((EditText)a.findViewById(R.id.patient_details_txt_height)).getEditableText().toString());
		// mPatient.setHeight(height);
		//
		// PatientDetailsDataSource db = new
		// PatientDetailsDataSource(getActivity());
		// db.open();
		// if(mViewMode == MODE_ADD) {
		// db.createPatientDetailsRecord(mPatient);
		// } else {
		// db.updatePatientRecord(mPatient);
		// }
		// db.close();
		// ((FragmentActivity)getActivity()).getSupportFragmentManager().popBackStack();

	}
	
	/*private boolean validatePatientAttributes(ArrayList<PatientAttribute> items) {
		
		int count = 1;
		
		Log.d("DEBUG", items.size() + "");
		
		for (int i = 0; i < items.size(); i++) {
			int id = i + 1;

			PatientAttribute attri = items.get(i);
			Log.d("DEBUG", attri.answer);
		}
		
		for (PatientAttribute attribute : items) {
			if(attribute.answer.equals("")) {
				Log.d("B", count + "th is empty");
				count++;
			}
		}
		
		for (PatientAttribute attribute : items) {
			if(attribute.answer.equals("")) {
				return true;
			}
		}
		return true;
	}*/

	private void deletePatient() {

		Database db = new Database(mActivity);
		db.deletePatient(mPatient.getPatientID());

		getFragmentManager().popBackStack();
	}

	private void getDetails() {
		Bundle b = new Bundle();
		b.putLong(ARG_KEY_PATIENT_HISTORY_ID, mPatient.getPatientID());

		Fragment frag = new PatientHistoryFragment();
		frag.setArguments(b);
		mActivity.setContent(frag);
	}

	private String parseBirthDate(String birthdateString) {

		int count = birthdateString.split("\\.", -1).length - 1;
		int count2 = birthdateString.split("\\/", -1).length - 1;
		int count3 = birthdateString.split("\\-", -1).length - 1;
		SimpleDateFormat targetformat = new SimpleDateFormat("yyyy-MM-dd");
		if (count > 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
			try {
				Date birthdate = format.parse(birthdateString);
				return (String) targetformat.format(birthdate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (count2 > 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			try {
				Date birthdate = format.parse(birthdateString);
				return (String) targetformat.format(birthdate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (count3 > 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date birthdate = format.parse(birthdateString);
				return (String) targetformat.format(birthdate);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "1900-01-01"; // fall back
	}

}
