package org.get.oxicam.clinicalguide.db;

/**
 * Responsible for validating the username and password during registration.
 * 
 * @author Andrew Kim
 * @version 1.0
 */
public class RegistrationValidator {
	
	public static final int MIN_USERNAME_LENG = 3;
	public static final int MIN_PW_LENG = 3;
	
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
			throw new Exception(""); // place holder; put error message for password combination here
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
	public boolean checkUsernameComb(String username) {
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
	public boolean checkPWComb(String password) {
		for(int i = 0; i < password.length(); ++i) {
			char c = password.charAt(i);
			if(!Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
