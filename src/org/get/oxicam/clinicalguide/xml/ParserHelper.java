package org.get.oxicam.clinicalguide.xml;

import java.util.ArrayList;
import java.util.HashSet;

import org.get.oxicam.clinicalguide.xml.stats.StatsColumnCompare;
import org.get.oxicam.clinicalguide.xml.stats.StatsComparatorOperator;
import org.get.oxicam.clinicalguide.xml.stats.StatsCompareConstraint;
import org.get.oxicam.clinicalguide.xml.stats.StatsConstraint;
import org.get.oxicam.clinicalguide.xml.stats.StatsSubject;
import org.get.oxicam.clinicalguide.xml.stats.StatsTimespan;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserHelper {

    /**
     * Gets an attribute from s specified element. If the attribute is not found
     * or the attribute has an empty value, an exception will be thrown.
     * 
     * @param e The element to get the attribute from.
     * @param attr Attribute name to get from the element.
     * @return The attribute value or null if it is not found or no value is defined.
     */
    public static String requiredAttributeGetter(Element e, String attr) {
	String retVal = null;
	retVal = e.getAttribute(attr);

	if (retVal.isEmpty()) {
	    String tagName = e.getTagName();
	    String str = "ERROR! <" + tagName + "> has no attribute [" + attr
		    + "]";
	    throw new IllegalArgumentException(str);
	}

	return retVal;
    }

    /**
     * Gets details from an element that acts like a subject.
     * The element must have attributes "columnname" and "tablename"
     * @param e The Element that has "columnname" and "tablename" attribute.
     * @return StatsQuestionSubject object with the columnname and tablename.
     */
    public static StatsSubject getStatsSubjectDetails(Element e) {
	String columnname = requiredAttributeGetter(e, "columnname");
	String tablename = requiredAttributeGetter(e, "tablename");
	return new StatsSubject(tablename, columnname);
    }

    /**
     * Gets details from an element that acts like a subject.
     * The element must have attributes "columnname", "tablename", "data" and an optional "operator"
     * @param e The Element that has "columnname", "tablename", "data" and an optional "operator" attributes.
     * @return FormConstraint object with the columnname, tablename, data and operator.
     */
    public static StatsConstraint getStatsConstraintDetails(Element e) {
	String columnname = requiredAttributeGetter(e, "columnname");
	String tablename = requiredAttributeGetter(e, "tablename");
	String data = requiredAttributeGetter(e, "data");
	String operator = e.getAttribute("operator");
	if(operator.isEmpty()){
	    operator = "equal";
	}
	return new StatsConstraint(tablename, columnname, data, new StatsComparatorOperator(operator));
    }
    
    /**
     * Gets timespan details.
     * "type" attribute is mandatory. "group" and "adjust" are optional,
     * @param e The timespan element
     * @return Timespan object
     */
    public static StatsTimespan getTimespanDetails(Element e) {
	StatsTimespan retVal = null;
	String type = requiredAttributeGetter(e, "type");
	String group = "";
	String total = "";
	String adjust = "";
	int adjustInt = 0;
	if (!type.isEmpty()) {
	    group = e.getAttribute("group");
	    adjust = e.getAttribute("adjust");
	    if (!adjust.isEmpty()) {
		adjustInt = Integer.parseInt(adjust);
	    }
	    retVal = new StatsTimespan(type, group, adjustInt);
	}
	return retVal;
    }

    /**
     * Checks if the arraylist contains more than one distinct table.
     * 
     * @param constraints
     * @return True if you will need more than one table (needs joining). False otherwise.
     */
    public static boolean moreThanOneTable(
	    ArrayList<? extends StatsSubject> constraints) {
	HashSet<String> set = new HashSet<String>();
	int len = constraints.size();
	for (int i = 0; i < len; i++) {
	    StatsSubject fc = constraints.get(i);
	    if (set.add(fc.tablename) && set.size() > 1) {
		return true;
	    }
	}
	return false;

    }

    /**
     * Gets compare constraint.
     * Element should have childnodes"lefthandside", "righthandside", and "comparator".
     * @param e The element that has childnodes"lefthandside", "righthandside", and "comparator"
     * @return CompareConstraint object.
     */
    public static StatsCompareConstraint getCompareConstraintDetails(Element e) {
	StatsCompareConstraint retVal = null;
	NodeList lhsList = e.getElementsByTagName("lefthandside");
	NodeList rhsList = e.getElementsByTagName("righthandside");
	NodeList signList = e.getElementsByTagName("comparator");
	int lhsLen = lhsList.getLength();
	int rhsLen = rhsList.getLength();
	int signLen = signList.getLength();

	if (lhsLen != 1 || rhsLen != 1 || signLen != 1) {
	    throw new IllegalArgumentException(
		    "compareconstraint should have one lefthandside, righthandside and comparator child");
	}

	Element signNode = (Element) signList.item(0);
	Element lhsNode = (Element) lhsList.item(0);
	Element rhsNode = (Element) rhsList.item(0);

	int lhsChildLen = lhsNode.getChildNodes().getLength();
	int rhsChildLen = rhsNode.getChildNodes().getLength();
	int signChildLen = signNode.getChildNodes().getLength();
	
	if (lhsChildLen != rhsChildLen && rhsChildLen != signChildLen) {
	    throw new IllegalArgumentException(
		    "left hand side, right hand side, and comparator should have the same number of child");
	}

	retVal = new StatsCompareConstraint(getCompareSideDetails(lhsNode),
		getCompareSideDetails(rhsNode), getComparator(signNode));

	return retVal;
    }

    /**
     * Gets the details for either lefthandside or righthandside of compareconstraint/
     * @param e The "lefthandside" or "righthandside" element that has a childnode "columncompare"
     * @return arraylist of column compare that has been generated depending on the details of the element passed in
     */
    private static ArrayList<StatsColumnCompare> getCompareSideDetails(
	    Element e) {
	ArrayList<StatsColumnCompare> retVal = new ArrayList<StatsColumnCompare>();
	NodeList nl = e.getChildNodes();
	int len = nl.getLength();
	for (int i = 0; i < len; i++) {
	    Node node = nl.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {

		Element child = (Element) node;
		if (child.getTagName().equalsIgnoreCase("columncompare")) {
		    retVal.add(getColumnCompare(child));
		} 
		else {
		    throw new IllegalArgumentException(
			    "left hand side and right hand side can only have columncompare child");
		}
	    }
	}
	return retVal;
    }
    /**
     * Gets comparator objects.
     * The element passed in should have a childnode "operator" and that child node should have attribute  "type"
     * @param e The element that has a childnode "operator"
     * @return Arraylist of StatsCOmparatorOperator that has been constructed depending on the "type" attribute of the "operator" tag
     */
    private static ArrayList<StatsComparatorOperator> getComparator(Element e){
	ArrayList<StatsComparatorOperator> retVal = new ArrayList<StatsComparatorOperator>();
	NodeList nl = e.getElementsByTagName("operator");
	int len = nl.getLength();
	
	for(int i = 0; i < len; i++){
	    Element child = (Element)nl.item(i);
	    retVal.add(new StatsComparatorOperator(requiredAttributeGetter(child, "type")));
	}
	return retVal;
    }

    private static StatsColumnCompare getColumnCompare(Element e) {
	String columnname = requiredAttributeGetter(e, "columnname");
	String tablename = requiredAttributeGetter(e, "tablename");
	return new StatsColumnCompare(tablename, columnname);
    }
    
}
