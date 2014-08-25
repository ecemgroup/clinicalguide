package org.get.oxicam.clinicalguide.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

public class FollowupSQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_FOLLOWUP_DETAILS 	= "followup_details";
	public static final String FOLLOWUP_ID 				= "indexID";
	public static final String FOLLOWUP_PATIENT_ID 		= "patientID";
	public static final String FOLLOWUP_XML_ID	 		= "xmlID";	
	public static final String FOLLOWUP_DESCIPTION	 	= "description";	
	public static final String FOLLOWUP_START_DATE	 	= "startDate";
	public static final String FOLLOWUP_END_DATE	 	= "endDate";
	public static final String FOLLOWUP_REPEAT_MONTHLY	= "repeatMonthly";
	public static final String FOLLOWUP_REPEAT_WEEKLY	= "repeatWeekly";
	public static final String FOLLOWUP_NOTES		 	= "notes";
	public static final String FOLLOWUP_NOTIFICATIONS 	= "notifications";
	

	private static final String FOLLOWUP_DATABASE_NAME = "follow_up.db";
	private static final int FOLLOWUP_DATABASE_VERSION = 2;

	// Database creation SQL statement
	private static final String FOLLOWUP_DATABASE_CREATE = "create table " +
			TABLE_FOLLOWUP_DETAILS + " (" + 
			FOLLOWUP_ID + " integer primary key autoincrement, " + 
			FOLLOWUP_PATIENT_ID + " integer not null, " +
			FOLLOWUP_XML_ID + " text, " +
			FOLLOWUP_DESCIPTION + " text, " +
			FOLLOWUP_START_DATE + " long, " +
			FOLLOWUP_END_DATE + " long, " +
			FOLLOWUP_REPEAT_MONTHLY + " integer, " +
			FOLLOWUP_REPEAT_WEEKLY + " integer, " +
			FOLLOWUP_NOTES + " text, " +
			FOLLOWUP_NOTIFICATIONS + " integer " +
			");";

	public FollowupSQLHelper(Context context) {
		super(context, FOLLOWUP_DATABASE_NAME, null, FOLLOWUP_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(FOLLOWUP_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FollowupSQLHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWUP_DETAILS);
		onCreate(db);
	}
}
