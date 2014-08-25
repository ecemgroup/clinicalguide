package org.get.oxicam.clinicalguide.xml.data;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;

import android.graphics.drawable.Drawable;

public class Symptom {
	public final String symptomId;
	public final String name;
	public final ArrayList<Questionnaire> questionnaires;
	public final ArrayList<Option> options;
	public final ArrayList<Treatment> treatments;
	public final FollowUp followUp;
	public final Drawable icon;
	
	public Symptom(String symptomId, String name, ArrayList<Questionnaire> questionnaires, ArrayList<Option> options, ArrayList<Treatment> treatments, FollowUp followUp, String iconPath) {
		this.symptomId = symptomId;
		this.name = name;
		this.questionnaires = questionnaires;
		this.options = options;
		this.treatments = treatments;
		this.followUp = followUp;
		if(iconPath != null && !iconPath.isEmpty()) {
			this.icon = loadImage(iconPath);
		} else {
			this.icon = null;
		}
	}
	
	private Drawable loadImage(String relPath) {
		File p = ClinicalGuideActivity.getClinicalGuideDirectory();
		String sp = p.getAbsolutePath()  + relPath;
		Drawable d = Drawable.createFromPath(sp);
		
		if (d==null){ //alternatively, try the asset folder
			d=getDrawableFromAsset(relPath);
			
		}
		
		return d;
	}
	
	private Drawable getDrawableFromAsset(String strName) //TODO
    {
  //      AssetManager assetManager = getAssets();
        InputStream istr = null;
   //     try {
   //         istr = assetManager.open(strName);
   //     } catch (IOException e) {
   //         e.printStackTrace();
    //    }
        Drawable d = Drawable.createFromStream(istr, null);
        return d;
    }
}
