package org.get.oxicam.clinicalguide.ui;

import java.io.File;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.FileUtils;
import org.get.oxicam.clinicalguide.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryScreenFragment extends Fragment {

    private ClinicalGuideActivity mActivity;

    // private SimpleAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Summary");
	View v = inflater.inflate(R.layout.summaryscreen_list_item, container,
		false);
	RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.layout);
	rl.setPadding(10, 10, 10, 10); // setPadding for entire screen
	Object arg = getArguments().get("summary"); // get the contents

	Display display = mActivity.getWindowManager().getDefaultDisplay();
	Point size = new Point();
	display.getSize(size);

	// text view
	TextView summaryTv = new TextView(mActivity);
	summaryTv.setId(1);
	summaryTv.setHint("This is Summary of a form");
	summaryTv.setText((String) arg); // get the summary data to the summary
					 // textview area
	summaryTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // setTextSize
	rl.addView(summaryTv);
	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) summaryTv
		.getLayoutParams();

	// Comment - EditText
	EditText commentET = new EditText(mActivity);
	commentET.setId(4);
	commentET.setHint("Comments");
	commentET.setMaxHeight(size.y / 2 - 100); // !!!!!!!!!!!!!!
	// commentET.setLayoutParams(new ViewGroup.LayoutParams(size.x,
	// size.y/2));
	rl.addView(commentET);

	params = (RelativeLayout.LayoutParams) commentET.getLayoutParams();
	params.addRule(RelativeLayout.BELOW, summaryTv.getId());
	params.topMargin = 10;
	params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
	// params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

	// Button = "View Detail"
	Button viewDetailBtn = new Button(mActivity);
	viewDetailBtn.setId(3);
	viewDetailBtn.setText("View Detail");
	viewDetailBtn.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle b = new Bundle();

		String details = getArguments().getString("details");
		details += "\n\nComments";
		details += "\n============";
		EditText comment = (EditText) mActivity.findViewById(4);
		details += "\n" + comment.getText().toString();
		b.putString("detail", details);
		Fragment frag = Fragment.instantiate(mActivity,
			ViewDetailScreenFragment.class.getName());
		frag.setArguments(b);
		mActivity.setContent(frag);
	    }
	});
	rl.addView(viewDetailBtn);
	params = (RelativeLayout.LayoutParams) viewDetailBtn.getLayoutParams();
	params.addRule(RelativeLayout.BELOW, commentET.getId());
	params.topMargin = 10;
	params.bottomMargin = 10;
	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

	// Cancel Button
	Button cancelBtn = new Button(mActivity);
	cancelBtn.setId(5);
	cancelBtn.setText("Cancel");
	cancelBtn.setCompoundDrawablesWithIntrinsicBounds(
		R.drawable.navigation_cancel, 0, 0, 0);
	cancelBtn.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setTitle("Confirmation");
		alert.setMessage("Do you really want to cancel?");

		alert.setPositiveButton("Ok",
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				// TODO Auto-generated method stub
				mActivity.onBackPressed();
			    }
			});

		alert.setNegativeButton("Cancel",
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

	rl.addView(cancelBtn);
	params = (RelativeLayout.LayoutParams) cancelBtn.getLayoutParams();
	params.addRule(RelativeLayout.BELOW, viewDetailBtn.getId());
	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

	// OK Button
	Button okBtn = new Button(mActivity);
	okBtn.setId(6);
	okBtn.setText("Save");
	okBtn.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle b = getArguments();
		String data = b.getString("details");
		String html = b.getString("html");

		data += "\n\nComments";
		html += "<div title=\"comments\">\n";
		data += "\n============";
		EditText comment = (EditText) mActivity.findViewById(4);
		data += "\n" + comment.getText().toString();
		String reportName = b.getString("filename");
		html += comment.getText().toString();
		html += "\n</div>\n";

		Log.w("hi", Environment.getExternalStorageDirectory()
			+ "/oxicam/" + reportName);
		FileUtils.writeFile(html, reportName);
		Toast.makeText(mActivity, "Success saving files",
			Toast.LENGTH_SHORT).show();
	    }
	});
	okBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.content_save,
		0, 0, 0);
	rl.addView(okBtn);
	params = (RelativeLayout.LayoutParams) okBtn.getLayoutParams();
	params.addRule(RelativeLayout.BELOW, viewDetailBtn.getId());
	params.addRule(RelativeLayout.LEFT_OF, cancelBtn.getId());

	// email button
	Button emailButton = new Button(mActivity);
	emailButton.setId(7);
	emailButton.setText("Save and Email");
	emailButton.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("application/octet-stream");
		Bundle b = getArguments();
		String data = b.getString("details");
		String html = b.getString("html");

		data += "\n\nComments";
		html += "<div title=\"comments\">\n";
		data += "\n============";
		EditText comment = (EditText) mActivity.findViewById(4);
		data += "\n" + comment.getText().toString();
		String reportName = b.getString("filename");
		html += comment.getText().toString();
		html += "\n</div>\n";

		Log.w("hi", Environment.getExternalStorageDirectory()
			+ "/oxicam/" + reportName);
		FileUtils.writeFile(html, reportName);
		Toast.makeText(mActivity, "Success saving files",
			Toast.LENGTH_SHORT).show();
		File file = new File(Environment.getExternalStorageDirectory()
			+ "/oxicam/" + reportName);
		Log.w("hi", file.exists() + "");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, reportName);
		mActivity.startActivity(Intent.createChooser(emailIntent,
			"Send With..."));
	    }
	});
	emailButton.setCompoundDrawablesWithIntrinsicBounds(
		R.drawable.content_new_attachment, 0, 0, 0);
	rl.addView(emailButton);
	params = (RelativeLayout.LayoutParams) emailButton.getLayoutParams();
	params.addRule(RelativeLayout.BELOW, viewDetailBtn.getId());
	params.addRule(RelativeLayout.LEFT_OF, okBtn.getId());

	return v;

    }
}
