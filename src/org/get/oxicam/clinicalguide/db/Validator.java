package org.get.oxicam.clinicalguide.db;

import java.util.ArrayList;

import org.get.oxicam.clinicalguide.xml.data.PatientAttribute;

/**
 * Responsible for validating the username and password during registration.
 * 
 * @author Andrew Kim
 * @version 1.0
 */
public class Validator {
	
	public static final int MIN_USERNAME_LENG = 3;
	public static final int MIN_PW_LENG = 3;
	
	public Validator() {
		
	}
	
	/**
	 * Validate the given user name.
	 * 
	 * @param username - The user name to validate.
	 * @throws Exception
	 * 			Exception if the username length is < MIN_USERNAME_LENG
	 * 			Exception if the username does not meet the combination requirement.
	 */
	public void validateUsername(String username) throws Exception {
		if(username.length() < MIN_USERNAME_LENG) {
			throw new Exception("User name must be at least " + MIN_USERNAME_LENG + " characters long.");
		} else if(!checkUsernameComb(username)) {
			throw new Exception("User name must only consist of alphabets and numbers.");
		}
	}
	
	/**
	 * Validate the given password.
	 * 
	 * @param password - The password chosen by the user.
	 * @param confirmPassword - The password confirmed by the user.
	 * @throws Exception
	 * 			Exception if the password != confirmPassword.
	 * 			Exception if the password length is < MIN_PW_LENG.
	 * 			Exception if the password does not meet the combination requirement.
	 */
	public void validatePassword(String password, String confirmPassword) throws Exception {
		if(password.length() < MIN_PW_LENG) {
			throw new Exception("Password must be at least " + MIN_PW_LENG + " characters long.");
		} else if(!checkPWComb(password)) {
			throw new Exception("Password must only consist of alphabets and numbers.");
		} else if (!password.equals(confirmPassword)) {
			throw new Exception("Passwords do not match.");
		}
	}
	
	/**
	 * Check the username for character combination.
	 * 
	 * @param username - The username to check for combination.
	 * @return False if it does not meet the requirement; true otherwise.
	 */
	private boolean checkUsernameComb(String username) {
		for(int i = 0; i < username.length(); ++i) {
			char c = username.charAt(i);
			if(!Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check the password for character combination.
	 * 
	 * @param password - The password to check for combination.
	 * @return False if it does not meet the requirement; true otherwise.
	 */
	private boolean checkPWComb(String password) {
		for(int i = 0; i < password.length(); ++i) {
			char c = password.charAt(i);
			if(!Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Validate the patient attributes.
	 * 
	 * @param attributes - The array list of attributes to validate.
	 * @throws Exception
	 * 			Exception if the first name is blank.
	 * 			Exception if the last name is blank.
	 * 			Exception if the date exceeds today's date.
	 */
	public void checkPatientAttriutes(ArrayList<PatientAttribute> attributes) throws Exception {
		if(attributes.get(0).answer.equals("")) {
			throw new Exception("First name cannot be blank.");
		} if(attributes.get(1).answer.equals("")) {
			throw new Exception("Last name cannot be blank.");
		} if(attributes.get(3).answer.equals("")) {
			throw new Exception("The date cannot exceed today's date.");
		} 
	}
}
