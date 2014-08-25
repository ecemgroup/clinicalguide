package org.get.oxicam.clinicalguide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.R;
import org.get.oxicam.clinicalguide.xml.data.Answer;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.Question;
import org.get.oxicam.clinicalguide.xml.data.Questionnaire;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

public class AnswersReviewFragment extends ListFragment {

	public static final String ARG_KEY_QUESTIONNAIRES = "questionnaires";

	private ClinicalGuideActivity mActivity;
	private SimpleAdapter mAdapter;
	private Assessment mQuestionnaires;
	
	/**
	 * Initializes the ClassificationFragment.
	 */
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mActivity = (ClinicalGuideActivity)getActivity();
		mActivity.setTitle("Review Assessment");
        mQuestionnaires = (Assessment)getArguments().getSerializable(ARG_KEY_QUESTIONNAIRES);

		
		ViewGroup viewGroupFooter = new RelativeLayout(mActivity);
        Button button = new Button(mActivity);
        button.setText("Confirm");
        //button.setBackgroundDrawable(getResources().getDrawable(R.drawable.confirm));
    	button.setCompoundDrawablesWithIntrinsicBounds ( 0, 0, R.drawable.navigation_accept, 0);
    	
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putSerializable(ClassificationFragment.ARG_KEY_QUESTIONNAIRES, mQuestionnaires);
				Fragment frag = Fragment.instantiate(mActivity, ClassificationFragment.class.getName(), args);
				mActivity.setContent(frag);
			}
		});
        viewGroupFooter.addView(button);
        RelativeLayout.LayoutParams paramsFooter = (RelativeLayout.LayoutParams)button.getLayoutParams();
        paramsFooter.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsFooter.setMargins(0, 0, 10, 0);
        button.setLayoutParams(paramsFooter);
        getListView().addFooterView(viewGroupFooter);
        
        ArrayList<QuestionListItem> items = new ArrayList<QuestionListItem>();
        for (Questionnaire questionnaire: mQuestionnaires.questionnaires) {
        	for (Question question: questionnaire.questions) {
        		items.add(new QuestionListItem(R.drawable.ic_launcher, question));
        	}
        }  
        populateList(items);

	}
	
	private void populateList(ArrayList<QuestionListItem> items) {
		// populate a HashMap with all list items
		ArrayList<HashMap<String, Object>> itemData = new ArrayList<HashMap<String, Object>>();
		for(QuestionListItem item : items) {
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("question", item.question.label);
	        Answer answer = mQuestionnaires.answers.get(item.question.questionId);
	        String value = null;
	        if (item.question.answerType.equals("bool") | item.question.answerType.equals("boolswitch")) {
	        	if (answer.value.equals("true")) {
	        		value = "Yes";
	        	}
	        	else {
	        		value = "No";
	        	}
	        }
	        else {
	        	value = answer.value;
	        }
	        map.put("answer", value);

	        itemData.add(map);	
		}
    	
		// Specify source tag to corresponding target ID mapping
        String[] sourceTags = {
        		"question",
        		"answer"
        };
        int[] targetIds = {
        	R.id.question,
        	R.id.answer
        };
        
        // create the ListAdapter
        mAdapter = new SimpleAdapter(getActivity(), itemData, R.layout.question_answer_list_item, sourceTags, targetIds);
//        mAdapter.setViewBinder(new AnswersReviewBinder());
        setListAdapter(mAdapter);
	}

//	class AnswersReviewBinder implements ViewBinder {
//
//		@Override
//		public boolean setViewValue(View view, Object data, String textRepresentation) {
//			if (view.getId() == R.id.question) {
//				Question question = (Question)data;
//				TextView textView = (TextView)view;
//				textView.setText(question.label);
//			} 
//			else {
//				return false;
//			}
//			return true;
//		}
//	}
}
