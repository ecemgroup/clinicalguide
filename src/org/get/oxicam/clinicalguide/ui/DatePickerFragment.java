package org.get.oxicam.clinicalguide.ui;

import java.util.Calendar;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {


	static DatePickerFragment newInstance(Bundle args) {
		DatePickerFragment f = new DatePickerFragment();

		f.setArguments(args);

		return f;
	}
	private int dialog;
	
	
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		super.onCreate(savedInstanceState);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (getArguments() != null) {
			year = getArguments().getInt("year");
			month = getArguments().getInt("month");
			day = getArguments().getInt("day");
			dialog = getArguments().getInt("dialog");
		}

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		String selectedDate = Integer.toString(year)+"-"+String.format("%02d",month+1)+"-"+String.format("%02d",day);
		// date_text.setText(selectedDate);
		System.out.println("selected date is:"+selectedDate);
		// edittext.setText(selectedDate );
		//Context context = getActivity().getApplicationContext();
		ClinicalGuideActivity mActivity = (ClinicalGuideActivity) getActivity();
		if(mActivity.getTitle().equals("Add Patient") || mActivity.getTitle().equals("Edit Patient")){
			int id = 4; //FixME: Hardcoded birthday field
			EditText datetext = (EditText)getActivity().findViewById(id);
			datetext.setText(selectedDate);
		}else if(mActivity.getTitle().equals("Forms")){
			/*
			String startDate = Integer.toString(year)+"-"+String.format("%02d",month+1)+"-"+String.format("%02d",day-7);
			
			EditText startText = (EditText)getActivity().findViewById(1);
			startText.setText(startDate);
			*/
			EditText datetext = (EditText)getActivity().findViewById(dialog);
			datetext.setText(selectedDate);
			
		}
	}
}