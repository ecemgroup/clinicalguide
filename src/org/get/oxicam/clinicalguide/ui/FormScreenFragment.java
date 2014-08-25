package org.get.oxicam.clinicalguide.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.CGFormParser;
import org.get.oxicam.clinicalguide.xml.FormGenerator;
import org.get.oxicam.clinicalguide.xml.forms.Form;
import org.get.oxicam.clinicalguide.xml.forms.FormCell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;

public class FormScreenFragment extends ListFragment {

    private ClinicalGuideActivity mActivity;
    private SimpleAdapter mAdapter;
    private long endTimeMilli;
    private String formId;
    private ArrayList<String> forms;
    private CGFormParser formCG;
    private int position;
    private String prevFormId;
    private ArrayList<ViewGroup> cellViews;
    private ArrayList<String> cellString;
    private ArrayList<Integer> cellId;
    private CGFormParser formParser;
    private ArrayList<String> formIds;
    private ViewGroup exportButton;
    private ArrayList<String> cellQuestions;
    private ArrayList<ViewGroup> saveState = new ArrayList<ViewGroup>();
    private ArrayList<String> formNames;

    /**
     * Initializes the FormscreenFragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	mActivity = (ClinicalGuideActivity) getActivity();
	mActivity.setTitle("Forms");
	endTimeMilli = System.currentTimeMillis();
	cellViews = new ArrayList<ViewGroup>();
	cellString = new ArrayList<String>();
	cellId = new ArrayList<Integer>();
	cellQuestions = new ArrayList<String>();

	ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
	formNames = new ArrayList<String>();
	formIds = new ArrayList<String>();
	formParser = mActivity.getFormParser();
	HashMap<String, String> map = formParser.getFormNamesAndIds();
	Set<String> keySet = map.keySet();
	for (String s : keySet) {
	    formIds.add(s);
	    formNames.add(map.get(s));
	}
	items.add(formNames);

	forms = new ArrayList<String>(formIds);

	formCG = formParser;

	long start = formCG.getStartDay(formIds.get(0), endTimeMilli);
	// sdFooter.addRule(RelativeLayout.OVER_SCROLL_NEVER);

	final Calendar calendar = Calendar.getInstance();
	// getListView().addFooterView(viewDateFooter);
	calendar.setTimeInMillis(start);

	int yearStart = calendar.get(Calendar.YEAR);
	int monthStart = calendar.get(Calendar.MONTH) + 1;
	int dayStart = calendar.get(Calendar.DAY_OF_MONTH);

	String dateStartText = yearStart + "-"
		+ String.format("%02d", monthStart) + "-"
		+ String.format("%02d", dayStart);
	// Start Date input text
	ViewGroup viewDateFooter = new RelativeLayout(mActivity);
	final EditText startDate = new EditText(mActivity);
	startDate.setSingleLine();
	startDate.setEms(7);
	startDate.setEnabled(false);
	startDate.setId(1);
	startDate.setText(dateStartText);

	startDate.setInputType(InputType.TYPE_CLASS_DATETIME
		| InputType.TYPE_DATETIME_VARIATION_DATE);

	viewDateFooter.addView(startDate);
	RelativeLayout.LayoutParams sdFooter = (RelativeLayout.LayoutParams) startDate
		.getLayoutParams();
	sdFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	sdFooter.setMargins(0, 0, 0, 0);
	startDate.setLayoutParams(sdFooter);

	// Start date text
	TextView tvs = new TextView(mActivity);
	tvs.setText("Start date");
	tvs.setTextSize(16);
	viewDateFooter.addView(tvs);
	sdFooter = (RelativeLayout.LayoutParams) tvs.getLayoutParams();
	sdFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	// sdFooter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	// sdFooter.addRule(RelativeLayout.OVER_SCROLL_NEVER);
	// edFooter.addRule(RelativeLayout.)
	sdFooter.setMargins(0, 0, 50, 0);
	tvs.setLayoutParams(sdFooter);
	getListView().addFooterView(viewDateFooter);

	// =======================================================================================
	// End date input text
	ViewGroup viewEndFooter = new RelativeLayout(mActivity);
	final EditText endDate = new EditText(mActivity);
	endDate.setSingleLine();
	endDate.setEms(7);
	endDate.setEnabled(false);
	endDate.setId(2);
	endDate.setInputType(InputType.TYPE_CLASS_DATETIME
		| InputType.TYPE_DATETIME_VARIATION_DATE);
	final Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);
	int month = c.get(Calendar.MONTH) + 1;
	int day = c.get(Calendar.DAY_OF_MONTH);
	endDate.setText(year + "-" + String.format("%02d", month) + "-"
		+ String.format("%02d", day)); // default date is
					       // current date
	viewEndFooter.addView(endDate);

	RelativeLayout.LayoutParams edFooter = (RelativeLayout.LayoutParams) endDate
		.getLayoutParams();
	// edFooter.addRule(RelativeLayout.OVER_SCROLL_NEVER);
	edFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	// edFooter.setMargins(0, 0, 0, 0);
	endDate.setLayoutParams(edFooter);
	// getListView().addFooterView(viewEndFooter);

	// end date text
	TextView tv = new TextView(mActivity);
	tv.setText("End date");
	tv.setTextSize(16);
	viewEndFooter.addView(tv);
	edFooter = (RelativeLayout.LayoutParams) tv.getLayoutParams();
	// edFooter.addRule(RelativeLayout.OVER_SCROLL_NEVER);
	edFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	// edFooter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	// edFooter.addRule(RelativeLayout.)
	edFooter.setMargins(0, 0, 50, 0);
	tv.setLayoutParams(edFooter);
	// getListView().addFooterView(viewEndFooter);

	// End Date button: end date picker
	Button bte = new Button(mActivity);

	// bt.setBackgroundResource(R.drawable.content_event);
	bte.setCompoundDrawablesWithIntrinsicBounds(R.drawable.content_event,
		0, 0, 0);
	bte.setOnClickListener(new OnClickListener() {
	    public void onClick(View arg0) {
		String dateText = ((TextView) endDate).getText().toString();
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
		args.putInt("dialog", 2);
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

	viewEndFooter.addView(bte);
	RelativeLayout.LayoutParams edBteFooter = (RelativeLayout.LayoutParams) bte
		.getLayoutParams();
	edBteFooter = (RelativeLayout.LayoutParams) bte.getLayoutParams();
	edBteFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	// edBteFooter.addRule(RelativeLayout.OVER_SCROLL_NEVER);
	edBteFooter.setMargins(300, 0, 0, 0);
	bte.setLayoutParams(edBteFooter);
	getListView().addFooterView(viewEndFooter);

	populateList(items);

	endDate.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1,
		    int arg2, int arg3) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
		    int arg3) {
		// TODO Auto-generated method stub
		dateToLong(endDate);
		long startMilli = formCG.getStartDay(formId, endTimeMilli);
		c.setTimeInMillis(startMilli);

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		String dateText = year + "-" + String.format("%02d", month)
			+ "-" + String.format("%02d", day);
		startDate.setText(dateText);
	    }

	});

	dateToLong(endDate);
	

    }

    public void dateToLong(EditText endDate) {
	int yearL, monthL, dayL;
	String dateText = endDate.getText().toString();
	String temp[] = dateText.split("-");
	if (temp.length == 3) {

	    yearL = Integer.valueOf(temp[0]);
	    monthL = Integer.valueOf(temp[1]) - 1;
	    dayL = Integer.valueOf(temp[2]);

	    final GregorianCalendar cal = new GregorianCalendar(yearL, monthL,
		    dayL + 1);

	    endTimeMilli = cal.getTimeInMillis() - 1;

	}

    }

    private void populateList(ArrayList<ArrayList<String>> items) {
	// populate a HashMap with all list items
	ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
	for (ArrayList<String> item : items) {
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("texts", item);
	    itemData.add(map);
	}

	// Specify source tag to corresponding target ID mapping
	String[] sourceTags = { "texts" };
	int[] targetIds = { R.id.spinner1 };

	// create the ListAdapter
	mAdapter = new SimpleAdapter(getActivity(), itemData,
		R.layout.formscreen_list_item, sourceTags, targetIds);
	mAdapter.setViewBinder(new FormscreenBinder());
	setListAdapter(mAdapter);

    }

    class FormscreenBinder implements ViewBinder {

	@Override
	public boolean setViewValue(View view, Object data,
		String textRepresentation) {
	    if (view.getId() == R.id.spinner1) {

		final Spinner s = (Spinner) view;
		s.setPrompt("Choose a form");
		final ArrayAdapter<String> arrAdap = new ArrayAdapter<String>(
			mActivity, android.R.layout.simple_list_item_1,
			(ArrayList<String>) data);
		arrAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(arrAdap);
		prevFormId = forms.get(position);

		s.setSelection(position);
		s.setOnItemSelectedListener(new OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(AdapterView<?> arg0, View arg1,
			    int arg2, long arg3) {

			String temp = formId;// Fixes first selection
			position = s.getSelectedItemPosition();
			formId = forms.get(s.getSelectedItemPosition());

			if (!prevFormId.equals(formId) || temp == null) {
			    for (ViewGroup vg : saveState) {
				getListView().removeFooterView(vg);
			    }
			    saveState.clear();

			    cellId.clear();

			    ArrayList<FormCell> fc = formCG
				    .getInputTypeCells(formId);

			    /**
			     * remove any cell inputs that was being added
			     */
			    if (!cellViews.isEmpty()) {
				for (ViewGroup vg : cellViews) {
				    getListView().removeFooterView(vg);

				}
			    }

