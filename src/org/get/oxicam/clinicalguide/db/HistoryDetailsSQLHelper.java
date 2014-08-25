package org.get.oxicam.clinicalguide.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

public class HistoryDetailsSQLHelper extends SQLiteOpenHelper {

	public static final String TABLE_HISTORY_DETAILS 	= "history_details";
	public static final String HISTORY_ID 				= "historyIndexID";
	public static final String HISTORY_QUESTION_ID 		= "questionID";
	public static final String HISTORY_QUESTION_TEXT 	= "questionText";
	public static final String HISTORY_ANSWER_ID 		= "answerID";
	public static final String HISTORY_ANSWER_VALUE 	= "answerValue";
	public static final String HISTORY_ANSWER_TYPE 		= "answerType";
	public static final String HISTORY_PATIENT_ID 		= "patientID";

	private static final String HISTORY_DATABASE_NAME = "history_details.db";
	private static final int HISTORY_DATABASE_VERSION = 1;

	// Database creation SQL statement
	private static final String HISTORY_DATABASE_CREATE = "create table " +
			TABLE_HISTORY_DETAILS + "(" + 
			HISTORY_ID + " integer primary key autoincrement, " + 
			HISTORY_QUESTION_ID + " integer not null, " +
			HISTORY_QUESTION_TEXT + " text not null, " +
			HISTORY_ANSWER_ID + " text not null, " +
			HISTORY_ANSWER_VALUE + " text not null, " +
			HISTORY_ANSWER_TYPE + " text not null, " +
			HISTORY_PATIENT_ID + " integer not null" +
			");";

	public HistoryDetailsSQLHelper(Context context) {
		super(context, HISTORY_DATABASE_NAME, null, HISTORY_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(HISTORY_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(HistoryDetailsSQLHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY_DETAILS);
		onCreate(db);
	}
}
