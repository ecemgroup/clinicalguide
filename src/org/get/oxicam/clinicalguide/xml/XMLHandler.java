package org.get.oxicam.clinicalguide.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.encryption.Encryption;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class XMLHandler {

	public static InputStream getXMLInputStream(Context context, String filename) {
		InputStream inputStream = null;
		// check storage state
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// storage is mounted, check for xml file
			File xml = new File(ClinicalGuideActivity
					.getClinicalGuideDirectory().getAbsolutePath(), filename);
			try {
				inputStream = new FileInputStream(xml);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (inputStream == null) {
			// loading file from sd-card failed, used asset version as fall back
			AssetManager am = context.getAssets();
			try {
				inputStream = am.open(filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			CGParser.setLocationXML(true);
		}
			

		return inputStream;
	}
	/**
	 * Gets the decrypted file based on the path of the file.
	 * @param path The absolute path of the file.
	 * @param context The context.
	 * @return An array of decrypted bytes.
	 */
	private static byte[] getFile(String path)
	{
		byte[] file = null;
		Encryption dec = new Encryption(path);
		file = dec.decryptFile();
		return file;
	}
	/**
	 * Gets the decrypted file as an array of bytes from 
	 * an array of encrypted bytes.
	 * @param file The file to decrypt.
	 * @return The decrypted file.
	 */
	private static byte[] getFile(byte[] file)
	{
		Encryption dec = new Encryption(null);
		file = dec.decryptFile(file);
		return file;
	}
	/**
	 * Gets the input stream for the XML file, assuming that both the files
	 * on the SD card and in the assets folder are encrypted.
	 * @param context The context.
	 * @param filename The name of the file.
	 * @return The input stream to the decrypted XML file.
	 */
	public static InputStream getDecryptedXMLInputStream(Context context, String filename) 
	{
		InputStream inputStream = null;
		byte[] file = null;
		// check storage state
		String state = Environment.getExternalStorageState();
		// storage is mounted, check for xml file
		if (Environment.MEDIA_MOUNTED.equals(state)) 
		{
			File xml = new File(ClinicalGuideActivity.getClinicalGuideDirectory().getAbsolutePath(), filename);
			if(xml.exists())
			{
				file = getFile(xml.getAbsolutePath());
				if(file != null)
				{
					inputStream = new ByteArrayInputStream(file);
				}
				else
					inputStream = null;
			}
				
		}
		// loading file from sd-card failed, used asset version as fall back
		if (inputStream == null) 
		{
			AssetManager am = context.getAssets();
			try 
			{
				inputStream = am.open(filename);
				file = new byte[inputStream.available()];
				inputStream.read(file);
				inputStream = new ByteArrayInputStream(getFile(file));
			} 
			catch (IOException e) { e.printStackTrace(); }
		}
		return inputStream;
	}
}
