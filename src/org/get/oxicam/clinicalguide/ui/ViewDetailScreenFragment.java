package org.get.oxicam.clinicalguide.ui;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewDetailScreenFragment extends Fragment{
	private ClinicalGuideActivity mActivity;
	
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mActivity = (ClinicalGuideActivity) getActivity();
		mActivity.setTitle("View Detail");
		View v = inflater.inflate(R.layout.summaryscreen_list_item, container, false);
		RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.layout);
		rl.setPadding(10, 10, 10, 10);		// setPadding for entire screen
		Object arg = getArguments().get("detail");	// get the contents

		
		
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		ScrollView sv = new ScrollView(mActivity);
		sv.setLayoutParams(new ViewGroup.LayoutParams(size.x, size.y));
		
		// text view
		TextView detailTv = new TextView(mActivity);
		detailTv.setId(1);
		detailTv.setHint("This is detail of the form");
		detailTv.setText((String)arg);		// get the summary data to the summary textview area
		detailTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);	// setTextSize
		
		sv.addView(detailTv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rl.addView(sv);
		return v;
	}
}