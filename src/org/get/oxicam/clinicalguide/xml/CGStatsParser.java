package org.get.oxicam.clinicalguide.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.stats.AbstractStatsQuestion;
import org.get.oxicam.clinicalguide.xml.stats.Stats;
import org.get.oxicam.clinicalguide.xml.stats.StatsQuestionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CGStatsParser {

	private final Document mDom;
	private Context clinicalGuideContext;
	private String statsName;

	public CGStatsParser(Context context) {
		Document document = null;
		this.clinicalGuideContext = context;
		
		String filename = "clinicalguidestats.xml";
		
		InputStream inputStream = XMLHandler.getDecryptedXMLInputStream(context,
				filename);
		if (inputStream != null) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				document = db.parse(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (document == null) {
			Log.e("Parser", "failed loading XML file");
		}
		mDom = document;
	}

	/**
	 * Gets all the stats tags inside the clinicalguidestats.xml
	 * 
	 * @return A NodeList containing all form nodes inside clinicalguidestas.xml
	 */
	private NodeList getStats() {
		return mDom.getElementsByTagName("stats");
	}

	/**
	 * Populates the stats using a specific form id.
	 * 
	 * @param id The id of the stats to be populated.
	 */
	public String populateStats(String id) {
		Element e = mDom.getElementById(id);
		String retVal = "";
		if (!e.getTagName().equals("stats")) {
			Toast.makeText(clinicalGuideContext,
					"Error! " + id + " is not an id of a <stats> tag",
					Toast.LENGTH_SHORT).show();
			return retVal;
		}

		statsName = ParserHelper.requiredAttributeGetter(e, "name");

		if (statsName != null) {
			Stats stats = new Stats(getQuestions(e), e.getAttribute("name"));
			StatsGenerator sqb = new StatsGenerator(clinicalGuideContext, stats);
			retVal = sqb.getStatsString();
		}

		return retVal;
	}

	private ArrayList<AbstractStatsQuestion> getQuestions(Element e) {
		ArrayList<AbstractStatsQuestion> retVal = new ArrayList<AbstractStatsQuestion>();
		NodeList nl = e.getElementsByTagName("question");
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			retVal.add(StatsQuestionFactory.createQuestion((Element) nl.item(i), (ClinicalGuideActivity)clinicalGuideContext));
		}
		return retVal;
	}

	/**
	 * Gets all stats names and its corresponding id that is inside
	 * clinicalguidestats.xml
	 * 
	 * @return A HashMap<String,String> containing stats IDs as the keys and form
	 *         names as the values.
	 */
	public HashMap<String, String> getStatsNamesAndIds() {
		NodeList nl = getStats();
		HashMap<String, String> id_name = new LinkedHashMap<String, String>();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String id = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "id");
			String name = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "name");
			if (id != null && name != null) {
				id_name.put(id, name);
			}
		}
		return id_name;
	}

	/**
	 * Gets the stats ids that is inside clinicalguidestats.xml
	 * 
	 * @return Arraylist of String containing the ids of each stats in the clinicalguidestats.xml
	 *         file.
	 */
	public ArrayList<String> getStatsIds() {
		ArrayList<String> arr = new ArrayList<String>();
		NodeList nl = getStats();
		int len = nl.getLength();
		String str = "";
		for (int i = 0; i < len; i++) {
			String id = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "id");
			if (id != null) {
				arr.add(id);
				str += (id + "\n");
			}
		}
		return arr;
	}

	/**
	 * Gets the stats names that is inside clinicalguidestats.xml
	 * 
	 * @return Arraylist of String containing the names of each stats in the xml
	 *         file.
	 */
	public ArrayList<String> getStatsNames() {
		ArrayList<String> arr = new ArrayList<String>();
		NodeList nl = getStats();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String name = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "name");
			if (name != null) {
				arr.add(name);
			}
		}
		return arr;
	}

}
