//package org.get.oxicam.clinicalguide.db;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
///**
// * This DAO class. It maintains the database connection and supports adding new PatientDetailss and fetching all PatientDetailss.
// */
//public class PatientDetailsDataSource {
//
//	// Database fields
//	private SQLiteDatabase database;
//	private PatientDetailsSQLHelper dbHelper;
////	private String[] allColumns = { 
////			PatientDetailsSQLHelper.PATIENT_ID,
////			PatientDetailsSQLHelper.PATIENT_NAME,
////			PatientDetailsSQLHelper.PATIENT_SURNAME,
////			PatientDetailsSQLHelper.PATIENT_MOBILE,
////			PatientDetailsSQLHelper.PATIENT_HOME,
////			PatientDetailsSQLHelper.PATIENT_WORK,
////			PatientDetailsSQLHelper.PATIENT_ADDESS,
////			
////			PatientDetailsSQLHelper.PATIENT_HEIGHT,
////			PatientDetailsSQLHelper.PATIENT_WEIGHT,
////			PatientDetailsSQLHelper.PATIENT_GENDER,
////			PatientDetailsSQLHelper.PATIENT_KNOWNALIMENTS,
////			PatientDetailsSQLHelper.PATIENT_BIRTHDATE
////	};
//
//	public PatientDetailsDataSource(Context context) {
//		dbHelper = new PatientDetailsSQLHelper(context);
//	}
//
//	public void open() throws SQLException {
//		database = dbHelper.getWritableDatabase();
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
//
//	/**
//	 * Create a new patient details record in the database.
//	 * @param patient The details to be inserted
//	 * @return The patient record as read from the database after the insert. Used for validations
//	 */
//	public PatientDetails createPatientDetailsRecord(PatientDetails patient) {
//		ContentValues values = new ContentValues();
//		values.put(PatientDetailsSQLHelper.PATIENT_NAME, patient.getName());
//		values.put(PatientDetailsSQLHelper.PATIENT_SURNAME, patient.getSurname());
//		values.put(PatientDetailsSQLHelper.PATIENT_ADDESS, patient.getAddress());
//		values.put(PatientDetailsSQLHelper.PATIENT_MOBILE, patient.getMobileNumber());
//		values.put(PatientDetailsSQLHelper.PATIENT_HOME, patient.getHomeNumber());
//		values.put(PatientDetailsSQLHelper.PATIENT_WORK, patient.getWorkNumber());
//
//		values.put(PatientDetailsSQLHelper.PATIENT_WEIGHT, patient.getHeight());
//		values.put(PatientDetailsSQLHelper.PATIENT_HEIGHT, patient.getWeight());
//		values.put(PatientDetailsSQLHelper.PATIENT_GENDER, patient.getGender());
//		values.put(PatientDetailsSQLHelper.PATIENT_KNOWNALIMENTS, patient.getKnownAliments());
//		values.put(PatientDetailsSQLHelper.PATIENT_BIRTHDATE, patient.getBirthDate());
//		
//		long insertId = database.insert(PatientDetailsSQLHelper.TABLE_PATIENT_DETAILS, null,
//				values);
//		Cursor cursor = database.query(PatientDetailsSQLHelper.TABLE_PATIENT_DETAILS,
//				allColumns, PatientDetailsSQLHelper.PATIENT_ID + " = " + insertId, null,
//				null, null, null);
//		cursor.moveToFirst();
//		PatientDetails newPatient = cursorToPatient(cursor);
//		cursor.close();
//		return newPatient;
//	}
//
//	/**
//	 * Delete a patient record
//	 * @param PatientDetails THe record to be deleted
//	 */
//	public void deletePatientDetails(PatientDetails PatientDetails) {
//		long id = PatientDetails.getPatientID();
//		Log.d("PatientDB", "PatientDetails deleted with id: " + id);
//		database.delete(PatientDetailsSQLHelper.TABLE_PATIENT_DETAILS, 
//				PatientDetailsSQLHelper.PATIENT_ID + " = " + id, null);
//	}
//
//	public List<PatientDetails> getAllPatientDetailRecords() {
//		List<PatientDetails> PatientDetailsList = new ArrayList<PatientDetails>();
//
//		Cursor cursor = database.query(PatientDetailsSQLHelper.TABLE_PATIENT_DETAILS,
//				allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			PatientDetails patientDetails = cursorToPatient(cursor);
//			PatientDetailsList.add(patientDetails);
//			cursor.moveToNext();
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		return PatientDetailsList;
//	}
//	
//	/**
//	 * Update a patient record based on the ID provided in the patient record
//	 * @param patient The patient record to be updated.
//	 */
//	public void updatePatientRecord(PatientDetails patient)
//	{
//		long id = patient.getPatientID();
//		ContentValues values = new ContentValues();
//		values.put(PatientDetailsSQLHelper.PATIENT_NAME, patient.getName());
//		values.put(PatientDetailsSQLHelper.PATIENT_SURNAME, patient.getSurname());
//		values.put(PatientDetailsSQLHelper.PATIENT_ADDESS, patient.getAddress());
//		values.put(PatientDetailsSQLHelper.PATIENT_MOBILE, patient.getMobileNumber());
//		values.put(PatientDetailsSQLHelper.PATIENT_HOME, patient.getHomeNumber());
//		values.put(PatientDetailsSQLHelper.PATIENT_WORK, patient.getWorkNumber());
//
//		values.put(PatientDetailsSQLHelper.PATIENT_WEIGHT, patient.getHeight());
//		values.put(PatientDetailsSQLHelper.PATIENT_HEIGHT, patient.getWeight());
//		values.put(PatientDetailsSQLHelper.PATIENT_GENDER, patient.getGender());
//		values.put(PatientDetailsSQLHelper.PATIENT_KNOWNALIMENTS, patient.getKnownAliments());
//		values.put(PatientDetailsSQLHelper.PATIENT_BIRTHDATE, patient.getBirthDate());
//		
//		database.update(PatientDetailsSQLHelper.TABLE_PATIENT_DETAILS, values,
//				PatientDetailsSQLHelper.PATIENT_ID + " = " + id, null);		
//
//		Log.d("PatientDB", "PatientDetails updated id: " + id);
//	}
//
////	private PatientDetails cursorToPatient(Cursor cursor) {
//////		PatientDetails record = new PatientDetails();
//////		record.setPatientID(cursor.getLong(0));
//////		record.setName(cursor.getString(1));
//////		record.setSurname(cursor.getString(2));
//////		record.setAddress(cursor.getString(3));
//////		record.setMobileNumber(cursor.getString(4));
//////		record.setHomeNumber(cursor.getString(5));
//////		record.setWorkNumber(cursor.getString(6));
//////		
//////		record.setWeight(cursor.getDouble(7));
//////		record.setHeight(cursor.getDouble(8));
//////		record.setGender(cursor.getString(9));
//////		record.setKnownAliments(cursor.getString(10));
//////		record.setBirthDate(cursor.getString(11));
//////		return record;
////	}
//} 