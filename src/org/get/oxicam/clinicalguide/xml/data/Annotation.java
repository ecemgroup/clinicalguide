package org.get.oxicam.clinicalguide.xml.data;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;

import android.graphics.drawable.Drawable;

public class Annotation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String label;
	public final String imgPath;
	
	public Annotation(String label, String imgPath) {
		this.label = label;
		this.imgPath = imgPath;
	}
	
	public Drawable getImage() {
		Drawable img = null;
		if(imgPath != null && !imgPath.isEmpty()) {
			img = loadImage(imgPath);
		}
		return img;
	}
	
	private Drawable loadImage(String relPath) {
		File p = ClinicalGuideActivity.getClinicalGuideDirectory();
		String sp = p.getAbsolutePath()  + relPath;
		Drawable d = Drawable.createFromPath(sp);
		
		//look alternatively in assets
		if (d==null){
		//	d=getDrawableFromAsset(relPath);
			
		}
		
		
		return d;
	}
	
	private Drawable getDrawableFromAsset(String strName) //TODO
    {
  //      AssetManager assetManager = getActivity().getAssets();
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
