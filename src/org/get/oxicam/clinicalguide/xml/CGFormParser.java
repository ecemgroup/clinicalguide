package org.get.oxicam.clinicalguide.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.xml.forms.Form;
import org.get.oxicam.clinicalguide.xml.forms.FormCell;
import org.get.oxicam.clinicalguide.xml.forms.FormColumn;
import org.get.oxicam.clinicalguide.xml.forms.FormDuration;
import org.get.oxicam.clinicalguide.xml.stats.AbstractStatsQuestion;
import org.get.oxicam.clinicalguide.xml.stats.Stats;
import org.get.oxicam.clinicalguide.xml.stats.StatsConstraint;
import org.get.oxicam.clinicalguide.xml.stats.StatsQuestionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CGFormParser {

	private final Document mDom;
	private Context clinicalGuideContext;
	private String formName;
	
	public CGFormParser(Context context) {
		Document document = null;
		this.clinicalGuideContext = context;
		String filename = "clinicalguideforms.xml";
		
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
	 * Gets all the form tags inside the clinicalguideforms.xml
	 * 
	 * @return A NodeList containing all form nodes inside
	 *         clinicalguideforms.xml
	 */
	private NodeList getForms() {
		return mDom.getElementsByTagName("form");
	}

	/**
	 * Gets all the cell tags that has 'input' type. Returns and arraylist of
	 * FormCell. use formcell.name to get the name of the field and
	 * formcell.value to get the initial value.
	 * 
	 * @param id
	 *            ID of the form tag
	 * @return Returns an arraylist of FormCell which value and name can be used
	 *         for textfields.
	 */
	public ArrayList<FormCell> getInputTypeCells(String id) {
		ArrayList<FormCell> retVal = new ArrayList<FormCell>();
		Element e = mDom.getElementById(id);
		if (!e.getTagName().equalsIgnoreCase("form")) {
			Toast.makeText(clinicalGuideContext,
					"Error! This is not a 'form' tag", Toast.LENGTH_LONG)
					.show();
			return retVal;
		}
		ArrayList<FormCell> k = getCells(e);
		int len = k.size();
		for (int i = 0; i < len; i++) {
			FormCell fc = k.get(i);
			if (fc.type.equalsIgnoreCase("input")) {
				retVal.add(fc);
			}
		}
		return retVal;
	}

	/**
	 * Returns the corresponding startDate with the given end date depending on
	 * the duration of the form
	 * 
	 * @param id ID of the form tag
	 * @param endDate end date in milliseconds to compute the start date
	 * @return A long representing milliseconds of the start date.
	 */
	public long getStartDay(String id, long endDate) {
		long retVal = -1;
		Element e = mDom.getElementById(id);
		if (!e.getTagName().equalsIgnoreCase("form")) {
			Toast.makeText(clinicalGuideContext, "Error! This is not a 'form' tag", Toast.LENGTH_LONG).show();
			return retVal;
		}
		retVal = new DateHelper(getDuration(e)).getStartDay(endDate);
		return retVal;
	}

	/**
	 * Populates the form using a specific form id.
	 * 
	 * @param id The id of the form to be populated.
	 * @return A Form that can be passed into FormsQueryBuilder
	 */
	public Form populateForm(String id) {
		Element e = mDom.getElementById(id);
		Form retVal = null;
		if (!e.getTagName().equals("form")) {
			Toast.makeText(clinicalGuideContext, "Error! " + id + " is not an id of a <form> tag", Toast.LENGTH_LONG).show();
			return null;
		}

		formName = ParserHelper.requiredAttributeGetter(e, "name");

		if (formName != null) {
			retVal = new Form(formName, id, getDuration(e), getConstraint(e), getCells(e), getColumns(e), getStats(e));
		}
		return retVal;
	}

	/**
	 * Gets the "stats" tag from a specified node. The "stats" tag must be a
	 * direct child from the specific node.
	 * 
	 * @param e
	 *            The parent node where the "stats" tag resides.
	 * @return An Arraylist of FormStats containing all details needed for each
	 *         form.
	 */
	private ArrayList<Stats> getStats(Element e) {
		NodeList nl = e.getChildNodes(); // get only direct child <stats>
		ArrayList<Stats> retVal = new ArrayList<Stats>();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && ((Element) n).getTagName().equals("stats")) {
				ArrayList<AbstractStatsQuestion> arr = populateStats((Element) n);
				if (arr.size() > 0) {
					retVal.add(new Stats(arr, ParserHelper.requiredAttributeGetter((Element) n,"name")));
				}
			}
		}
		return retVal;
	}


	/**
	 * Gets all the constraint from a specific form node
	 * 
	 * @param e The specific form node to get the global constraint from.
	 * @return Returns an arraylist of FormConstraint representing the global constraints in the form.
	 */
	private ArrayList<StatsConstraint> getConstraint(Element e) {
		ArrayList<StatsConstraint> retVal = new ArrayList<StatsConstraint>();
		NodeList nl = e.getElementsByTagName("formconstraint");
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			retVal.add(ParserHelper.getStatsConstraintDetails((Element)nl.item(i)));
		}
		return retVal;
	}

	/**
	 * Gets the "column" from a specific form. A form can only have one "column" tag.
	 * 
	 * @param e The specific form node to get the column from.
	 * @return A FormColumn containing all the details needed.
	 */
	private FormColumn getColumns(Element e) {
		NodeList nl = e.getElementsByTagName("column");
		FormColumn retVal = null;

		int len = nl.getLength();
		if (len > 1) {
			Toast.makeText(clinicalGuideContext, "A <form> can only have one <column> child node", Toast.LENGTH_LONG).show();
		} else if (len == 1) {
			Element column = (Element) nl.item(0);
			retVal = new FormColumn(getColumnData(column), getColumnSave(column), getColumnStats(column));
		}
		return retVal;
	}

	private ArrayList<Stats> getColumnStats(Element e) {
		ArrayList<Stats> retVal = new ArrayList<Stats>();
		if (e.getTagName().equalsIgnoreCase("column")) {
			NodeList cl = e.getElementsByTagName("stats");
			NodeList nl = e.getElementsByTagName("save");
			if (nl.getLength() < 1 && cl.getLength() > 0) {
				Toast.makeText(clinicalGuideContext, "ERROR! Column Stats without Save tag", Toast.LENGTH_LONG).show();
			} else {
				return getStats(e);
			}
		}
		return retVal;
	}

	/**
	 * Populates all the stats details.
	 * 
	 * @param e The "stats" node.
	 * @return An arraylist of FormStatsQuestion.
	 */
	private ArrayList<AbstractStatsQuestion> populateStats(Element e) {
		NodeList nl = e.getElementsByTagName("question");
		ArrayList<AbstractStatsQuestion> retVal = new ArrayList<AbstractStatsQuestion>();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			Element question = (Element) nl.item(i);
			String qType = ParserHelper.requiredAttributeGetter(question, "type");
			if (qType != null) {
				retVal.add(StatsQuestionFactory.createQuestion(question, (ClinicalGuideActivity) clinicalGuideContext));
			}
		}
		return retVal;
	}

	/**
	 * Gets all the "data" tag inside column and store its details in a HashMap.
	 * 
	 * @param e The specific "column" node to get the "data" tag from.
	 * @return An ArrayList of Hashmaps. ArrayList represents each "data" tag.
	 *         The Hashmap key will contain all the name of the attributes and
	 *         the value will contain all the corresponding attribute value.
	 */
	private ArrayList<LinkedHashMap<String, String>> getColumnData(Element e) {
		ArrayList<LinkedHashMap<String, String>> retVal = new ArrayList<LinkedHashMap<String, String>>();
		NodeList nl = e.getElementsByTagName("data");
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String type = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "columnname");
			String tablename = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "tablename");
			if (type != null && tablename != null) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put("columnname", type);
				map.put("tablename", tablename);
				retVal.add(map);
			}
		}
		return retVal;
	}

	/**
	 * Gets all the "save" tag inside a specific column and returns the "type"
	 * attribute of each tag. 'save' node is a must if the column has stats. It is useless otherwise.
	 * 
	 * @param e The specific column node to get the "save" tag from.
	 * @return An ArrayList of HashMap containing 'columnname' and 'tablename' attribute of each "save" tag.
	 */
	private ArrayList<HashMap<String, String>> getColumnSave(Element e) {
		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();
		NodeList nl = e.getElementsByTagName("save");
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String columnname = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "columnname");
			String tablename = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "tablename");
			if (columnname != null && tablename != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("columnname", columnname);
				map.put("tablename", tablename);
				arr.add(map);
			}
		}
		return arr;
	}

	/**
	 * Gets all the "cell" tag in a specific form node and returns an ArrayList
	 * of string containing the "type" attribute from each "cell" tag.
	 * 
	 * @param e The 'form' node to get the cells from.
	 * @return ArrayList of FormCell which will contain the details of each Cell
	 */
	private ArrayList<FormCell> getCells(Element e) {
		NodeList nl = e.getElementsByTagName("cell");
		ArrayList<FormCell> retVal = new ArrayList<FormCell>();

		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			Element cell = ((Element) nl.item(i));

			String name = ParserHelper.requiredAttributeGetter(cell, "name").trim();
			String type = ParserHelper.requiredAttributeGetter(cell, "type").trim();
			if (name != null && type != null) {
				String value = getCellValue(cell, type);
				retVal.add(new FormCell(name, type, value));

			}
		}
		return retVal;
	}

	/**
	 * Gets the value attribute of a specified cell. Returns null if the value
	 * is mandatory and is not found
	 * 
	 * @param e The Cell Element(node)
	 * @param type The type attribute of the element.
	 * @return Returns the value attribute of the Cell
	 */
	private String getCellValue(Element e, String type) {
		String retVal = "";
		if (type.equalsIgnoreCase("query")) {
			retVal = ParserHelper.requiredAttributeGetter(e, "value");
		} else if (type.equalsIgnoreCase("defined")) {
			retVal = ParserHelper.requiredAttributeGetter(e, "value");
		} else if (type.equalsIgnoreCase("current_date")) {
			retVal = ParserHelper.requiredAttributeGetter(e, "value");
		} else if (type.equalsIgnoreCase("input")) {
			retVal = e.getAttribute("value");
		}
		return retVal;
	}

	/**
	 * Gets the duration in a specific form
	 * 
	 * @param e The form node to get the duration from.
	 * @return a FormDuration object that contains the details of the duration
	 */
	private FormDuration getDuration(Element e) {
		NodeList nodes = e.getElementsByTagName("duration");
		int len = nodes.getLength();
		String str = null;
		String detail = "";
		int type = -1;
		if (len != 1) {
			Toast.makeText(clinicalGuideContext, "Error! A form must have 1 <duration>\n" + "This form has " + len, Toast.LENGTH_LONG).show();
			return null;
		}

		Element durationNode = (Element) nodes.item(0);
		str = ParserHelper.requiredAttributeGetter(durationNode, "type");
		if (str.equalsIgnoreCase("year")) {
			type = FormDuration.DURATION_YEAR;
		} else if (str.equalsIgnoreCase("month")) {
			type = FormDuration.DURATION_MONTH;
		} else if (str.equalsIgnoreCase("week")) {
			detail = durationNode.getAttribute("detail");
			type = FormDuration.DURATION_WEEK;
		} else if (str.equalsIgnoreCase("defined")) {
			type = FormDuration.DURATION_DEFINED;
			detail = ParserHelper.requiredAttributeGetter(durationNode, "detail");    
		}
		return new FormDuration(type, detail);
	}

	/**
	 * Gets all form names and its corresponding id that is inside
	 * clinicalguideforms.xml
	 * 
	 * @return A HashMap<String,String> containing form IDs as the keys and form
	 *         names as the values.
	 */
	public HashMap<String, String> getFormNamesAndIds() {
		NodeList nl = getForms();
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
	 * Gets the form ids that is inside clinicalguideforms.xml
	 * 
	 * @return Arraylist of String containing the ids of each forms in the xml file.
	 */
	public ArrayList<String> getFormIds() {
		ArrayList<String> arr = new ArrayList<String>();
		NodeList nl = getForms();
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			String id = ParserHelper.requiredAttributeGetter((Element) nl.item(i), "id");
			if (id != null) {
				arr.add(id);
			}
		}
		return arr;
	}

	/**
	 * Gets the form names that is inside clinicalguideforms.xml
	 * 
	 * @return Arraylist of String containing the names of each forms in the xml file.
	 */
	public ArrayList<String> getFormNames() {
		ArrayList<String> arr = new ArrayList<String>();
		NodeList nl = getForms();
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
