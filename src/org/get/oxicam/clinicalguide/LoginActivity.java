package org.get.oxicam.clinicalguide;

import org.get.oxicam.clinicalguide.db.Database;
import org.get.oxicam.clinicalguide.db.Validator;
import org.get.oxicam.clinicalguide.xml.data.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The WelcomeActivity is shown on app startup. It displays the message required
 * for the clinical tests.
 * 
 * @author fth
 * @version 2014-03-11
 */
public class LoginActivity extends Activity implements View.OnClickListener {
	
	// error messages to display on login screen.
	private static final String userNameError = "Username cannot be blank.";
	private static final String userPWError = "Password cannot be blank.";
	private static final String authFailError = "Username and password do not match.";
	
	private Database database;
	private static User user;
	// global toast variable to prevent flooding of toast messages
	private Toast toast;
	public static Activity mActivity;
	
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		database = new Database(this);

		// set up layout
		setContentView(R.layout.login);
		// register OnClickListener for the skip button
		findViewById(R.id.btn_login).setOnClickListener(this);
		// display keyboard:
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		// initialize the toast message
		// it's initialized here to be able to clear previous toast messsages
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mActivity=(this);
	}
	
	/**
	 * Add the text change listeners to the user name and password text areas
	 * in the login screen.
	 */
	private void initializeLoginTextChangeListener() {
		final EditText txtUser = (EditText) findViewById(R.id.txt_username);
		final EditText pwdLogin = (EditText) findViewById(R.id.pwd_login);
		
		// for username area in login screen
		txtUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	String username = txtUser.getText().toString();
            	String pw = pwdLogin.getText().toString();
            	
            	try {
            		if(txtUser.getText().toString().equals("")) {
                		txtUser.setError(userNameError);
        			} else if(database.authenticateUser(username, pw)) {
            			// exception is thrown
        			} else {
        				txtUser.setError(null);
        			}	
            	} catch (Exception e) {
            		txtUser.setError(e.getMessage());
            	}
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
				
		// for password area in login screen
		/*pwdLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	String username = txtUser.getText().toString();
            	String pw = pwdLogin.getText().toString();
            	Boolean authResult = false;
            	
            	try {
            		authResult = database.authenticateUser(username, pw);
            	} catch (Exception e) { return ; }
            	
        		if(pwdLogin.getText().toString().equals("")) {
            		pwdLogin.setError(userPWError);
    			} else if(!authResult) {
    				pwdLogin.setError(authFailError);
            	} else {
        			pwdLogin.setError(null);
        		}
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });*/
	}
	
	@Override
	public void onClick(View v) {
		// clear previous toast messages
		// Note that toast.cancel() does no work because it 
		// prevents future toast messages from showing as well
		toast.setDuration(0);
		toast.setDuration(Toast.LENGTH_SHORT);
		
		if (v == findViewById(R.id.btn_login)) {
			EditText txtUser = (EditText) findViewById(R.id.txt_username);
			EditText txtPwd = (EditText) findViewById(R.id.pwd_login);
			
			String username = txtUser.getText().toString();
			String password = txtPwd.getText().toString();
			
			try {
				// if username field is empty
				if(txtUser.getText().toString().equals("")) {
					toast.setText(userNameError);
					toast.show();
				} else if(database.authenticateUser(username, password)) {
					int id = database.getUserId(username);
					user = new User(username, id);
					Intent intent = new Intent(v.getContext(),
							ClinicalGuideActivity.class);
					startActivity(intent);
				// if password field is empty
				} else if(txtPwd.getText().toString().equals("")) {
					toast.setText(userPWError);
					toast.show();
				// else authentication failed
				} else {
					toast.setText(authFailError);
					toast.show();
				}
			} catch (Exception e) { // if the user does not exist
				toast.setText(e.getMessage());
				toast.show();
				return;
			}
		} else if (v == findViewById(R.id.add_user_button)) {
			try {
				String username = ((EditText) findViewById(R.id.username))
						.getText().toString();
				String password = ((EditText) findViewById(R.id.password))
						.getText().toString();
				String confirmPassword = ((EditText) findViewById(R.id.confirm_password))
						.getText().toString();
				
				// validate the user name and password
				Validator validator = new Validator();
				validator.validateUsername(username);
				validator.validatePassword(password, confirmPassword);
				
				database.addUser(username, password, confirmPassword);
				setContentView(R.layout.login);
				findViewById(R.id.btn_login).setOnClickListener(this);
				
				initializeLoginTextChangeListener();
			} catch (Exception e) { // validation failed
				toast.setText(e.getMessage());
				toast.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	//	initializeLoginTextChangeListener();
		getMenuInflater().inflate(R.menu.activity_clinical_guide_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_register_user) {
			setContentView(R.layout.register_user);
			
			// display username text area error message
			final Validator rv = new Validator();
			final EditText user = (EditText) findViewById(R.id.username);
			//user.requestFocus();
			user.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	            	try{
	            		rv.validateUsername(user.getText().toString());
	            		user.setError(null);
	            	} catch (Exception e) {
	            		user.setError(e.getMessage());
	            	}
	            }

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	            @Override
	            public void afterTextChanged(Editable s) {}
	        });
			
			// display password text area error message
			final EditText password = (EditText) findViewById(R.id.password);
			password.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	            	try {
	            		rv.validatePassword(password.getText().toString(), password.getText().toString());
	            		// if an error occurs, setError(null) won't trigger because an exception is thrown
	            		// in validatePassword
	            		user.setError(null);
	            	} catch (Exception e) {
	            		password.setError(e.getMessage());
	            	}
	            }

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	            @Override
	            public void afterTextChanged(Editable s) {}
	        });
			
			// display confirm password text area error message
			final EditText confirmPassword = (EditText) findViewById(R.id.confirm_password);
			confirmPassword.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	            	if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
	            		confirmPassword.setError("Passwords do not match.");
	            	}
	            }

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	            @Override
	            public void afterTextChanged(Editable s) {}
	        });
			
			findViewById(R.id.add_user_button).setOnClickListener(this);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	public static User getUser() {
		return user;
	}
	public static Activity getActivity() {
		return mActivity;
	}
}