			    cellViews.clear();// Clear arrayList of views

			    /**
			     * Create Cell inputs
			     */
			    for (int i = 0; i < fc.size(); i++) {
				ViewGroup cellGroup = new RelativeLayout(
					mActivity);
				final EditText cell = new EditText(mActivity);
				cell.setSingleLine();
				cell.setEms(7);
				cell.setId(i + 5);
				cell.setHint(fc.get(i).value);
				cellId.add(i + 5);

				cellGroup.addView(cell);
				RelativeLayout.LayoutParams cellFooter = (RelativeLayout.LayoutParams) cell
					.getLayoutParams();
				cellFooter
					.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				cell.setLayoutParams(cellFooter);

				TextView tv = new TextView(mActivity);
				tv.setText(fc.get(i).name);
				cellQuestions.add(fc.get(i).name);
				tv.setTextSize(16);
				cellGroup.addView(tv);
				RelativeLayout.LayoutParams cellTextFooter = (RelativeLayout.LayoutParams) tv
					.getLayoutParams();
				cellTextFooter
					.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				cellTextFooter
					.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

				cellTextFooter.setMargins(0, 0, 60, 0);
				tv.setLayoutParams(cellTextFooter);

				getListView().addFooterView(cellGroup);
				cellViews.add(cellGroup);// to keep track of
							 // what's being
							 // added to the
							 // screen
				saveState.add(cellGroup);// to save state

			    }

