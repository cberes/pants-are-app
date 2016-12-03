package com.pantsare;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PantsAreActivity extends Activity {
	
	private final String TAG = "Pants Are";
	private final String filename = "info.txt";
	private final String keyData = "24GO434YjhrwBDirqJN1";
	
	private String user, hash;
	private ToggleButton tglBtn;
	private Button updateBtn, loginBtn;
	private EditText field;
	private Context context;
	private boolean credentialsRead;
	private Aes aes;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        aes = new Aes(keyData);
        
        tglBtn = (ToggleButton)findViewById(R.id.toggleButton1);
        updateBtn = (Button)findViewById(R.id.button1);
        field = (EditText)findViewById(R.id.editText1);
        loginBtn = (Button)findViewById(R.id.button2);
        
        credentialsRead = readCredentials();
        
        if (credentialsRead && user.length() > 0) {
	        new ReadTask(user, tglBtn).execute((Void[])null);
	        Log.i(TAG, "Started read task.");
        }

        tglBtn.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View v) {
		        if (credentialsRead && user.length() > 0 && hash.length() > 0) {
					new UpdateTask(user, hash, tglBtn.getText().toString(), context).execute((Void[])null);
			        Log.i(TAG, "Started update task, status=" + tglBtn.getText());
		        } else {
		        	Toast toast = Toast.makeText(context, "You must first log in!", Toast.LENGTH_LONG);
		        	toast.show();
		        }
			}
		});
        
        updateBtn.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View v) {
		        if (credentialsRead && user.length() > 0 && hash.length() > 0) {
					new UpdateTask(user, hash, field.getText().toString(), context).execute((Void[])null);
			        Log.i(TAG, "Started update task, status=" + field.getText());
		        } else {
		        	Toast toast = Toast.makeText(context, "You must first log in!", Toast.LENGTH_LONG);
		        	toast.show();
		        }
			}
		});
        
        loginBtn.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setClassName("com.pantsare", "com.pantsare.CredentialsActivity");
				context.startActivity(intent);
			}
		});
        
        if (!credentialsRead || user.length() == 0 || hash.length() == 0) {
        	// User has not entered his password
        	Toast toast = Toast.makeText(context, "You must log in before updating your status.", Toast.LENGTH_LONG);
        	toast.show();
        }
    }
    
    @Override
    protected void onResume() {
	    super.onResume();
	    credentialsRead = readCredentials();
        
        if (credentialsRead && user.length() > 0) {
	        new ReadTask(user, tglBtn).execute((Void[])null);
	        Log.i(TAG, "Started read task.");
        }
    }
    
    private boolean readCredentials() {
    	FileInputStream fis;
    	byte[] buffer = new byte[2];
    	int userLen, passLen;
    	user = "";
    	hash = "";
    	boolean read = false;
		try {
			fis = openFileInput(filename);
			
			// Read username length
			fis.read(buffer, 0, 2);
			userLen = Integer.parseInt(new String(buffer));
			fis.read();	// Space
			
			// Read password length
			fis.read(buffer, 0, 2);
			passLen = Integer.parseInt(new String(buffer));
			fis.read();	// Space
			
			// Read username
			buffer = new byte[userLen];
			fis.read(buffer, 0, userLen);
			user = new String(aes.decrypt(buffer));
			
			// Read password
			buffer = new byte[passLen];
			fis.read(buffer, 0, passLen);
			hash = new String(aes.decrypt(buffer));
	    	
	    	fis.close();
	    	read = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return read;
    }
}