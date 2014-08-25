package org.get.oxicam.clinicalguide.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PatientDetailsSQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_PATIENT_DETAILS = "patient_details";
	public static final String PATIENT_ID = "_id";
//	public static final String PATIENT_NAME = "name";
//	public static final String PATIENT_SURNAME = "surname";
//	public static final String PATIENT_ADDESS = "address";
//	public static final String PATIENT_MOBILE = "mobileNumber";
//	public static final String PATIENT_HOME = "homeNumber";
//	public static final String PATIENT_WORK = "workNumber";
//	
//	public static final String PATIENT_WEIGHT	= "weight"; 
//	public static final String PATIENT_HEIGHT = "height"; 
//	public static final String PATIENT_GENDER = "gender"; 
//	public static final String PATIENT_KNOWNALIMENTS = "knownAliments";	
//	public static final String PATIENT_BIRTHDATE = "birthDate";

	private static final String PATIENT_DATABASE_NAME = "patient_details.db";
	private static final int PATIENT_DATABASE_VERSION = 2;

	// Database creation SQL statement
//	private static final String PATIENT_DATABASE_CREATE = "create table " +
//			TABLE_PATIENT_DETAILS + "(" + 
//			PATIENT_ID + " integer primary key autoincrement, " + 
//			PATIENT_NAME + " text not null, " +
//			PATIENT_SURNAME + " text not null, " +
//			PATIENT_ADDESS + " text, " +
//			PATIENT_MOBILE + " text, " +
//			PATIENT_HOME + " text, " +
//			PATIENT_WORK + " text, " +
//			PATIENT_WEIGHT + " double, " +
//			PATIENT_HEIGHT + " double, " +
//			PATIENT_GENDER + " string, " +
//			PATIENT_KNOWNALIMENTS + " memo, " +
//			PATIENT_BIRTHDATE + " string" +
//			");";
	
//	public static String createTableStatement(ArrayList<PatientAttribute> attributes) {
//		String PATIENT_DATABASE_CREATE = "create table" +
//				TABLE_PATIENT_DETAILS + "(";
//				PATIENT_DATABASE_CREATE += "id integer primary key autoincrement";
//				for (PatientAttribute attribute : attributes) {
//					PATIENT_DATABASE_CREATE += attribute.name + " " + attribute.type;
//				}
//				
//		return PATIENT_DATABASE_CREATE;
//	}

	public PatientDetailsSQLHelper(Context context) {
		super(context, PATIENT_DATABASE_NAME, null, PATIENT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
//		database.execSQL(PATIENT_DATABASE_CREATE);
	}
	
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PatientDetailsSQLHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT_DETAILS);
		onCreate(db);
	}
}
