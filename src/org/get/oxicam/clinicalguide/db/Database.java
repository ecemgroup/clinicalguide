package org.get.oxicam.clinicalguide.db;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.xml.data.Answer;
import org.get.oxicam.clinicalguide.xml.data.Assessment;
import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;
import org.get.oxicam.clinicalguide.xml.data.User;
import org.get.oxicam.clinicalguide.xml.query.QueryResultTable;

import android.content.Context;

public class Database {

	/** <p>Used to make calls to the methods in dbHelper to assist this class.</p> */
	private DatabaseHelper dbHelper;

	/**
	 * <p>Constructor: instantiates a DatabaseHelper for the context.</p>
	 * @param context
	 * 			the current context.
	 */
	public Database(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	/**
	 * <p>Creates a table with patient details</p>
	 * 
	 * @see DatabaseHelper createPatientDetailsTable
	 */
	public void createPatientDetailsTable() {
		this.dbHelper.createPatientDetailsTable();
	}

	/**
	 * <p>Deletes the patient from the database.
	 * 
	 * @param patientId
	 * 			the patientId to the patient
	 *          wanting to be deleted.
	 */
	public void deletePatient(long patientId) {
		String mKey = "id";
		String mName = Long.toString(patientId);
		this.dbHelper.deletePatient(mKey, mName);
	}

	/**
	 * <p>Adds patient details</p>
	 * 
	 * @see class DatabaseHelper method addPatientDetails
	 * @param attributes
	 * 			the ArrayList of attributes to be stored for the patient
	 */
	public void addPatientDetails(ArrayList<PatientAttribute> attributes) throws Exception {
		this.dbHelper.addPatientDetails(attributes);
	}

	/**
	 * <p>Updates the patients details</p>
	 * 
	 * @see class DatabaseHelper method updatePatientDetails
	 * @param patientId
	 * 			Id to the patient to update
	 * @param attributes
	 * 			patient details to be added for the patient
	 */
	public void updatePatientDetails(long patientId,
			ArrayList<PatientAttribute> attributes) throws Exception {
		this.dbHelper.updatePatientDetails(patientId, attributes);
	}

	/**
	 * <p>Retrieves the patients age</p>
	 * 
	 * @see class DatabaseHelper method getPatientAge
	 * @param patientId
	 * 			Id to the patient to retrieve age
	 * @return
	 * 		the patients age in years
	 */
	public float getPatientAge(long patientId) {
		return this.dbHelper.getPatientAge(patientId);
	}

	/**
	 * <p>Retrieves all patients details</p>
	 * 
	 * @see class DatabaseHelper method getAllPatientDetailRecords
	 * @return
	 * 		all patients details
	 */
	public ArrayList<PatientDetails> getAllPatientDetailRecords() {
		return dbHelper.getAllPatientDetailRecords();
	}

	/**
	 * <p>Checks that the username and password being used
	 * to login are correct.</p>
	 * 
	 * @see class DatabaseHelper method getPasswordForUser(User user)
	 * @param user
	 * 			the username of the user
	 * @param password
	 * 			the password entered by the user
	 * @return
	 * 		true if the user and password are correct, false otherwise
	 */
	public boolean authenticateUser(String user, String password) throws Exception {
		try {
			String pwd = dbHelper.getPasswordForUser(user);
			return pwd.equals(password);
		} catch (Exception e) {
			throw new Exception("User does not exist.");
		}
	}

	/**
	 * <p>Attempts to add the new user to the database.
	 * If the confirm password isn't the same as the password
	 * entered, throws an exception.</p>
	 * 
	 * @see class DatabaseHelper method addNewUser(...,...)
	 * @param username
	 * 			The name the new user will use to login
	 * @param password
	 * 			The password the new user will use to login
	 * @param confirmPassword
	 * 			The password re-entered to check against first password
	 * @throws Exception
	 * 			Exception if the password != confirmPassword 
	 */
	public void addUser(String username, String password, String confirmPassword)
			throws Exception {
		dbHelper.addNewUser(username, password);
	}
    
	/**
	 * <p>Adds a new assessment being conducted by the user</p>
	 * 
	 * @see class DatabaseHelper method addAssessment
	 * @param assessment
	 * 			the new assessment the user is conducting
	 * @param u
	 * 		the user that is starting a new assessment
	 */
	public void addAssessment(Assessment assessment, User u) {
		dbHelper.addAssessment(assessment, u);
	}
    
	/**
	 * <p>Get the patient's answers from the previous assessment</p>
	 * 
	 * @see getSpecificAnswerForPatient
	 * @param patient
	 * @return
	 */
	public ArrayList<Answer> getLastAnswersForPatient(PatientDetails patient) {
		return dbHelper.getSpecificANswerForPatient(patient.getPatientID(), -1);
	}

	public String getLastAnswerDateForPatient(PatientDetails patient) {
		return dbHelper.getLastAnswerDateForPatient(patient.getPatientID());
	}

	public int getUserId(String name) {
		try {
			return dbHelper.getUserId(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public ArrayList<Integer> getAssessmentListForPatient(long patientId) {
		return dbHelper.getAssessmentListForPatient(patientId);
	}

	public ArrayList<String> getIllnessListForPatient(long patientId) {
		return dbHelper.getIllnessListForPatient(patientId);
	}

	public ArrayList<Answer> getSpecificANswerForPatient(long patientId,
			int assessmentId) {
		return dbHelper.getSpecificANswerForPatient(patientId, assessmentId);
	}

	public QueryResultTable executeSQLstatement(String statement) {
		return dbHelper.executeSQLstatement(statement);
	}
}
