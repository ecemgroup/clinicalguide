package org.get.oxicam.clinicalguide.xml.forms;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class FormCell {

	public final String name;
	public final String type;
	public final String value;

	public FormCell(String name, String type, String value) {
		this.name = name;
		this.type = type;
		if (type.trim().equalsIgnoreCase("current_date")) {

		}
		this.value = value;
	}

	/**
	 * Only call this if the cell.type is "current_date". Gets the String
	 * representation depending on the [value] that is passed in parameter.
	 * "month" will return the current month ("January", "February", etc) "year"
	 * will return the current year ("2013", etc) "month_day" will return
	 * current day of month in number ("1","2",.. "31") "week_day" will return
	 * current day of week ("Monday", Tuesday"...)
	 * 
	 * @param value
	 *            "month", "year", "month_day", or "week_day"
	 * @return A String representation of the current date depending on the
	 *         cell.value or an empty string if the cell.value is not valid or
	 *         cell.type is not current_date.
	 */
	public String getCurrentDateValue() {
		String retVal = "";
		if (this.type.trim().equalsIgnoreCase("current_date")) {
			String value = this.value.trim();
			Calendar c = Calendar.getInstance();
			int num;
			if (value.equalsIgnoreCase("month")) {
				num = c.get(Calendar.MONTH);
				retVal = new DateFormatSymbols().getMonths()[num];
			} else if (value.equalsIgnoreCase("year")) {
				retVal = c.get(Calendar.YEAR) + "";
			} else if (value.equalsIgnoreCase("month_day")) {
				retVal = c.get(Calendar.DAY_OF_MONTH) + "";
			} else if (value.equalsIgnoreCase("week_day")) {
				num = c.get(Calendar.DAY_OF_WEEK);
				retVal = new DateFormatSymbols().getWeekdays()[num];
			}
		}
		return retVal;
	}
}
