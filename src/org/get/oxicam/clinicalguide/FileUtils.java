package org.get.oxicam.clinicalguide;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

public class FileUtils {

    private static final String LOG_TAG = "FileUtils";

    /**
     * Returns the base directory for recorded videos, log files, etc.
     */
    public static File getAppDirectory() {
	File f = new File(Environment.getExternalStorageDirectory()
		.getAbsolutePath() + "/ClinicalGuide");
	// create directory if it not exists
	f.mkdirs();
	return f;
    }

    /**
     * Returns a filename containing the current date. The file is placed in the
     * directory returned by getAppDirectory(). The filename is customized by
     * adding the specified prefix and extension. For example calling this
     * method with prefix "vid" and extension ".mp4" will result in a path
     * something like "/mnt/sdcard/ClinicalGuide/vid_20120603-174523.mp4".
     * 
     * @param prefix
     *            Filename prefix.
     * @param extension
     *            Filename extension.
     * @return A file containing the current date.
     */
    public static File getDateFilename(String prefix, String extension) {
	File dir = getAppDirectory();
	GregorianCalendar cal = new GregorianCalendar();
	String f = String.format("%s/%s_%d%02d%02d-%02d%02d%02d%s",
		dir.getAbsolutePath(), prefix, cal.get(Calendar.YEAR),
		cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
		cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
		cal.get(Calendar.SECOND), extension);
	return new File(f);
    }

    /**
     * Runs the MediaScanner service on the specified file. This is needed to
     * see the file on an attached computer.
     */
    public static void updateFileForMtp(File file, Context context) {
	if (file != null && context != null) {
	    String path = file.getAbsolutePath();
	    Log.d(LOG_TAG, "running MediaScanner on: " + path);
	    String[] paths = { path };
	    MediaScannerConnection.scanFile(context, paths, null, null);
	}
    }

    public static boolean writeFile(String data, String fileName) {
	try {
	    Log.w("FileWriting", getAppDirectory().getPath());
	    File output = new File(getAppDirectory().getPath() + "/" + fileName);
	    if (!output.exists()) {
		output.createNewFile();
	    }

	    BufferedWriter bw = new BufferedWriter(new FileWriter(output));
	    bw.append(data);
	    bw.close();
	} catch (FileNotFoundException fnfe) {
	    return false;
	} catch (IOException ioe) {
	    return false;
	}
	return true;
    }
}