			    // s.setSelection(position, true);

			    /**
			     * Set start date accordingly to the end date
			     */
			    EditText sdDate = (EditText) mActivity
				    .findViewById(1);
			    EditText edDate = (EditText) mActivity
				    .findViewById(2);
			    final Calendar c = Calendar.getInstance();
			    int year = c.get(Calendar.YEAR);
			    int month = c.get(Calendar.MONTH) + 1;
			    int day = c.get(Calendar.DAY_OF_MONTH);
			    String dateText = year + "-"
				    + String.format("%02d", month) + "-"
				    + String.format("%02d", day);
			    edDate.setText(dateText);
			    dateToLong(edDate);

			    long startMilli = formCG.getStartDay(formId,
				    endTimeMilli);
			    c.setTimeInMillis(startMilli);

			    year = c.get(Calendar.YEAR);
			    month = c.get(Calendar.MONTH) + 1;
			    day = c.get(Calendar.DAY_OF_MONTH);
			    dateText = year + "-"
				    + String.format("%02d", month) + "-"
				    + String.format("%02d", day);
			    sdDate.setText(dateText);

			    ViewGroup viewGroupFooter = new RelativeLayout(
				    mActivity);

			    // ======================Export
			    // Button==================================================================
			    getListView().removeFooterView(exportButton);
			    Button button = new Button(mActivity);
			    button.setText("Export now!");
			    button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

