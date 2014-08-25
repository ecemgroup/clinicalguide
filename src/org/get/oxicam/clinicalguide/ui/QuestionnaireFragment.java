package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ecemgroup.sharevitalsigns.library.ShareVitalSigns;
import org.ecemgroup.sharevitalsigns.library.ShareVitalSigns.ShareVitalSignsResultReceiver;
import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.data.Answer;
import org.get.oxicam.clinicalguide.xml.data.AnswerValidator;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.Question;
import org.get.oxicam.clinicalguide.xml.data.Questionnaire;
import org.get.oxicam.clinicalguide.xml.data.Symptom;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Switch;
import android.widget.TextView;

public class QuestionnaireFragment extends ListFragment implements
		ListItemOnClickListener {

	public static final String ARG_KEY_QUESTIONNAIRES = "questionnaires";

	private static final int ID_RADIO_YES = 1;
	private static final int ID_RADIO_NO = 2;
	private static final int ID_QUANTITY = 3;
	private static final int ID_SWITCH = 4;

	private ClinicalGuideActivity mActivity;
	private SimpleAdapter mAdapter;
	private Assessment mQuestionnaires;
	private Questionnaire mQuestionnaire;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQuestionnaires = (Assessment) getArguments().getSerializable(
				ARG_KEY_QUESTIONNAIRES);
		mQuestionnaire = mQuestionnaires.getCurrentQuestionnaire();

		Log.w("quest", "index: " + mQuestionnaires.currentIndex);

		mActivity = (ClinicalGuideActivity) getActivity();
		mActivity.setTitle(mQuestionnaire.title);

		ArrayList<QuestionListItem> items = new ArrayList<QuestionListItem>();
		for (Question question : mQuestionnaire.questions) {
			items.add(new QuestionListItem(R.drawable.ic_launcher, question));
		}

		getListView().setAdapter(null);

		ViewGroup viewGroupHeader = new RelativeLayout(mActivity);
		TextView textView = new TextView(mActivity);
		textView.setText(mQuestionnaire.type.toUpperCase());
		textView.setTextColor(Color.argb(255, 47, 178, 229));
		textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
		textView.setTextSize(17f);
		textView.setShadowLayer(0f, 0f, 1f, Color.DKGRAY);
		viewGroupHeader.addView(textView);
		RelativeLayout.LayoutParams paramsHeader = (RelativeLayout.LayoutParams) textView
				.getLayoutParams();
		paramsHeader.addRule(RelativeLayout.CENTER_HORIZONTAL);
		paramsHeader.setMargins(10, 0, 0, 0);
		textView.setLayoutParams(paramsHeader);
		getListView().addHeaderView(viewGroupHeader);

		ViewGroup viewGroupFooter = new RelativeLayout(mActivity);
		/************* Next button *****************/
		Button button = new Button(mActivity);
		button.setText("Next");
		// button.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_right));
		button.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.navigation_forward, 0);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mQuestionnaires.answers.putAll(getAnswers());
				if (mQuestionnaires.hasNextQuestionnaire()) {
					Assessment next = mQuestionnaires.nextQuestionnaire();
					Bundle args = new Bundle();
					args.putSerializable(ARG_KEY_QUESTIONNAIRES, next);
					Fragment frag = Fragment.instantiate(mActivity,
							QuestionnaireFragment.class.getName(), args);
					mActivity.setContent(frag);

				} else {
					Symptom mainSymptom = mQuestionnaires
							.getMainSymptom(mActivity);
					Bundle args = new Bundle();
					args.putSerializable(
							AnswersReviewFragment.ARG_KEY_QUESTIONNAIRES,
							mQuestionnaires);
					// args.putSerializable(ClassificationFragment.ARG_KEY_QUESTIONNAIRES,
					// mQuestionnaires);
					// Treatment recommendedTreatment =
					// AnswerValidator.getTreatment(mainSymptom,
					// mActivity.getAnswers(), mActivity.getXmlParser());
					mQuestionnaires.recommendedTreatment = AnswerValidator
							.getTreatment(mActivity, mainSymptom,
									mQuestionnaires.answers,
									mActivity.getXmlParser(),
									mQuestionnaires.patient.getPatientID());

					// mActivity.setRecommendedTreatment(recommendedTreatment);
					Fragment frag = Fragment.instantiate(mActivity,
							AnswersReviewFragment.class.getName(), args);
					mActivity.setContent(frag);
				}
			}
		});
		viewGroupFooter.addView(button);
		RelativeLayout.LayoutParams paramsFooter = (RelativeLayout.LayoutParams) button
				.getLayoutParams();
		paramsFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		paramsFooter.setMargins(0, 0, 10, 0);
		button.setLayoutParams(paramsFooter);

		/************* Previous button *****************/
		if (!mQuestionnaires.isFirstQuestionnaire()) {
			Button preButton = new Button(mActivity);
			preButton.setText("Previous");
			// button.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_right));
			preButton.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.navigation_backward, 0, 0, 0);
			preButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mQuestionnaires.answers.putAll(getAnswers());
					mActivity.onBackPressed();
				}
			});
			viewGroupFooter.addView(preButton);
			paramsFooter = (RelativeLayout.LayoutParams) preButton
					.getLayoutParams();
			paramsFooter.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			paramsFooter.setMargins(10, 0, 0, 0);
			preButton.setLayoutParams(paramsFooter);
		}

		getListView().addFooterView(viewGroupFooter);
		populateList(items);
	}

	/**
	 * Called if a item in the ListView is clicked.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// apparently this doesn't work if there are buttons in the list item
		// layout
	}

	/**
	 * Called if a item in the ListView is clicked.
	 */
	@Override
	public void onListItemClick(View v) {
		if (v.getId() == R.id.itemContainer) {
			expandInfoArea(v);

		} else {
			checkQuestionValueChanged(v);
		}
	}

	private void checkQuestionValueChanged(View v) {
		ListView lv = getListView();

		// check if radio buttons have changed
		int firstPos = lv.getFirstVisiblePosition();
		int lastPos = lv.getLastVisiblePosition();

		// iterate over views in the list and check if values have changed
		for (int i = firstPos, j = 0; i <= lastPos; i++, j++) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> item = (HashMap<String, Object>) lv
					.getItemAtPosition(i);

			// for some reason indices of visible views count from 0 (instead of
			// firstPos)
			View child = lv.getChildAt(j);

			if (child != null) {
				// yes no option
				View rb = child.findViewById(ID_RADIO_YES);
				if (rb != null) {
					// compare model selection state with view selection state
					boolean selected = ((Integer) item.get("value")) != 0 ? true
							: false;
					boolean vSelected = ((RadioButton) rb).isChecked();
					if (vSelected != selected) {
						// states are different, button was clicked
						Log.d("lv", "value: " + vSelected);
						item.put("value", vSelected ? 1 : 0);
					}
				}
				View sw = child.findViewById(ID_SWITCH);
				if (sw != null) {
					// compare model selection state with view selection state
					boolean selected = ((Integer) item.get("value")) != 0 ? true
							: false;
					boolean vSelected = ((Switch) sw).isChecked();
					if (vSelected != selected) {
						// states are different, button was clicked
						Log.d("lv", "value: " + vSelected);
						item.put("value", vSelected ? 1 : 0);
					}
				}
				// quantity option
				View nt = child.findViewById(ID_QUANTITY);
				if (nt != null) {
					int val = (Integer) item.get("value");
					int ntVal = ((NumberTicker) nt).getValue();
					if (val != ntVal) {
						// value has changed
						Log.d("lv", "value: " + ntVal);
						item.put("value", ntVal);
					}
				}
			}
		}
	}

	private HashMap<String, Answer> getAnswers() {
		ListView lv = getListView();
		HashMap<String, Answer> answers = new HashMap<String, Answer>();
		// check if radio buttons have changed
		// int firstPos = lv.getFirstVisiblePosition();
		// int lastPos = lv.getLastVisiblePosition();

		for (int i = 0; i < lv.getCount(); i++) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> item = (HashMap<String, Object>) lv
					.getItemAtPosition(i);
			if (item != null) {
				Question question = (Question) item.get("question");
				if (question.answerType.equals("bool")
						| question.answerType.equals("boolswitch")) {
					String selected = ((Integer) item.get("value")) != 0 ? "true"
							: "false";
					Answer answer = new Answer(question, selected);
					answers.put(answer.question.questionId, answer);
				} else {
					String value = item.get("value").toString();
					Answer answer = new Answer(question, value);
					answers.put(answer.question.questionId, answer);
				}
			}
		}
		return answers;
	}

	/*
	 * Called if an info was clicked. Expands / hides the info area.
	 */
	private void expandInfoArea(View v) {
		// find the item view that contains this info button
		View itemContainer = v;
		if (v.getId() != R.id.itemContainer) {
			ViewParent parent = v.getParent();
			while (parent != null && parent instanceof View) {
				if (((View) parent).getId() == R.id.itemContainer) {
					itemContainer = (View) parent;
					break;
				} else {
					parent = parent.getParent();
				}
			}
		}

		if (itemContainer != null) {
			// we found the parent container of this list item
			View infoView = itemContainer.findViewById(R.id.infoView);
			View fg = itemContainer.findViewById(R.id.foregroundContainer);
			ImageView iv = (ImageView) itemContainer
					.findViewById(R.id.infoImage);
			TextView infoTxt = (TextView) itemContainer
					.findViewById(R.id.infoText);
			if (infoView != null
					&& fg != null
					&& (infoTxt.getText().length() > 0 || iv.getDrawable() != null)) {
				boolean visible = false;
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infoView
						.getLayoutParams();
				if (params.topMargin == 0) {
					visible = true;
					params.topMargin = fg.getHeight();
				} else {
					params.topMargin = 0;
				}
				infoView.setLayoutParams(params);
				infoView.setVisibility(visible ? View.VISIBLE : View.GONE);
				infoView.invalidate();

				final View item = itemContainer;
				infoView.post(new Runnable() {
					public void run() {
						try {
							Rect rect = new Rect(0, 0, item.getWidth(), item
									.getHeight());
							getListView().requestChildRectangleOnScreen(item,
									rect, false);
						} catch (Exception e) {
							Log.w("ListView", "warning: " + e.getMessage());
						}
					}
				});
			}
		}
	}

	private void populateList(ArrayList<QuestionListItem> items) {
		// populate a HashMap with all list items
		ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
		ArrayList<Answer> answers = new ArrayList<Answer>();
		for (QuestionListItem item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("question", item.question);
			map.put("answerType", item.question.answerType);
			Answer ans = mQuestionnaires.answers.get(item.question.questionId);
			answers.add(ans);
			if (ans != null) {
				if (ans.value.equals("true")) {
					map.put("value", 1);
				} else if (ans.value.equals("false")) {
					map.put("value", 0);
				} else {
					map.put("value", Integer.parseInt(ans.value));
				}
			} else {
				map.put("value", 0);
			}
			map.put("infoButton", item.question.annotation);
			if (item.question.annotation != null) {
				map.put("infoText", item.question.annotation.label);
				map.put("infoImg", item.question.annotation.getImage());
			}
			if (item.question.info != null) {
				map.put("infoType", item.question.info.type);
				map.put("infoLabel", item.question.info.label);
			}
			if (item.question.min != null) {
				map.put("min", item.question.min);

			}
			if (item.question.link != null) {
				map.put("link", item.question.link);

			}
			itemData.add(map);
		}

		// Specify source tag to corresponding target ID mapping
		String[] sourceTags = { "question", "answerType", "infoText",
				"infoImg", "infoButton", "infoType", "infoLabel" };
		int[] targetIds = { R.id.question, R.id.answerContainer, R.id.infoText,
				R.id.infoImage, R.id.infoButton, R.id.infoBackground, R.id.info };

		// create the ListAdapter
		mAdapter = new SimpleAdapter(getActivity(), itemData,
				R.layout.question_list_item, sourceTags, targetIds);
		mAdapter.setViewBinder(new GeneralDangerSignBinder(answers));
		setListAdapter(mAdapter);
	}

	public Questionnaire getQuestionnaire() {
		return mQuestionnaire;
	}

	public void setQuestionnaire(Questionnaire mQuestionnaire) {
		this.mQuestionnaire = mQuestionnaire;
	}

	class GeneralDangerSignBinder implements ViewBinder, View.OnClickListener,
			NumberTickerValueChangeListener, OnCheckedChangeListener {

		private ArrayList<Answer> answer = new ArrayList<Answer>();
		private Question ques;

		public GeneralDangerSignBinder(ArrayList<Answer> answers) {
			this.answer = answers;
			for (Answer a : answer) {
				if (a != null) {
					Log.w("answer", "back: " + a.question.label + " " + a.value);
				}
			}
		}

		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view.getId() == R.id.question) {
				Question question = (Question) data;
				ques = question;
				TextView textView = (TextView) view;
				textView.setText(question.label);

			} else if (view.getId() == R.id.infoText) {
				TextView infoTxt = (TextView) view;
				if (data != null) {
					infoTxt.setText(data.toString());
				} else {
					infoTxt.setVisibility(View.GONE);
				}

			} else if (view.getId() == R.id.infoImage) {
				ImageView infoImg = (ImageView) view;
				if (data != null) {
					Drawable img = (Drawable) data;
					infoImg.setImageDrawable(img);
				} else {
					infoImg.setVisibility(View.GONE);
				}

			} else if (view.getId() == R.id.answerContainer) {
				FrameLayout fl = (FrameLayout) view;
				setAnswerView(fl, (String) data);
			} else if (view.getId() == R.id.infoButton) {
				if (data == null) {
					view.setVisibility(View.INVISIBLE);
				}
			} else if (view.getId() == R.id.infoBackground) {
				if (data != null) {
					if (data.equals("info")) {
						view.setBackgroundResource(R.drawable.rounded_edges_info);
					} else if (data.equals("urgent")) {
						view.setBackgroundResource(R.drawable.rounded_edges_urgent);
					}
				} else {
					view.setVisibility(View.GONE);
				}
			} else if (view.getId() == R.id.info) {
				if (data != null) {
					((TextView) view).setText((String) data);
				}
			} else {
				return false;
			}
			return true;
		}

		private void setAnswerView(FrameLayout container, String answerType) {
			if (container.getChildCount() > 0) {
				return;
			}
			String value = "";

			for (Answer a : answer) {
				if (a != null) {
					if (a.question.label.equalsIgnoreCase(ques.label)) {
						value = a.value;
					}
				}
			}

			if (answerType.equals("quantity")) {
				NumberTicker nt = new NumberTicker(mActivity);
				nt.setId(ID_QUANTITY);
				nt.setValueChangeListener(this);
				if (!value.equals("")) {
					nt.setValue(Integer.parseInt(value));
				}
				container.addView(nt);

			} else if (answerType.equals("bool")) {
				// bool answer
				RadioGroup rg = new RadioGroup(mActivity);
				container.addView(rg);

				RadioButton rbYes = new RadioButton(mActivity);
				rbYes.setId(ID_RADIO_YES);
				rbYes.setText("Yes ");
				rbYes.setOnClickListener(this);
				rg.addView(rbYes);

				RadioButton rbNo = new RadioButton(mActivity);
				rbNo.setId(ID_RADIO_NO);
				rbNo.setText("No ");
				rbNo.setOnClickListener(this);
				if (value.equals("true")) {
					rg.check(ID_RADIO_YES);
				} else {
					rbNo.setChecked(true);
				}
				// rbNo.setChecked(true); //no button marked by default
				rg.addView(rbNo);

			} else if (answerType.equals("boolswitch")) {
				// switch answer //TODO: set format of switch
				Switch sw = new Switch(mActivity);
				sw.setTextOff("No");
				sw.setTextOn("Yes");
				sw.setId(ID_SWITCH);
				sw.setOnCheckedChangeListener(this);
				if (value.equals("true")) {
					sw.setChecked(true);
				}
				container.addView(sw);

			} else if (answerType.equals("measure")) {
				RelativeLayout layout = new RelativeLayout(mActivity);
				container.addView(layout);

				NumberTicker nt = new NumberTicker(mActivity);
				nt.setId(ID_QUANTITY);
				nt.setValueChangeListener(this);
				layout.addView(nt);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nt
						.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

				// button for getting external values
				Button button = new Button(mActivity);
				button.setText("\uE715");
				// button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.content_new_attachment,0,0,0);
				Typeface font = Typeface.createFromAsset(mActivity.getAssets(),
						"fontello.ttf");
				button.setTypeface(font);
				layout.addView(button);
				params = (RelativeLayout.LayoutParams) button.getLayoutParams();
				params.addRule(RelativeLayout.RIGHT_OF, ID_QUANTITY);
				params.addRule(RelativeLayout.CENTER_VERTICAL);

				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
											
						try {    		
							getActivity().startActivityForResult(ClinicalGuideActivity.sharevitals.measureVitalSigns(new ShareVitalSignsResultReceiver() {
				    			@Override
				    			public void onResult(float[] vital, int[] confidence) {
				    			
				    				NumberTicker nt = (NumberTicker) mActivity
											.findViewById(ID_QUANTITY);
									nt.setValue((int) vital[0]);
				    			}
				    		},ShareVitalSigns.MEASURE_RR),ShareVitalSigns.MEASURE_RR); //TODO choose proper signal
				    	} catch (Exception e) {
				    		/*Toast.makeText(
				    				this,
				    				String.format("No App providing requested vital sign found on device."),
				    				Toast.LENGTH_SHORT).show();*/
				    	}
						
						
					}
				});
			}
		}

		@Override
		public void onClick(View v) {
			onListItemClick(v);
		}

		@Override
		public void valueChanged(NumberTicker view, int newVal) {
			onListItemClick(view);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			onListItemClick(buttonView);
		}
	}
}
