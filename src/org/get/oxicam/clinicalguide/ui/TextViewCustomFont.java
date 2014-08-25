package org.get.oxicam.clinicalguide.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TextViewCustomFont extends TextView {
    private static final String TAG = "TextView";

    public TextViewCustomFont(Context context) {
        super(context);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, "fontello.ttf");
    }

    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, "fontello.ttf");
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
        	tf = Typeface.createFromAsset(ctx.getAssets(), asset);  
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        setText(">");
        return true;
    }

}
