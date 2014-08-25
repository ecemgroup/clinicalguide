package org.get.oxicam.clinicalguide.xml;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.get.oxicam.clinicalguide.xml.forms.FormDuration;

public class DateHelper {
    private int duration;
    private String details;
    private Calendar calendar;

    /**
     * Constructs a datehelper to calculate dates.
     * Start Day of the week for this object depends on the FormDuration object.
     * If the details of the form is empty, Start of the week will be monday.
     * @param fd A FormDuration object that will hold the duraton (Form.DURATION_WEEK, etc..) and the start day of the week.
     */
    public DateHelper(FormDuration fd) {
	duration = fd.type;
	details = fd.detail;
	calendar = Calendar.getInstance();
	if (duration == FormDuration.DURATION_WEEK && !details.isEmpty()) {
	    calendar.setFirstDayOfWeek(dayToCalendarDayOfWeek(details));
	} else {
	    calendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
    }

    /**
     * Construct a DateHelper with a specified duration and specified adjustment.
     * Start Day of the week in this object will be Sunday.
     * @param duration duration of the timespan (Form.DURATION_WEEK, etc)
     * @param adjust Specific adjustment (ie. if duration is Form.DURATION_WEEK and adjust is 2, that will be the week two weeks ago)
     */
    public DateHelper(int duration, int adjust) {
	this.duration = duration;
	details = "";
	calendar = Calendar.getInstance();
	calendar.setFirstDayOfWeek(Calendar.SUNDAY);
	switch (duration) {
	case FormDuration.DURATION_YEAR:
	    if (adjust != 0) {
		GregorianCalendar gc = new GregorianCalendar(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
		//calendar.setTimeInMillis(gc.getTimeInMillis());
		for (int i = 1; i < adjust; i++) {
		    gc.add(Calendar.YEAR, -1);
		}
		calendar.setTimeInMillis(gc.getTimeInMillis() - 1);
	    }
	    break;
	case FormDuration.DURATION_MONTH:
	    if (adjust != 0) {
		GregorianCalendar gc = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		//calendar.setTimeInMillis(gc.getTimeInMillis());
		for (int i = 1; i < adjust; i++) {
		    gc.add(Calendar.MONTH, -1);
		}
		calendar.setTimeInMillis(gc.getTimeInMillis() - 1);
	    }
	    break;
	case FormDuration.DURATION_WEEK:
	    if (adjust != 0) {
		int diff = getDayDifference(calendar.getFirstDayOfWeek(),
			calendar.get(Calendar.DAY_OF_WEEK));
		calendar.add(Calendar.DAY_OF_YEAR, diff * -1);
		GregorianCalendar gc = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		
		
		for (int i = 1; i < adjust; i++) {
		    gc.add(Calendar.DAY_OF_YEAR, -7);
		}
		calendar.setTimeInMillis(gc.getTimeInMillis() - 1);
	    }
	    break;
	case FormDuration.DURATION_DAY:
	    if(adjust != 0){
		calendar = getMidnight(calendar);
		calendar.setTimeInMillis(calendar.getTimeInMillis() - 1);
		for(int i = 1; i < adjust; i++){
		    calendar.add(Calendar.DAY_OF_YEAR, -1);
		}
	    }
	    break;
	case FormDuration.DURATION_DEFINED:
	    break;
	}
    }

    public static long changeTextDateToLong(String s) {
	long retVal = 0;
	int yearL, monthL, dayL;
	String temp[] = s.split("-");
	if (temp.length == 3) {
	    yearL = Integer.valueOf(temp[0]);
	    monthL = Integer.valueOf(temp[1]) - 1;
	    dayL = Integer.valueOf(temp[2]);

	    final GregorianCalendar cal = new GregorianCalendar(yearL, monthL,
		    dayL + 1);

	    retVal = cal.getTimeInMillis() - 1;

	}

	return retVal;
    }
    /**
     * Creates a new calendar that will have the date of the calendar passed in parameter but at midnight.
     * This does not affect the original state of the calendar that is passed in the parameter.
     * @param c Calendar to get the midnight date from
     * @return a new GregorianCalendar with the same date but at midnight.
     */
    public static Calendar getMidnight(Calendar c){
	Calendar retVal = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	return retVal;
    }
    
    /**
     * Calculates the age based on the milliseconds
     * @param birthdate The milliseconds representing the date of the birthday
     * @return The year difference from todays date compare to the date that is passed in parameter.
     */
    public static int calculateAge(long birthdate) {
	int retVal = -1;
	Calendar c1 = Calendar.getInstance();
	c1.setTimeInMillis(birthdate);
	Calendar c2 = Calendar.getInstance();
	c1 = getMidnight(c1);
	c2 = getMidnight(c2);
	do{
	    retVal++;
	    c1.add(Calendar.YEAR, 1);
	    
	} while (c1.getTimeInMillis() <= c2.getTimeInMillis());

	return retVal;
    }

    /**
     * Gets the start day depending on the end date of the calendar that is inside this object.
     * This will call getStartDay(endDate).
     * Useful when the constructor DateHelper(type, adjust) was used when
     * creating the object. If the constructor DateHelper(formduration) was
     * used, endDate will be the current time.
     * 
     * @return A long representing milliseconds for the date
     */
    public long getStartDay() {
	return getStartDay(calendar.getTimeInMillis());
    }
    
    /**
     * Gets the end day depending on the calendar that is inside this object.
     * Useful when the constructor DateHelper(type, adjust) was used when
     * creating the object. If the constructor DateHelper(formduration) was
     * used, endDate will be the current time.
     * 
     * @return A long representing milliseconds for the date
     */
    public long getEndDay(){
	return calendar.getTimeInMillis();
    }

    /**
     * Gets the start day of the form depending on its duration.
     * 
     * @return A long representing milliseconds which can be transformed to
     *         date.
     */
    public long getStartDay(long endDate) {
	long retVal = -1;
	calendar.setTimeInMillis(endDate);
	switch (duration) {
	case FormDuration.DURATION_YEAR:
	    retVal = getFirstOfYear();
	    break;
	case FormDuration.DURATION_MONTH:
	    retVal = getFirstOfMonth();
	    break;
	case FormDuration.DURATION_WEEK:
	    retVal = getFirstOfWeek();
	    break;
	case FormDuration.DURATION_DEFINED:
	    retVal = getFirstOfDefined();
	    break;
	case FormDuration.DURATION_DAY:
	    retVal = getMidnight(calendar).getTimeInMillis();
	    break;
	}

	return retVal;
    }

    private long getFirstOfYear() {
	Calendar c = Calendar.getInstance();
	Date d = new Date();
	int year = calendar.get(Calendar.YEAR);
	GregorianCalendar gc = new GregorianCalendar(year, Calendar.JANUARY, 1);

	long retVal = gc.getTimeInMillis();

	return retVal;
    }

    private long getFirstOfMonth() {
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH);
	GregorianCalendar gc = new GregorianCalendar(year, month, 1);

	long retVal = gc.getTimeInMillis();

	return retVal;
    }

    private long getFirstOfWeek() {

	int firstDay = calendar.getFirstDayOfWeek();
	int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
	int diff = getDayDifference(firstDay, currentDay);

	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH);
	int day = calendar.get(Calendar.DAY_OF_MONTH);

	GregorianCalendar gc = new GregorianCalendar(year, month, day);

	gc.add(Calendar.DAY_OF_YEAR, (diff * -1));

	return gc.getTimeInMillis();
    }

