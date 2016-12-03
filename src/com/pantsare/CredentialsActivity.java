package com.pantsare;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CredentialsActivity extends Activity {
	
	private final String filename = "info.txt";
	private final String keyData = "24GO434YjhrwBDirqJN1";
	private final int maxUsernameChars = 16; 
	
	private Context context;
	private EditText username, password;
	private Button save;
	private Aes aes;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        
        aes = new Aes(keyData);

        username = (EditText)findViewById(R.id.editTextUsername);
        password = (EditText)findViewById(R.id.editTextPassword);
        save = (Button)findViewById(R.id.buttonSave);
        
        save.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View v) {
				Toast toast;
				if (username.getText().toString().trim().length() <= maxUsernameChars) {
					// Valid length
					if (saveCredentials(username.getText().toString().trim(), password.getText().toString().trim())) {
						// Success
						toast = Toast.makeText(context, "Credentials saved.", Toast.LENGTH_SHORT);
					} else {
						// Failure
						toast = Toast.makeText(context, "Error saving credentials!", Toast.LENGTH_LONG);
					}
				} else {
					// Too long
					toast = Toast.makeText(context, "Username was too long!", Toast.LENGTH_LONG);
				}
				toast.show();
			}
		});
    }
    
    private boolean saveCredentials(String username, String password) {
    	FileOutputStream fos;
    	MessageDigest md5;
    	byte[] encUser, encPass;
    	BigInteger hash;
    	String header;
    	boolean saved = false;
		try {
			fos = openFileOutput(filename, MODE_PRIVATE);
			md5 = MessageDigest.getInstance("MD5");
			
			// Encrypt fields
	    	encUser = aes.encrypt(username.getBytes("UTF-8"));
	    	hash = new BigInteger(md5.digest(password.getBytes("UTF-8")));
	    	encPass = aes.encrypt(hash.toString(16).getBytes("UTF-8"));
	    	
	    	// Save fields
	    	header = encUser.length + " " + encPass.length + " ";
	    	fos.write(header.getBytes("UTF-8"));
	    	fos.write(encUser);
	    	fos.write(encPass);
	    	
	    	fos.close();
	    	saved = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return saved;
    }

}