				    Form f = formParser.populateForm(formIds
					    .get(position));
				    f.setStartAndAndDate(endTimeMilli);

				    FormGenerator builder = new FormGenerator(
					    mActivity, f);
				    String str = builder.getFormResult();
				    String html = builder.getHTMLResult();

				    Bundle b = new Bundle();
				    String summary = "Current user: "
					    + mActivity.getUser().username
					    + "\n";
				    summary += "Reportdate:\n";
				    summary += "From: "
					    + new Date(f.getStartDate())
						    .toString() + "\n";
				    summary += "To: "
					    + new Date(f.getEndDate())
						    .toString() + "\n";
				    summary += "Report name: " + f.name;

				    for (Integer oneCell : cellId) {
					EditText cellInput = (EditText) mActivity
						.findViewById(oneCell);
					String text = cellInput.getText()
						.toString();
					if (!text.equals("")) {
					    cellString.add(text);
					} else {
					    text = cellInput.getHint()
						    .toString();
					    cellString.add(text);
					}
				    }
				    str += "\n Cell Inputs ";
				    str += "\n==================";
				    html += "<div title=\"cellInput\">";
				    if (cellString.size() != 0) {
					for (int i = 0; i < cellString.size(); i++) {
					    str += "\n" + cellQuestions.get(i)
						    + " : " + cellString.get(i);
					    html += "<div title=\""
						    + cellQuestions.get(i)
						    + "\">\n";
					    html += cellString.get(i);
					    html += "\n</div>\n";
					}
				    } else {
					str += "\n no cell inputs";
				    }
				    html += "</div>\n";
				    b.putString("summary", summary);
				    b.putString("details", str);
				    b.putString("html", html);

				    Calendar cal = Calendar.getInstance();
				    String sdf = new SimpleDateFormat(
					    "yyyy-MM-dd-hh-mm-ss")
					    .format(new Date(System
						    .currentTimeMillis()))
					    + ".html";
				    String userString = mActivity.getUser().username;

				    String formName = formNames.get(position);
				    formName = formName.replaceAll(" ", "-");
				    formName = formName.replaceAll("/", "-");
				    b.putString("filename", formName + "_"
					    + userString + "_" + sdf);

				    if (!cellString.isEmpty()) {
					b.putStringArrayList("inputFromCell",
						cellString);
				    }
				    formId = null;
				    Fragment frag = Fragment.instantiate(
					    mActivity,
					    SummaryScreenFragment.class
						    .getName());
				    frag.setArguments(b);
				    mActivity.setContent(frag);
				}
			    });

			    viewGroupFooter.addView(button);
			    RelativeLayout.LayoutParams paramsFooter = (RelativeLayout.LayoutParams) button
				    .getLayoutParams();
			    paramsFooter
				    .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			    paramsFooter.addRule(RelativeLayout.ALIGN_BOTTOM);
			    paramsFooter.setMargins(20, 50, 10, 0);
			    paramsFooter
				    .addRule(RelativeLayout.OVER_SCROLL_NEVER);
			    button.setLayoutParams(paramsFooter);
			    exportButton = viewGroupFooter;// assign it to
							   // export button to
							   // be deleted
			    saveState.add(viewGroupFooter);
			    getListView().addFooterView(viewGroupFooter);
			}

		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing
		    }

		});

		return true;
	    }

	    return false;
	}
    }

}