    private long getFirstOfDefined() {
	long retVal = 0;
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH);
	int day = calendar.get(Calendar.DAY_OF_MONTH);

	int num = Integer.parseInt(details);

	GregorianCalendar gc = new GregorianCalendar(year, month, day);
	gc.add(Calendar.DAY_OF_YEAR, (num - 1) * -1);
	retVal = gc.getTimeInMillis();

	return retVal;
    }

    private int getDayDifference(int firstDay, int currentDay) {
	int retVal = 0;

	while (currentDay != firstDay) {
	    retVal++;
	    currentDay--;
	    if (currentDay == 0) {
		currentDay = 7;
	    }
	}

	return retVal;
    }

    /**
     * Turns a weekday string to the specific Calendar.DAY_OF_WEEK or -1 if the
     * day is invalid.
     * 
     * @param day
     *            The specific day to turn to int. eg. "sunday"
     * @return Returns Calendar.Monday for "monday", etc.. or -1 if the day is
     *         not a valid day
     */
    public int dayToCalendarDayOfWeek(String day) {
	int retVal = -1;
	if (day.equalsIgnoreCase("monday")) {
	    retVal = Calendar.MONDAY;
	} else if (day.equalsIgnoreCase("tuesday")) {
	    retVal = Calendar.TUESDAY;

	} else if (day.equalsIgnoreCase("wednesday")) {
	    retVal = Calendar.WEDNESDAY;

	} else if (day.equalsIgnoreCase("thursday")) {
	    retVal = Calendar.THURSDAY;

	} else if (day.equalsIgnoreCase("friday")) {
	    retVal = Calendar.FRIDAY;

	} else if (day.equalsIgnoreCase("saturday")) {
	    retVal = Calendar.SATURDAY;

	} else if (day.equalsIgnoreCase("sunday")) {
	    retVal = Calendar.SUNDAY;
	}
	return retVal;
    }

}
