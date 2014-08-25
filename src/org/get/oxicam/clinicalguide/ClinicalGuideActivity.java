package org.get.oxicam.clinicalguide;

import java.io.File;

import org.ecemgroup.sharevitalsigns.library.ShareVitalSigns;
import org.get.oxicam.clinicalguide.ui.HomescreenFragment;
import org.get.oxicam.clinicalguide.ui.ListItemOnClickListener;
import org.get.oxicam.clinicalguide.xml.CGFormParser;
import org.get.oxicam.clinicalguide.xml.CGParser;
import org.get.oxicam.clinicalguide.xml.CGStatsParser;
import org.get.oxicam.clinicalguide.xml.data.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ClinicalGuideActivity extends FragmentActivity {

       
    public static ShareVitalSigns sharevitals = new ShareVitalSigns(0);

    private CGParser mXmlParser;
    private CGFormParser mFormParser;
    private CGStatsParser mStatsParser;
  //  private ShareVitalSignResultsReceiver vitalSignReceiver;
    private User user;
    
    

    public void setXmlParser(CGParser xmlParser) {
	this.mXmlParser = xmlParser;
    }

    public User getUser() {
	return user;
    }

    private int mHomescreenId;
    private ListItemOnClickListener listItemListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	mXmlParser = new CGParser(this);
	mFormParser = new CGFormParser(this);
	mStatsParser = new CGStatsParser(this);
	user = LoginActivity.getUser();

	getSupportFragmentManager().addOnBackStackChangedListener(
		new FragmentManager.OnBackStackChangedListener() {
		    @Override
		    public void onBackStackChanged() {
			int stackSize = getSupportFragmentManager()
				.getBackStackEntryCount();
			if (stackSize == 0) {
			    // exit app
			    finish();
			}
		    }
		});

	mHomescreenId = setContent(new HomescreenFragment());
    }

    public CGParser getXmlParser() {
	return mXmlParser;
    }
    
    public CGFormParser getFormParser() {
	return mFormParser;
    }

    public CGStatsParser getStatsParser() {
	return mStatsParser; 
    }

    /**
     * Callback method that is called if a button in a ListView item is clicked.
     * Maybe a bit messy to have the listener method here instead of in the
     * corresponding fragment, but XML defined listener methods have to be in
     * the Activity.
     * 
     * @param v
     *            View that was clicked.
     */
    public void onListItemClick(View v) {
	if (listItemListener != null) {
	    listItemListener.onListItemClick(v);
	}
    }

    /**
     * Displays a new Fragment. The previously displayed Fragment is added to
     * the back-stack. Fragments on the back-stack will show up again after
     * pressing the back button.
     * 
     * @param fragment
     *            The Fragment to display
     * @return The transaction ID.
     */
    public int setContent(Fragment fragment) {
	Log.d("Activity", "setContent: " + fragment.getClass().getName());

	if (fragment instanceof ListItemOnClickListener) {
	    listItemListener = (ListItemOnClickListener) fragment;
	} else {
	    listItemListener = null;
	}

	FragmentManager fm = getSupportFragmentManager();
	FragmentTransaction transaction = fm.beginTransaction();
	transaction.replace(android.R.id.content, fragment);
	transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	transaction.addToBackStack(null);
	return transaction.commit();
    }

    /**
     * Pops the entire fragment backstack to display the homescreen.
     */
    public void gotoHomescreen() {
	Log.d("Activity", "goto homescreen");
	FragmentManager fm = getSupportFragmentManager();
	fm.popBackStack(mHomescreenId, 0);
    }

    /**
     * Open a measurement app and request heart rate measurement.
     */
   /* public void measureHeartRate(ShareVitalSignsResultReceiver resultReceiver) {
    	vitalSignReceiver = resultReceiver;
    	startVitalSignAquisition(ShareVitalSigns.MEASURE_HR);

    }*/
    /**
     * Open a measurement app and request heart rate measurement.
     */
    /*public void measurePulseOx(ShareVitalSignsResultReceiver resultReceiver) {
    	vitalSignReceiver = resultReceiver;
    	startVitalSignAquisition(ShareVitalSigns.MEASURE_PO);

    }*/
    /**
     * Open a measurement app and request respiratory rate measurement.
     */
   /* public void measureRespiratoryRate(ShareVitalSignsResultReceiver resultReceiver) {
    	vitalSignReceiver = resultReceiver;
    	startVitalSignAquisition(ShareVitalSigns.MEASURE_RR);
        }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_clinical_guide_main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.menu_home) {
	    gotoHomescreen();
	    return true;

	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
    	
		/*
		 * To check and process the received data in the Intent, execute 
		 * ShareVitals_get_results(int requestCode, int resultCode, Intent data) it will return "true" if 
		 * the requested vital sign was obtained, "false" otherwise
		 */
			if (sharevitals.getResults(requestCode, resultCode,  data)){ //process Intent data
				
					//Do something here if necessary
			
			} else {
				Toast.makeText(
	                    this,
	                    String.format("WARNING: No valid vital sign obtained"),
	                    Toast.LENGTH_SHORT).show();
			}
		
    	
    	
    }


    /**
     * Returns the base directory for the xml
     * TODO use FileUtils function
     */
    public static File getClinicalGuideDirectory() {
	File f = new File(Environment.getExternalStorageDirectory()
		.getAbsolutePath() + "/ClinicalGuide");
	// create directory if it does not exist
	f.mkdirs();
	return f;
    }

    /*
    public interface VitalSignResultReceiver {
	public void onVitalSignResult(float vs1, float vs2);
    }*/
}
