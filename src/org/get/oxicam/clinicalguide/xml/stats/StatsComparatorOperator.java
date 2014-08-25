package org.get.oxicam.clinicalguide.xml.stats;

public class StatsComparatorOperator {

    public static final int LESS_THAN = 0;
    public static final int GREATER_THAN = 1;
    public static final int LESS_THAN_EQUAL = 2;
    public static final int GREATER_THAN_EQUAL = 3;
    public static final int EQUAL = 4;
    public static final int NOT_EQUAL = 5;

    public final int typeInt;
    public final String typeString;

    public StatsComparatorOperator(String type) {
	typeString = type;
	typeInt = changeStringToInt(type);
    }

    public StatsComparatorOperator(int type) {
	typeInt = type;
	typeString = changeIntToString(type);

    }

    private int changeStringToInt(String str) {
	str = str.trim();
	int retVal = -1;
	if (str.equalsIgnoreCase("less_than")) {
	    retVal = LESS_THAN;
	} else if (str.equalsIgnoreCase("greater_than")) {
	    retVal = GREATER_THAN;
	} else if (str.equalsIgnoreCase("greater_than_equal")) {
	    retVal = GREATER_THAN_EQUAL;
	} else if (str.equalsIgnoreCase("less_than_equal")) {
	    retVal = LESS_THAN_EQUAL;
	} else if (str.equalsIgnoreCase("equal")) {
	    retVal = EQUAL;
	} else if (str.equalsIgnoreCase("not_equal")) {
	    retVal = NOT_EQUAL;
	} else {
	    throw new IllegalArgumentException(
		    "Type of comparator not recognized");
	}

	return retVal;
    }

    private String changeIntToString(int i) {
	String retVal = "error";
	switch (i) {
	case LESS_THAN:
	    retVal = "less_than";
	    break;
	case GREATER_THAN:
	    retVal = "greater_than";
	    break;
	case LESS_THAN_EQUAL:
	    retVal = "less_than_equal";
	    break;
	case GREATER_THAN_EQUAL:
	    retVal = "greater_than_equal";
	    break;
	case EQUAL:
	    retVal = "equal";
	    break;
	case NOT_EQUAL:
	    retVal = "not_equal";
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Type of comparator not recognized");

	}
	return retVal;
    }
    
    
    public String toString(){
	String retVal;
	switch (typeInt) {
	case LESS_THAN:
	    retVal = " < ";
	    break;
	case GREATER_THAN:
	    retVal = " > ";
	    break;
	case LESS_THAN_EQUAL:
	    retVal = " <= ";
	    break;
	case GREATER_THAN_EQUAL:
	    retVal = " >= ";
	    break;
	case EQUAL:
	    retVal = " = ";
	    break;
	case NOT_EQUAL:
	    retVal = " != ";
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Type of comparator not recognized");

	}
	return retVal;
    }

}
