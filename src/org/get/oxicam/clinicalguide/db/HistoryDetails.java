package org.get.oxicam.clinicalguide.db;

/**
 * Contains a patient's history. Every question that's answered is saved here for later review.
 */
public class HistoryDetails {
	//Auto incrementing history index
	private long	historyIndexID;
	//The question ID from the XML
	private String 	questionID;
	//The question text - used for fast display
	private String  questionText;
	//The answer ID for the question
	private String  answerID;
	//If a value was entered this is stored here
	private String  answerValue;
	//Since the value is saved as a string this field is used to keep the type
	private String  answerType;
	//The patient to whom this history record applies.
	private long	patientID;

	public long getPatientID() {
		return patientID;
	}
	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}
	public long getHistoryIndexID() {
		return historyIndexID;
	}
	public void setHistoryIndexID(long historyIndexID) {
		this.historyIndexID = historyIndexID;
	}
	public String getQuestionID() {
		return questionID;
	}
	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getAnswerID() {
		return answerID;
	}
	public void setAnswerID(String answerID) {
		this.answerID = answerID;
	}
	public String getAnswerValue() {
		return answerValue;
	}
	public void setAnswerValue(String answerValue) {
		this.answerValue = answerValue;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	@Override
	public String toString(){
		String recordString;
		recordString = historyIndexID + ", " +
				questionID + ", " +
				questionText + ", " +
				answerID + ", " +
				answerValue + ", " +
				answerType + ", " +
				patientID;		
		return recordString;

	}
}
