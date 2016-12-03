package com.pantsare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UpdateTask extends AsyncTask<Void, Void, Boolean> {

	//private final String TAG = "PantsAre ReadTask";
	private final String method = "POST";
	private final String host = "http://www.pantsare.com/update.php";
	private final int maxLength = 140;
	
	private String user, secret;
	private String status;
	private Context context;
	
	public UpdateTask(String user, String secret, String status, Context context) {
		this.user = user;
		this.secret = secret;
		this.status = status.trim();
		this.context = context;
		
		if (this.status.length() > maxLength) {
			this.status = this.status.substring(0, maxLength);
		}
	}

	private boolean update() {
		HttpURLConnection con;
		OutputStreamWriter out;
		BufferedReader in;
		String message = "";
		String line;
		StringBuilder sb;
		
		try {
			message = "user=" + URLEncoder.encode(user, "UTF-8") + "&secret="
					+ URLEncoder.encode(secret, "UTF-8") + "&status="
					+ URLEncoder.encode(status, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			con = (HttpURLConnection) new URL(host).openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
   			con.setRequestProperty("Content-Length", "" + message.getBytes("UTF-8").length);
   			con.setRequestProperty("Content-Language", "en-US");  
   			con.setUseCaches(false);
   			con.setAllowUserInteraction(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			out = new OutputStreamWriter(con.getOutputStream());
			
			// Send message
			out.write(message);
			out.flush();
			out.close();

			// Sends request
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			// Read reply
			sb = new StringBuilder();
		    while ((line = in.readLine()) != null) {
		    	sb.append(line + '\n');
		    }
		    in.close();
		    message = sb.toString();
		    
		    //return readStatus(message);
		    return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		return update();
	}

	@Override
    protected void onPostExecute(Boolean arg) {
        Toast toast;
        if (arg.booleanValue()) {
        	toast = Toast.makeText(context, "Status updated.", Toast.LENGTH_SHORT);
        } else {
        	toast = Toast.makeText(context, "Error updating status!", Toast.LENGTH_LONG);
        }
        toast.show();
    }

}
