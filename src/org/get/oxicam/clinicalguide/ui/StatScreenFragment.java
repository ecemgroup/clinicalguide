package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.CGStatsParser;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;

public class StatScreenFragment extends ListFragment {  
    
    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private ArrayList<ViewGroup> statResults;
    
    /**
     * Initializes the FormscreenFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mActivity = (ClinicalGuideActivity)getActivity();
        mActivity.setTitle("General Statistics");
    
        // add some dummy data to the list view
        //ArrayList<StatscreenListItem> items = new ArrayList<StatscreenListItem>();
        //items.add(new StatscreenListItem(R.drawable.alerts_and_states_warning, R.string.statscreen_item1_title, R.string.statscreen_item1_description));
        
        ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
        ArrayList<String> statsNames = new ArrayList<String>();
        statResults = new ArrayList<ViewGroup>();
        final ArrayList<String> statsIds = new ArrayList<String>();
        final CGStatsParser statsParser = mActivity.getStatsParser();
        HashMap<String, String> map = statsParser.getStatsNamesAndIds();
        Set<String> keySet = map.keySet();
        for(String s : keySet){
            statsIds.add(s);
            statsNames.add(map.get(s));
        }
        items.add(statsNames);
        
        
        ViewGroup viewGroupFooter = new RelativeLayout(mActivity);
        Button button = new Button(mActivity);
        button.setText("Get Statistics");
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Spinner s = (Spinner)mActivity.findViewById(R.id.spinner1);
                String result = statsParser.populateStats(statsIds.get(s.getSelectedItemPosition()));
                Log.v("Stat result", result);
                
                final ViewGroup resultGroup = new RelativeLayout(mActivity);
                Button remove = new Button(mActivity);
                remove.setText("x");
                remove.setId(1);
                remove.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						getListView().removeFooterView(resultGroup);
						statResults.remove(resultGroup);
						
					}
				});
                
                resultGroup.addView(remove);
                RelativeLayout.LayoutParams removeFooter = (RelativeLayout.LayoutParams) remove
                        .getLayoutParams();
                
                removeFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                remove.setLayoutParams(removeFooter);
                
                
                TextView tv = new TextView(mActivity);
                tv.setText(result);
                resultGroup.addView(tv);
                removeFooter = (RelativeLayout.LayoutParams) tv
                        .getLayoutParams();
                removeFooter.addRule(RelativeLayout.LEFT_OF, 1);
                removeFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                tv.setLayoutParams(removeFooter);
                
                statResults.add(resultGroup);
                getListView().addFooterView(resultGroup);
                
               
            }
        });
        viewGroupFooter.addView(button);
        RelativeLayout.LayoutParams paramsFooter = (RelativeLayout.LayoutParams) button
            .getLayoutParams();
        paramsFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsFooter.setMargins(0, 50, 0, 0);
        button.setLayoutParams(paramsFooter);
        
        Button clearAll = new Button (mActivity);
        clearAll.setText("Clear All");
        clearAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(ViewGroup vg: statResults){
					getListView().removeFooterView(vg);
				}
				
			}
		});
        
        viewGroupFooter.addView(clearAll);
        RelativeLayout.LayoutParams clearFooter = (RelativeLayout.LayoutParams) clearAll
                .getLayoutParams();
        clearFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        clearFooter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        clearAll.setLayoutParams(clearFooter);
        getListView().addFooterView(viewGroupFooter);
        
        populateList(items);
    }
    
    private void populateList(ArrayList<ArrayList<String>> items) {
        // populate a HashMap with all list items
     // populate a HashMap with all list items
        ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
        for(ArrayList<String> item : items) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("texts", item);
            itemData.add(map);  
        }
        
        // Specify source tag to corresponding target ID mapping
        String[] sourceTags = {
                "texts"
        };
        int[] targetIds = {
            R.id.spinner1
        };
        
        // create the ListAdapter
        mAdapter = new SimpleAdapter(getActivity(), itemData, R.layout.statscreen_list_item, sourceTags, targetIds);
        mAdapter.setViewBinder(new FormscreenBinder());
        setListAdapter(mAdapter);
    }
    
    
    class FormscreenBinder implements ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if(view.getId() == R.id.spinner1){
                
                Spinner s = (Spinner)view;
                s.setPrompt("Select a statistics");
                ArrayAdapter<String> arrAdap = new ArrayAdapter<String>(mActivity, R.layout.spinner_layout, (ArrayList<String>)data);
                arrAdap.setDropDownViewResource(R.layout.spinner_layout);
                s.setAdapter(arrAdap);
                
                return true;    
            }
            
            return false;
        }
    }
    
    
    
    
}
