package org.get.oxicam.clinicalguide.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This DAO class. It maintains the database connection and supports adding new FollowupDetailss and fetching all FollowupDetailss.
 */
public class FollowupDataSource {

	// Database fields
	private SQLiteDatabase database;
	private FollowupSQLHelper dbHelper;
	private String[] allColumns = { 
			FollowupSQLHelper.FOLLOWUP_ID,
			FollowupSQLHelper.FOLLOWUP_PATIENT_ID,
			FollowupSQLHelper.FOLLOWUP_XML_ID,
			FollowupSQLHelper.FOLLOWUP_DESCIPTION,
			FollowupSQLHelper.FOLLOWUP_START_DATE,
			FollowupSQLHelper.FOLLOWUP_END_DATE,
			FollowupSQLHelper.FOLLOWUP_REPEAT_MONTHLY,
			FollowupSQLHelper.FOLLOWUP_REPEAT_WEEKLY,
			FollowupSQLHelper.FOLLOWUP_NOTES,
			FollowupSQLHelper.FOLLOWUP_NOTIFICATIONS			
	};

	public FollowupDataSource(Context context) {
		dbHelper = new FollowupSQLHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new follow up record in the database.
	 * @param followupRecord The details to be inserted
	 * @return The follow-up record as read from the database after the insert. Used for validations
	 */
	public FollowupDetails createFollowupDetailsRecord(FollowupDetails followupRecord) {
		ContentValues values = new ContentValues();
		values.put(FollowupSQLHelper.FOLLOWUP_PATIENT_ID, followupRecord.getPatientID());
		values.put(FollowupSQLHelper.FOLLOWUP_XML_ID, followupRecord.getXmlID());
		values.put(FollowupSQLHelper.FOLLOWUP_DESCIPTION, followupRecord.getDescription());
		values.put(FollowupSQLHelper.FOLLOWUP_START_DATE, followupRecord.getStartDate().getTime());
		values.put(FollowupSQLHelper.FOLLOWUP_END_DATE, followupRecord.getEndDate().getTime());
		values.put(FollowupSQLHelper.FOLLOWUP_NOTES, followupRecord.getNotes());
		values.put(FollowupSQLHelper.FOLLOWUP_NOTIFICATIONS, followupRecord.getNotifications());
		values.put(FollowupSQLHelper.FOLLOWUP_REPEAT_MONTHLY, followupRecord.getRepeatMonthly());
		values.put(FollowupSQLHelper.FOLLOWUP_REPEAT_WEEKLY, followupRecord.getRepeatWeekly());

		long insertId = database.insert(FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS, null,
				values);
		Cursor cursor = database.query(FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS,
				allColumns, FollowupSQLHelper.FOLLOWUP_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		FollowupDetails newFollowupRecord = cursorToFollowupRecord(cursor);
		cursor.close();
		return newFollowupRecord;
	}
	
	public void updateFollowupDetailsRecord(FollowupDetails followupRecord) {
		ContentValues values = new ContentValues();
		values.put(FollowupSQLHelper.FOLLOWUP_PATIENT_ID, followupRecord.getPatientID());
		values.put(FollowupSQLHelper.FOLLOWUP_XML_ID, followupRecord.getXmlID());
		values.put(FollowupSQLHelper.FOLLOWUP_DESCIPTION, followupRecord.getDescription());
		values.put(FollowupSQLHelper.FOLLOWUP_START_DATE, followupRecord.getStartDate().getTime());
		values.put(FollowupSQLHelper.FOLLOWUP_END_DATE, followupRecord.getEndDate().getTime());
		values.put(FollowupSQLHelper.FOLLOWUP_NOTES, followupRecord.getNotes());
		values.put(FollowupSQLHelper.FOLLOWUP_NOTIFICATIONS, followupRecord.getNotifications());
		values.put(FollowupSQLHelper.FOLLOWUP_REPEAT_MONTHLY, followupRecord.getRepeatMonthly());
		values.put(FollowupSQLHelper.FOLLOWUP_REPEAT_WEEKLY, followupRecord.getRepeatWeekly());
		long id = followupRecord.getFollowupIndexID();
		database.update(FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS, values,
				FollowupSQLHelper.FOLLOWUP_ID + " = " + id, null);		
		System.out.println("FollowupDetails updated with id: " + id);
	}

	/**
	 * Deletes a follow-up record from the database
	 */
	public void deleteFollowupDetails(FollowupDetails FollowupDetails) {
		long id = FollowupDetails.getFollowupIndexID();
		System.out.println("FollowupDetails deleted with id: " + id);
		database.delete(FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS, 
				FollowupSQLHelper.FOLLOWUP_ID + " = " + id, null);
	}

	/**
	 * Gets the whole table back as a list of follow-up records
	 * @return List of follow-up records
	 */
	public List<FollowupDetails> getAllFollowUpRecords() {
		List<FollowupDetails> FollowupDetailsList = new ArrayList<FollowupDetails>();

		Cursor cursor = database.query(FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			FollowupDetails FollowupDetails = cursorToFollowupRecord(cursor);
			FollowupDetailsList.add(FollowupDetails);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return FollowupDetailsList;
	}

	/**
	 * Gets all the follow up records relating to a patient
	 * @param patientID The ID of the patient to be looked up
	 * @return The patient followup records
	 */
	public List<FollowupDetails> getPatientFollowup(String patientID){
		List<FollowupDetails> FollowupDetailsList = new ArrayList<FollowupDetails>();

		Cursor cursor = database.rawQuery("select * from " +
				FollowupSQLHelper.TABLE_FOLLOWUP_DETAILS +
				" where " +
				FollowupSQLHelper.FOLLOWUP_PATIENT_ID +
				"= ?", new String[] { patientID });

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			FollowupDetails FollowupDetails = cursorToFollowupRecord(cursor);
			FollowupDetailsList.add(FollowupDetails);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return FollowupDetailsList;
	}

	private FollowupDetails cursorToFollowupRecord(Cursor cursor) {
		FollowupDetails record = new FollowupDetails();
		record.setFollowupIndexID(cursor.getLong(0));
		record.setPatientID(cursor.getLong(1));
		record.setXmlID(cursor.getString(2));
		record.setDescription(cursor.getString(3));
		//set the dates and check the conversion
		try
		{
			record.setStartDate(new Date(cursor.getLong(4)));
			record.setEndDate(new Date(cursor.getLong(5)));
		} catch(Exception err)
		{
			Log.println(0, "Follow-up Record: Date type cast failed", err.getMessage());
		}

		record.setRepeatMonthly(cursor.getInt(6));
		record.setRepeatWeekly(cursor.getInt(7));
		record.setNotes(cursor.getString(8));
		record.setNotifications(cursor.getInt(9));

		return record;
	}
} 