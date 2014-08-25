package org.get.oxicam.clinicalguide.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.FileUtils;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.db.DatabaseHelper;

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
import android.widget.Toast;

public class ExportFragment extends ListFragment {

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    
    

    /**
     * Initializes the ExportscreenFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle(R.string.exportscreen_title);

	// add some dummy data to the list view
	ArrayList<ExportscreenListItem> items = new ArrayList<ExportscreenListItem>();
	
	
	items.add(new ExportscreenListItem(R.drawable.ic_launcher,
		R.string.exportscreen_item3_title,
		R.string.exportscreen_item3_description));
	items.add(new ExportscreenListItem(R.drawable.content_edit,
		R.string.exportscreen_item4_title,
		R.string.exportscreen_item4_description));
	items.add(new ExportscreenListItem(R.drawable.savedata_icon,
		R.string.exportscreen_item2_title,
		R.string.exportscreen_item2_description));
	items.add(new ExportscreenListItem(R.drawable.syncdata_icon,
		R.string.exportscreen_item1_title,
		R.string.exportscreen_item1_description));

	populateList(items);
    }

    /**
     * Called if a item in the ListView is clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	Fragment frag;
	Bundle args = new Bundle();

	switch (position) {
	case 3:
	    // Synch

	    break;

	case 2:
	    // Save to Disk
		File backupfile=DatabaseHelper.backupDatabaseSDCard(false);
		//make available on PC
		FileUtils.updateFileForMtp(backupfile,mActivity.getApplicationContext());
		
		Toast.makeText(
                mActivity,
                String.format(""+backupfile.getName()+" written to SD card"),
                Toast.LENGTH_SHORT).show();
	    break;

	case 0:
	    // Forms
	    // args = new Bundle();
	    // args.putString(PatientsFragment.ARG_KEY_NEXT_FRAGMENT,
	    // FormscreenFragment.class.getName());
	    frag = Fragment.instantiate(mActivity,
		    FormScreenFragment.class.getName());
	    mActivity.setContent(frag);
	    break;

	case 1: 
	    frag = Fragment.instantiate(mActivity,
		    StatScreenFragment.class.getName());
	    mActivity.setContent(frag);
	    break;

	}
    }

    private void populateList(ArrayList<ExportscreenListItem> items) {
	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (ExportscreenListItem item : items) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("icon", item.icon);
	    map.put("title", mActivity.getString(item.title));
	    map.put("description",
		    mActivity.getResources().getString(item.description));
	    map.put("accessory", "\uE75E");

	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "icon", "title", "description", "accessory" };
	int[] targetIds = { R.id.itemIcon, R.id.itemTitle,
		R.id.itemDescription, R.id.accessoryView };

	// create the ListAdapter
	mAdapter = new SimpleAdapter(getActivity(), itemData,
		R.layout.exportscreen_list_item, sourceTags, targetIds);
	mAdapter.setViewBinder(new ExportscreenBinder());
	setListAdapter(mAdapter);
    }

    class ExportscreenBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data,
		String textRepresentation) {
	    if (view.getId() == R.id.itemTitle
		    || view.getId() == R.id.itemDescription) {
		String text = (String) data;
		TextView textView = (TextView) view;
		textView.setText(text);
	    } else if (view.getId() == R.id.itemIcon) {
		ImageView icon = (ImageView) view;
		if (data != null) {
		    Drawable img = mActivity.getResources().getDrawable(
			    (Integer) data);// (Drawable)data;
		    icon.setImageDrawable(img);
		} else {
		    icon.setVisibility(View.GONE);
		}
	    } else if (view.getId() == R.id.accessoryView) {
		TextView textView = (TextView) view;
		Typeface font = Typeface.createFromAsset(mActivity.getAssets(),
			"fontello.ttf");
		textView.setTypeface(font);
	    }
	    return true;
	}
    }

}
