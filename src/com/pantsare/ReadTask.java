package com.pantsare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.widget.ToggleButton;

public class ReadTask extends AsyncTask<Void, Void, Boolean> {
	
	//private final String TAG = "PantsAre ReadTask";
	private final String method = "GET";
	private final String host = "http://www.pantsare.com/show.php";
	private final String positiveText = "ON";
	
	private String user;
	private ToggleButton tglBtn;
	
	public ReadTask(String user, ToggleButton tglBtn) {
		this.user = user;
		this.tglBtn = tglBtn;
	}
	
	private boolean getUserStatus() {
		HttpURLConnection con;
		BufferedReader in;
		String message, line;
		StringBuilder sb;
		
		try {
			con = (HttpURLConnection) new URL(host + "?user=" + URLEncoder.encode(user, "UTF-8")).openConnection();
			con.setRequestMethod(method);
			con.setDoInput(true);
			
			// Sends request
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    sb = new StringBuilder();
		    while ((line = in.readLine()) != null) {
		    	sb.append(line + '\n');
		    }
		    in.close();
		    message = sb.toString();
		    
		    return readStatus(message);			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean readStatus(String message) {
		boolean status = true;
		String value;
		Pattern pattern = Pattern.compile("<status>(.+?)</status>");
		Matcher matcher = pattern.matcher(message);
		
		if (matcher.find()) {
			if (matcher.groupCount() > 0) {
				value = matcher.group(1);
				status = value.trim().equalsIgnoreCase(positiveText);
			}
		}
		
		return status;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		return getUserStatus();
	}

	@Override
    protected void onPostExecute(Boolean arg) {
        tglBtn.setChecked(arg);
    }

}
