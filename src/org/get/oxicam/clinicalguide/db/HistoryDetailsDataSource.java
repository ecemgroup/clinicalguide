package org.get.oxicam.clinicalguide.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This DAO class. It maintains the database connection and supports adding new HistoryDetailss and fetching all HistoryDetailss.
 */
public class HistoryDetailsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private HistoryDetailsSQLHelper dbHelper;
	private String[] allColumns = { 
			HistoryDetailsSQLHelper.HISTORY_ID,
			HistoryDetailsSQLHelper.HISTORY_QUESTION_ID,
			HistoryDetailsSQLHelper.HISTORY_QUESTION_TEXT,
			HistoryDetailsSQLHelper.HISTORY_ANSWER_ID,
			HistoryDetailsSQLHelper.HISTORY_ANSWER_VALUE,
			HistoryDetailsSQLHelper.HISTORY_ANSWER_TYPE,
			HistoryDetailsSQLHelper.HISTORY_PATIENT_ID
	};

	public HistoryDetailsDataSource(Context context) {
		dbHelper = new HistoryDetailsSQLHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new history details record in the database.
	 * @param history The details to be inserted
	 * @return The history record as read from the database after the insert. Used for validations
	 */
	public HistoryDetails createHistoryDetailsRecord(HistoryDetails historyRecord) {
		ContentValues values = new ContentValues();
		values.put(HistoryDetailsSQLHelper.HISTORY_QUESTION_ID, historyRecord.getQuestionID());
		values.put(HistoryDetailsSQLHelper.HISTORY_QUESTION_TEXT, historyRecord.getQuestionText());
		values.put(HistoryDetailsSQLHelper.HISTORY_ANSWER_ID, historyRecord.getAnswerID());
		values.put(HistoryDetailsSQLHelper.HISTORY_ANSWER_VALUE, historyRecord.getAnswerValue());
		values.put(HistoryDetailsSQLHelper.HISTORY_ANSWER_TYPE, historyRecord.getAnswerType());
		values.put(HistoryDetailsSQLHelper.HISTORY_PATIENT_ID, historyRecord.getPatientID());
		
		long insertId = database.insert(HistoryDetailsSQLHelper.TABLE_HISTORY_DETAILS, null,
				values);
		Cursor cursor = database.query(HistoryDetailsSQLHelper.TABLE_HISTORY_DETAILS,
				allColumns, HistoryDetailsSQLHelper.HISTORY_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		HistoryDetails newHistoryRecord = cursorToHistoryRecord(cursor);
		cursor.close();
		return newHistoryRecord;
	}

	/**
	 * Deletes a history record from the database
	 */
	public void deleteHistoryDetails(HistoryDetails HistoryDetails) {
		long id = HistoryDetails.getHistoryIndexID();
		System.out.println("HistoryDetails deleted with id: " + id);
		database.delete(HistoryDetailsSQLHelper.TABLE_HISTORY_DETAILS, 
				HistoryDetailsSQLHelper.HISTORY_ID + " = " + id, null);
	}

	/**
	 * Gets the whole table back as a list of history records
	 * @return List of history records
	 */
	public List<HistoryDetails> getAllhistoryDetailRecords() {
		List<HistoryDetails> HistoryDetailsList = new ArrayList<HistoryDetails>();

		Cursor cursor = database.query(HistoryDetailsSQLHelper.TABLE_HISTORY_DETAILS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HistoryDetails historyDetails = cursorToHistoryRecord(cursor);
			HistoryDetailsList.add(historyDetails);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return HistoryDetailsList;
	}

	/**
	 * Gets all the history records relating to a patient
	 * @param patientID The ID of the patient to be looked up
	 * @return The patient history records
	 */
	public List<HistoryDetails> getPatientHistory(String patientID){
		List<HistoryDetails> HistoryDetailsList = new ArrayList<HistoryDetails>();
		
		Cursor cursor = database.rawQuery("select * from " +
								HistoryDetailsSQLHelper.TABLE_HISTORY_DETAILS +
								" where " +
								HistoryDetailsSQLHelper.HISTORY_PATIENT_ID +
								"= ?", new String[] { patientID });

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HistoryDetails historyDetails = cursorToHistoryRecord(cursor);
			HistoryDetailsList.add(historyDetails);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return HistoryDetailsList;
	}
	
	private HistoryDetails cursorToHistoryRecord(Cursor cursor) {
		HistoryDetails record = new HistoryDetails();
		record.setHistoryIndexID(cursor.getLong(0));
		record.setQuestionID(cursor.getString(1));
		record.setQuestionText(cursor.getString(2));
		record.setAnswerID(cursor.getString(3));
		record.setAnswerValue(cursor.getString(4));
		record.setAnswerType(cursor.getString(5));
		record.setPatientID(cursor.getLong(6));

		return record;
	}
} 