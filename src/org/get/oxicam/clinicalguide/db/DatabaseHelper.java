package org.get.oxicam.clinicalguide.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.get.oxicam.clinicalguide.ClinicalGuideActivity;
import org.get.oxicam.clinicalguide.FileUtils;
import org.get.oxicam.clinicalguide.encryption.Encryption;
import org.get.oxicam.clinicalguide.xml.CGParser;
import org.get.oxicam.clinicalguide.xml.DateHelper;
import org.get.oxicam.clinicalguide.xml.data.Answer;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;
import org.get.oxicam.clinicalguide.xml.data.Question;
import org.get.oxicam.clinicalguide.xml.data.TreatmentAction;
import org.get.oxicam.clinicalguide.xml.data.User;
import org.get.oxicam.clinicalguide.xml.query.QueryResultCell;
import org.get.oxicam.clinicalguide.xml.query.QueryResultRow;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "Database";

	private ArrayList<PatientAttribute> patientAttributes = null;
	private static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "clinical_guide_db";
	private static final String PACKAGE_NAME = "org.get.oxicam.clinicalguide";

	private static final String QUERY_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS user ( "
			+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "name TEXT NOT NULL UNIQUE," + "password TEXT NOT NULL );";

	private static final String QUERY_CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS history ( "
			+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "patient INTEGER NOT NULL,"
			+ "start_time LONG NOT NULL, "
			+ "end_time LONG NOT NULL, "
			+ "duration LONG NOT NULL, "
			+ "user_id INTEGER NOT NULL,"
			+ "primary_symptom TEXT NOT NULL,"
			+ "recdiag_ID TEXT NOT NULL,"
			+ "recommended_diag TEXT NOT NULL,"
			+ "chsdiag_ID TEXT NOT NULL," + "chosen_diag TEXT NOT NULL);";

	private static final String QUERY_CREATE_HISTORY_ANSWERS_TABLE = "CREATE TABLE IF NOT EXISTS history_answers ( "
			+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "assessment INTEGER NOT NULL,"
			+ "question_id TEXT NOT NULL,"
			+ "question TEXT NOT NULL,"
			+ "answer_type TEXT NOT NULL,"
			+ "answer_value TEXT NOT NULL );";

	private static final String QUERY_CREATE_HISTORY_TREATMENT_TABLE = "CREATE TABLE IF NOT EXISTS history_treatment ( "
			+ "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ "assessment INTEGER NOT NULL,"
			+ "treatmentActionID TEXT NOT NULL,"
			+ "treatmentAction TEXT NOT NULL);";

	/**
	 * <p>Appends each attribute of the patient contact information
	 * into one SQL statement and writes it to the database.</p>
	 */
	public void createPatientDetailsTable() {
		String PATIENT_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS patient_details (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT";
		for (PatientAttribute attribute : this.patientAttributes) {
			PATIENT_DATABASE_CREATE += ", " + attribute.name + " "
					+ attribute.type;
		}
		PATIENT_DATABASE_CREATE += ");";

		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(PATIENT_DATABASE_CREATE);
		db.close();
	}

	/**
	 * <p>Assigns patientsAttributes the passed in arraylist of
	 * attributes</p>
	 *  
	 * @param attributes
	 * 			the attributes to be stored as the patientsAttributes			
	 */
	public void setPatientAttributes(ArrayList<PatientAttribute> attributes) {
		this.patientAttributes = attributes;
	}

	/**
	 * <p>Returns the patients current list of attributes.</p>
	 * 
	 * @return
	 * 		the current list patient attributes
	 */
	public ArrayList<PatientAttribute> getPatientAttributes() {
		return this.patientAttributes;
	}

	/**
	 * <p>Queries to the database to retrieve the patients
	 * birthdate in the form yyyy-mm-dd. Uses the method 
	 * calculateAge() in the class DateHelper to convert the 
	 * current birdate format into a float, returning the float
	 * as the patients age.</p>
	 * 
	 * @param patientId
	 * 			The Id of the patient whose age you want
	 * @return
	 * 		the age of the patientId passed in
	 */
	public float getPatientAge(long patientId) {
		float age = 0;
		SQLiteDatabase db = getReadableDatabase();
		String[] args = { "" + patientId };
		Cursor c = db.rawQuery(
				"SELECT birthdate FROM patient_details WHERE id == ?", args);
		if (c.moveToFirst()) {
			String birthdateString = c.getString(0);
			Encryption dec = new Encryption(null);
			try 
			{
				birthdateString = new String(dec.decryptData(birthdateString), "ISO-8859-1");
			} 
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// try {
			// Date birthdate = format.parse(birthdateString);
			// Date today = new Date();
			long bdate = DateHelper.changeTextDateToLong(birthdateString);

			// long diffInDays = (today.getTime() - birthdate.getTime())
			// / (1000 * 60 * 60 * 24);
			age = DateHelper.calculateAge(bdate);
			// } catch (ParseException e) {
			// e.printStackTrace();
			// }

		}
		c.close();
		db.close();
		return age;
	}

	/**
	 * A
	 * @param attributes
	 */
	public void addPatientDetails(ArrayList<PatientAttribute> attributes) throws Exception {
		SQLiteDatabase db = getWritableDatabase();

		Validator v = new Validator();
		v.checkPatientAttriutes(attributes);
		
		ContentValues vals = new ContentValues();
		for (PatientAttribute attribute : attributes) {
			Encryption en = new Encryption(null);
			try 
			{
				//Log.d("DEBUG", attribute.answer);
				/*if(attribute.answer.equals("")) {
					throw new Exception("Fields cannot be empty");
				}*/
				
				attribute.answer = new String(en.encryptData(attribute.answer), "ISO-8859-1");	
			} 
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			vals.put(attribute.name, attribute.answer);
		}
		db.insert("patient_details", null, vals);
		db.close();
	}

	public void updatePatientDetails(long PatientId,
			ArrayList<PatientAttribute> attributes) throws Exception {
		SQLiteDatabase db = getWritableDatabase();
		
		Validator v = new Validator();
		v.checkPatientAttriutes(attributes);

		ContentValues vals = new ContentValues();
		for (PatientAttribute attribute : attributes) {
			Encryption en = new Encryption(null);
			try 
			{
				attribute.answer = new String(en.encryptData(attribute.answer), "ISO-8859-1");
			} 
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			vals.put(attribute.name, attribute.answer);
		}
		String where = "id=?";
		String[] whereArgs = new String[] { String.valueOf(PatientId) };

		db.update("patient_details", vals, where, whereArgs);
		db.close();
	}

	/**
	 * <p>Deletes the paitent and their details from the database</p>
	 * 
	 * @param mKey
	 * @param mName
	 */
	public void deletePatient(String mKey, String mName) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete("patient_details", mKey + "=?", new String[] { mName });
		db.close();
	}

	
	/**
	 * <p>Helper function for getPatient(Cursor cursor)</p>
	 * 
	 * <p>Gets the attributes of this instances patientAttributes
	 * and stores them in an attribute object. Each attribute object
	 * is added to an ArrayList of PatientAttribute and returned.</p>
	 * 
	 * @return
	 * 		an ArrayList of PatientAttribute
	 */
	private ArrayList<PatientAttribute> getAttributeList() {
		ArrayList<PatientAttribute> attributes = new ArrayList<PatientAttribute>();
		for (PatientAttribute patientAttribute : patientAttributes) {
			PatientAttribute attribute = new PatientAttribute(
					patientAttribute.name, patientAttribute.value,
					patientAttribute.type);
			attributes.add(attribute);
		}
		return attributes;
	}

	/**
	 * <p>Helper function for getAllPatientDetailRecords()</p>
	 * 
	 * <p>Makes a call to the private method getAttributeList(),
     * and sets this instances answer to the answer retrieved 
     * if it equals the colmnname.</p>
	 *  
	 * @param cursor
	 * @return
	 */
	private PatientDetails getPatient(Cursor cursor) {
		int numberOfColumns = cursor.getColumnCount();
		ArrayList<PatientAttribute> attributes = getAttributeList();
		for (int i = 1; i < numberOfColumns; i++) {
			String columnname = cursor.getColumnName(i);
			String answer = cursor.getString(i);
			for (PatientAttribute attribute : attributes) {
				if (columnname.equals(attribute.name)) {
					Encryption dec = new Encryption(null);
					try 
					{
						answer = new String(dec.decryptData(answer), "ISO-8859-1");
					} 
					catch (UnsupportedEncodingException e) { e.printStackTrace(); }
					attribute.answer = answer;
					break;
				}
			}
		}
		PatientDetails record = new PatientDetails(attributes);
		record.setPatientID(cursor.getLong(0));
		return record;
	}
	
	/**
	 * <p>Queries the database for all the patients details that
	 * are stored. Makes a looping call to getPatient(Cursor c) to
	 * add each patients record to the arrayList PatientDetails</p>
	 * 
	 * @return
	 * 		An arrayList of all Patients details
	 */
	public ArrayList<PatientDetails> getAllPatientDetailRecords() {
		ArrayList<PatientDetails> patientDetailsList = new ArrayList<PatientDetails>();
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM patient_details;", null);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			patientDetailsList.add(getPatient(c));			
			c.moveToNext();
		}
		// Make sure to close the cursor
		c.close();
		db.close();
		return patientDetailsList;
	}

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		if (getPatientAttributes() == null) {
			if (context instanceof ClinicalGuideActivity) {
				CGParser parser = ((ClinicalGuideActivity) context)
						.getXmlParser();
				if (parser != null) {
					setPatientAttributes(parser.getPatientDetails());
					createPatientDetailsTable();
				}
			}
		}
	}

	public void addAssessment(Assessment assessment, User u) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put("patient", assessment.patient.getPatientID());
		Long tsLong = System.currentTimeMillis();
		Long start = assessment.starttime;
		Long duration = (tsLong - start) / 1000;// in seconds
		vals.put("end_time", tsLong); // FIXME: SOLVED not now the
		// string but the time
		vals.put("start_time", start);
		vals.put("duration", duration);
		vals.put("user_id", u.id);
		vals.put("recdiag_ID", assessment.recommendedTreatment.treatmentId);
		vals.put("recommended_diag",
				assessment.recommendedTreatment.classification);
		vals.put("chsdiag_ID", assessment.chosenTreatment.treatmentId);
		vals.put("chosen_diag", assessment.chosenTreatment.classification);
		vals.put("primary_symptom", assessment.primarySymptom);
		long id = db.insert("history", null, vals);

		for (Answer a : assessment.answers.values()) {
			vals = new ContentValues();
			vals.put("assessment", id);
			vals.put("question_id", a.question.questionId);
			vals.put("question", a.question.label);
			vals.put("answer_type", a.question.answerType);
			vals.put("answer_value", a.value);
			db.insert("history_answers", null, vals);
			// Log.d(TAG, "added history_answer:" + id + "," + a.question.label
			// + "," +
			// a.question.answerType + "," + a.value);
		}

		db.close();

		addHistoryTreatment(assessment, id);
	}

	private void addHistoryTreatment(Assessment assessment, Long u) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues vals = new ContentValues();
		for (TreatmentAction ta : assessment.chosenTreatment.chosenTreatmentActions) {
			String taId = ta.treatmentActionId;
			String treatment = ta.label;
			vals.put("assessment", u);
			vals.put("treatmentActionID", taId);
			vals.put("treatmentAction", treatment);
			db.insert("history_treatment", null, vals);
		}

		db.close();
	}

	public String getLastAnswerDateForPatient(long patientId) {

		SQLiteDatabase db = getReadableDatabase();
		String[] args = { "" + patientId };
		String assessmentTime = null;
		Cursor c = db.rawQuery(
				"SELECT MAX(end_time) FROM history WHERE patient == ?", args);
		if (c.moveToFirst()) {
			assessmentTime = c.getString(0);
			Log.d(TAG, "found past assessment time: " + assessmentTime);

		}
		c.close();
		db.close();

		return assessmentTime;
	}

	public String getPasswordForUser(String user) throws Exception {
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { "password" };
		String[] args = { user };
		Cursor res = db
				.query("user", cols, "name == ?", args, null, null, null);
		res.moveToFirst();
		if (res.getCount() > 0) {
			String pwd = res.getString(0);
			res.close();
			db.close();
			return pwd;
		} else {
			res.close();
			db.close();
			throw new Exception("User is not existent");
		}
	}

	public int getUserId(String user) throws Exception {
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { "id" };
		String[] args = { user };
		Cursor res = db
				.query("user", cols, "name == ?", args, null, null, null);
		res.moveToFirst();
		if (res.getCount() > 0) {
			String pwd = res.getString(0);
			res.close();
			db.close();
			return Integer.parseInt(pwd);
		} else {
			res.close();
			db.close();
			throw new Exception("User is not existent");
		}
	}

	public void addNewUser(String username, String password) throws Exception {
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { "name" };
		String[] args = { username };
		Cursor res = db
				.query("user", cols, "name == ?", args, null, null, null);
		res.moveToFirst();
		if (res.getCount() > 0) {
			throw new Exception("Username already taken.");
		}
		SQLiteDatabase dbW = getWritableDatabase();
		dbW.execSQL("INSERT INTO user (name, password) VALUES('" + username
				+ "', '" + password + "')");
		res.close();
		db.close();
		dbW.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating DB tables");

		db.execSQL(QUERY_CREATE_USER_TABLE);
		db.execSQL(QUERY_CREATE_HISTORY_TABLE);
		db.execSQL(QUERY_CREATE_HISTORY_ANSWERS_TABLE);
		db.execSQL(QUERY_CREATE_HISTORY_TREATMENT_TABLE);

		// db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Droping DB tables in order to upgrade");

		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS history");
		db.execSQL("DROP TABLE IF EXISTS history_answers");
		db.execSQL("DROP TABLE IF EXISTS patient_details");
		db.execSQL("DROP TABLE IF EXISTS history_treatment");
		db.execSQL("DROP TABLE IF EXISTS Diagnosis"); // It only exists in
		// Database version 3

		onCreate(db);
	}

	public ArrayList<Integer> getAssessmentListForPatient(long patientId) {
		Log.w(TAG, "Getting list of assessments for patient: " + patientId);

		String[] id = { "id" };
		String[] pId = { Long.toString(patientId) };
		ArrayList<Integer> assessments = new ArrayList<Integer>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor res = db.query("history", id, "patient = ?", pId, null, null,
				null);
		if (res.moveToFirst()) {
			do {
				assessments.add(res.getInt(0));
			} while (res.moveToNext());
		}
		res.close();
		db.close();

		return assessments;
	}

	public ArrayList<Answer> getSpecificANswerForPatient(long patientId,
			int assessmentId) {
		ArrayList<Answer> answers = new ArrayList<Answer>();
		int assessId = assessmentId;
		Cursor c;

		SQLiteDatabase db = getReadableDatabase();
		String[] args = { "" + patientId };

		if (assessmentId == -1) {
			c = db.rawQuery("SELECT MAX(id) FROM history WHERE patient == ?",
					args);
			c.moveToFirst();
			assessId = c.getInt(0);
		}
		Log.d(TAG, "found past assessment: " + assessId);

		String[] cols = { "question", "answer_type", "answer_value" };
		String[] args2 = { "" + assessId };
		c = db.query("history_answers", cols, "assessment == ?", args2, null,
				null, null);
		if (c.moveToFirst()) {
			do {
				Question q = new Question(null, c.getString(0), c.getString(1),
						null, null, null, null);
				Answer a = new Answer(q, c.getString(2));
				answers.add(a);
			} while (c.moveToNext());
		}

		c.close();
		db.close();

		return answers;
	}

	public ArrayList<String> getIllnessListForPatient(long patientId) {
		Log.w(TAG, "Getting illness list for patient: " + patientId);

		ArrayList<String> illness = new ArrayList<String>();
		String sql = "select primary_symptom " + "from history "
				+ "where patient = ? ";
		String[] pId = { Long.toString(patientId) };

		SQLiteDatabase db = getReadableDatabase();
		Cursor res = db.rawQuery(sql, pId);
		if (res.moveToFirst()) {
			do {
				illness.add(res.getString(0));
			} while (res.moveToNext());
		}
		res.close();
		db.close();

		return illness;
	}

	public QueryResultTable executeSQLstatement(String statement) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<QueryResultRow> rows = new ArrayList<QueryResultRow>();
		QueryResultTable result = null;
		ArrayList<LinkedHashMap<String, String>> retVal = new ArrayList<LinkedHashMap<String, String>>();
		Cursor cur = db.rawQuery(statement, null);
		if (cur.moveToFirst()) {
			do {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				ArrayList<QueryResultCell> cells = new ArrayList<QueryResultCell>();
				int count = cur.getColumnCount();
				for (int i = 0; i < count; i++) {
					String colName = cur.getColumnName(i);
					QueryResultCell cell = new QueryResultCell(colName,
							cur.getString(i));
					cells.add(cell);
					map.put(colName, cur.getString(i));
				}
				rows.add(new QueryResultRow(cells));
				retVal.add(map);

			} while (cur.moveToNext());
		}
		db.close();
		return new QueryResultTable(rows);
	}
	
	/**
	 * <p>Backups database to SDcard</p>
	 *  
	 * @param decrypt
	 * 			choose to decrypt the database, if encrypted (not functional)			
	 */
	public static  File backupDatabaseSDCard(boolean decrypt) {
		 String dateformat = "yyyyMMdd-HHmmss";
		 Calendar cal = Calendar.getInstance();
		 SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

		try {
	        File sd = FileUtils.getAppDirectory(); //Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();
	  
	        if (sd.canWrite()) {
	            String currentDBPath = "//data//"+ PACKAGE_NAME +"//databases//"+DATABASE_NAME;
	            String backupDBPath = DATABASE_NAME+"_"+sdf.format(cal.getTime());
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);
	        		//    File backupDB = FileUtils.getDateFilename(DATABASE_NAME, "db");
			    System.out.println("DB from address: " + currentDB.toString());
			    System.out.println("DB to address: " + backupDB.toString());
	            if (currentDB.exists()) {
	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close(); 
	                return backupDB; 
	            }
	           
	        }
	    } catch (Exception e) {
	      //  Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
	    	System.out.println("SD DB write exception: " + e.toString());
	    	
	    }
		return null;
	}
}
