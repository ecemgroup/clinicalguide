package org.get.oxicam.clinicalguide.db;

import java.util.Date;

/**
 * Contains a patient's follow-up appointments. Every question that's answered is saved here for later review.
 */
public class FollowupDetails {
	//The primary key
	private long	followupIndexID;
	//foreign key to patient database
	private long 	patientID;
	//foreign key to XML ID describing the follow-up procedure
	private String  xmlID;
	//Follow-up description 
	private String  description;
	//The follow up start date 
	private Date  	startDate;
	//The follow up end date 
	private Date  	endDate;
	//Whether the follow up is monthly - 0 = no & !0 = yes
	private int		repeatMonthly;
	//Whether the follow up is weekly - 0 = no, 1 = Monday,... , 7 = Sunday
	private int		repeatWeekly;
	//Any addition notes about the follow-up
	private String	notes;
	//Whether the follow-up should cause push notifications (0= off)
	private int		notifications;

	public long getFollowupIndexID() {
		return followupIndexID;
	}
	public void setFollowupIndexID(long followupIndexID) {
		this.followupIndexID = followupIndexID;
	}
	public long getPatientID() {
		return patientID;
	}
	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}
	public String getXmlID() {
		return xmlID;
	}
	public void setXmlID(String xmlID) {
		this.xmlID = xmlID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getRepeatMonthly() {
		return repeatMonthly;
	}
	public void setRepeatMonthly(int repeatMonthly) {
		this.repeatMonthly = repeatMonthly;
	}
	public int getRepeatWeekly() {
		return repeatWeekly;
	}
	public void setRepeatWeekly(int repeatWeekly) {
		this.repeatWeekly = repeatWeekly;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public int getNotifications() {
		return notifications;
	}
	public void setNotifications(int notifications) {
		this.notifications = notifications;
	}
	
	@Override
	public String toString()
	{
		String result;
		result = this.followupIndexID + ", " +
				this.patientID + ", " +
				this.xmlID + ", " +
				this.description + ", " +
				this.startDate + ", " +
				this.endDate + ", " +
				this.repeatMonthly + ", " +
				this.repeatWeekly + ", " +
				this.notes + ", " +
				this.notifications ;
		return result;
	}
}
