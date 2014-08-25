package org.get.oxicam.clinicalguide.ui;

import org.get.oxicam.clinicalguide.R;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class NumberTicker extends FrameLayout implements TextWatcher {

	private EditText mNumText;
	
	private int mMin = 0;
	private int mMax = 100;
	
	private NumberTickerValueChangeListener changeListener;
	
	public NumberTicker(Context context) {
		super(context);
		setLayout();
	}
	
	public NumberTicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayout();
	}
	
	public void setValueChangeListener(NumberTickerValueChangeListener l) {
		changeListener = l;
	}

	private void setLayout() {
		inflate(getContext(), R.layout.number_ticker, this);
		
		mNumText = (EditText)findViewById(R.id.numText);
		mNumText.setInputType(InputType.TYPE_CLASS_NUMBER);
		mNumText.addTextChangedListener(this);
		mNumText.setTextIsSelectable(true);//hide keyboard
		//mNumText.requestFocus();
	
		
		findViewById(R.id.numUp).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setValue(getValue() + 1);
			}
		});
		
		findViewById(R.id.numDown).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setValue(getValue() - 1);
			}
		});
	}
	
	private void fireValueChanged() {
		if(changeListener != null) {
			changeListener.valueChanged(this, getValue());
		}
	}
	
	public void setMinValue(int min) {
		mMin = min;
		// apply new min value if neccessary
		setValue(getValue());
	}
	
	public void setMaxValue(int max) {
		mMax = max;
		// apply new max value if neccessary
		setValue(getValue());
	}
	
	public int getValue() {
		int num = 0;
		try {
			num = Integer.parseInt(mNumText.getEditableText().toString());
		} catch(NumberFormatException e) {
			// don't care
		}
		return num;
	}
	
	public void setValue(int val) {
		if(val > mMax) {
			val = mMax;
		} else if(val < mMin) {
			val = mMin;
		}
		mNumText.setText("" + val);
		fireValueChanged();
	}

	@Override
	public void afterTextChanged(Editable e) {
		fireValueChanged();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// not needed
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// not needed
	}
}
