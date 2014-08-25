package org.get.oxicam.clinicalguide.db;

import java.io.Serializable;
import java.util.ArrayList;

import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;

/**
 * <p>Used to keep track of patient's contact details. Can be saved.</p>
 */
public class PatientDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**<p>The Id for this patient to track in the database</p>  */
	private long patientID;
	
	/** <p>A list of the patients current attributes.</p> */
	private ArrayList<PatientAttribute> attributes;
//	private String name;
//	private String surname;
//	private String address;
//	private String mobileNumber;
//	private String homeNumber;
//	private String workNumber;
//
//	private double weight; 
//	private double height; 
//	private String gender; 
//	private String knownAliments;
//	private String birthDate;

	/**
	 * <p>Constructor
	 * Stores the list of PatientAttributes into the attributes
	 * of created instance</p>
	 * 
	 * @param attributes
	 * 			Stores the attributes of this patient
	 */
	public PatientDetails(ArrayList<PatientAttribute> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * <p>Gets this instances patient attributes</p>
	 * 
	 * @return
	 * 		this instances attributes
	 */
	public ArrayList<PatientAttribute> getAttributes() {
		return this.attributes;
	}

//	public PatientAttribute getPatientAttribute(String attributeName) {
//		return attributes.get(attributeName);
//	}
	
	/**
	 * <p>Gets the patient ID</p>
	 * 
	 * @return
	 * 		this instances patiendID
	 */
	public long getPatientID() {
		return patientID;
	}

	/**
	 * <p>Sets the patientID</p>
	 * 
	 * @param patientID
	 * 			The patientID to set to this instances patientID
	 */
	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}

	
//	public void deletePatientEntry(db, "PATIENT_ID", Long.toString(id) ) ;
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getSurname() {
//		return surname;
//	}
//
//	public void setSurname(String surname) {
//		this.surname = surname;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}
//
//	public String getMobileNumber() {
//		return mobileNumber;
//	}
//
//	public void setMobileNumber(String mobileNumber) {
//		this.mobileNumber = mobileNumber;
//	}
//
//	public String getHomeNumber() {
//		return homeNumber;
//	}
//
//	public void setHomeNumber(String homeNumber) {
//		this.homeNumber = homeNumber;
//	}
//
//	public String getWorkNumber() {
//		return workNumber;
//	}
//
//	public void setWorkNumber(String workNumber) {
//		this.workNumber = workNumber;
//	}
//
//
//	public double getWeight() {
//		return weight;
//	}
//
//	public void setWeight(double weight) {
//		this.weight = weight;
//	}
//
//	public double getHeight() {
//		return height;
//	}
//
//	public void setHeight(double height) {
//		this.height = height;
//	}
//
//	public String getGender() {
//		return gender;
//	}
//
//	public void setGender(String s) {
//		this.gender = s;
//	}
//
//	public String getKnownAliments() {
//		return knownAliments;
//	}
//
//	public void setKnownAliments(String knownAliments) {
//		this.knownAliments = knownAliments;
//	}
//
//	public String getBirthDate() {
//		return birthDate;
//	}
//
//	public void setBirthDate(String birthDate) {
//		this.birthDate = birthDate;
//	}
//
//	@Override
//	public String toString()
//	{
//		String recordString = patientID + ", " +
//				name + ", " +
//				surname + ", " +
//				address + ", " +
//				mobileNumber + ", " +
//				homeNumber + ", " +
//				workNumber + ", " +
//				weight + ", " +
//				height + ", " +
//				gender + ", " +
//				knownAliments;
//		return recordString;
//	}
}
