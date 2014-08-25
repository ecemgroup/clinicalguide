package org.get.oxicam.clinicalguide.ui;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.data.Assessment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TreatmentConfirmationDialog extends DialogFragment {
	
	private Assessment mAssessment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.confirm_treatment);
        View v = inflater.inflate(R.layout.confirm_treatment_dialog, container);

        Bundle args = getArguments();
        if (args != null) {
        	mAssessment = (Assessment)args.get(TreatmentFragment.ARG_KEY_QUESTIONNAIRES);
            
            TextView tv = (TextView)v.findViewById(R.id.chosenTreatment);
            tv.setText(mAssessment.chosenTreatment.classification);
        }
        
        v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                getFragmentManager().popBackStack();
			}
		});
        
        v.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                getFragmentManager().popBackStack();
                
				ClinicalGuideActivity a = (ClinicalGuideActivity)getActivity();
				Bundle args = new Bundle();
				args.putSerializable(TreatmentFragment.ARG_KEY_QUESTIONNAIRES, mAssessment);
				Fragment frag = Fragment.instantiate(a, TreatmentFragment.class.getName(), args);
				a.setContent(frag);
				
			}
		});

        return v;
    }

}
