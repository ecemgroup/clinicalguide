package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;

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

public class HomescreenFragment extends ListFragment {
	
	private ClinicalGuideActivity mActivity;
	private SimpleAdapter mAdapter;
	
	/**
	 * Initializes the HomescreenFragment.
	 */
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mActivity = (ClinicalGuideActivity)getActivity();
		mActivity.setTitle("Home");
        
        // add some dummy data to the list view
        ArrayList<HomescreenListItem> items = new ArrayList<HomescreenListItem>();
        items.add(new HomescreenListItem(R.drawable.assessment_icon, R.string.homescreen_assessment, R.string.homescreen_assessment_description));
        items.add(new HomescreenListItem(R.drawable.followup_icon, R.string.homescreen_item2_title, R.string.homescreen_item2_description));
        items.add(new HomescreenListItem(R.drawable.patientdata_icon, R.string.homescreen_item3_title, R.string.homescreen_item3_description));
        items.add(new HomescreenListItem(R.drawable.exportdata_icon, R.string.homescreen_item4_title, R.string.homescreen_item4_description));
        
        populateList(items);
    }

	/**
	 * Called if a item in the ListView is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Fragment frag;
		Bundle args = new Bundle();
		
		switch(position) {
		case 0:
			// Assessment
			args = new Bundle();
			args.putString(PatientsFragment.ARG_KEY_NEXT_FRAGMENT, MainSymptomFragment.class.getName());
			frag = Fragment.instantiate(mActivity, PatientsFragment.class.getName(), args);
			mActivity.setContent(frag);
			break;
			
		case 1:
			// Follow-Up
			args = new Bundle();
			args.putString(PatientsFragment.ARG_KEY_NEXT_FRAGMENT, FollowUpFragment.class.getName());
			frag = Fragment.instantiate(mActivity, PatientsFragment.class.getName(), args);
			mActivity.setContent(frag);
			break;

		case 2:
			// Patient DB
			args = new Bundle();
			args.putString(PatientsFragment.ARG_KEY_NEXT_FRAGMENT, PatientDetailsFragment.class.getName());
			frag = Fragment.instantiate(mActivity, PatientsFragment.class.getName(), args);
			mActivity.setContent(frag);
			break;
		case 3:
			// Export DB
			args = new Bundle();
			args.putString(PatientsFragment.ARG_KEY_NEXT_FRAGMENT, ExportFragment.class.getName());
			frag = Fragment.instantiate(mActivity, ExportFragment.class.getName(), args);
			mActivity.setContent(frag);
			break;
		}
	}
	
	private void populateList(ArrayList<HomescreenListItem> items) {
		// populate a HashMap with all list items
		ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
		for(HomescreenListItem item : items) {
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("icon", item.icon);
	        map.put("title", mActivity.getString(item.title));
	        map.put("description", mActivity.getResources().getString(item.description));
	        map.put("accessory", "\uE75E");

	        itemData.add(map);	
		}
    	
		// Specify source tag to corresponding target ID mapping
        String[] sourceTags = {
        		"icon",
        		"title",
        		"description",
        		"accessory"
        };
        int[] targetIds = {
        	R.id.itemIcon,
        	R.id.itemTitle,
        	R.id.itemDescription,
        	R.id.accessoryView
        };
        
        // create the ListAdapter
        mAdapter = new SimpleAdapter(getActivity(), itemData, R.layout.homescreen_list_item, sourceTags, targetIds);
        mAdapter.setViewBinder(new HomescreenBinder());
        setListAdapter(mAdapter);
	}
	
	class HomescreenBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Object data, String textRepresentation) {
			if (view.getId() == R.id.itemTitle || view.getId() == R.id.itemDescription) {
				String text = (String)data;
				TextView textView = (TextView)view;
				textView.setText(text);
			}
			else if (view.getId() == R.id.itemIcon) {
				ImageView icon = (ImageView)view;
				if(data != null) {
					Drawable img = mActivity.getResources().getDrawable((Integer)data);//(Drawable)data;
					icon.setImageDrawable(img);
				} else {
					icon.setVisibility(View.GONE);
				}
			}
			else if (view.getId() == R.id.accessoryView) {
				TextView textView = (TextView)view;
		        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "fontello.ttf");  
		        textView.setTypeface(font);
			}
			return true;
		}
	}

}
