package com.mta.main;

import java.util.HashMap;
import java.util.Map;

import com.mta.location.CheckInActivity;
import com.mta.location.LocationRecordService;
import com.mta.login.LoginActivity;
import com.mta.message.MessageActivity;
import com.mta.notification.NotificationActivity;
import com.mta.schedule.ScheduleActivity;
import com.mta.util.Login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayShowHomeEnabled(true);

		Login.setCookies(this);
		if(! Login.isRegistered(this)){
			Log.d("MTA","trying to initially Logging now");
			Intent LoginIntent = new Intent(this, LoginActivity.class);
			startActivity(LoginIntent);
		}
		else if(! Login.isLoggedIn(this)){
			new registerUserTask().execute();
		}
		else{
			Intent intent = new Intent(this, InitService.class);
			startService(intent);
			
			Intent locationIntent = new Intent(this, LocationRecordService.class);
			startService(locationIntent);
			
			//Intent intentDUS = new Intent(this, DatabaseUpdateService.class);
			//startService(intentDUS);
		}
		
		

		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_notification:
	    		Intent notificationIntent = new Intent(this, NotificationActivity.class);
			    startActivity(notificationIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void startActivity(View view) {
		switch(view.getId()) {
			case R.id.action_message:
				Intent messageIntent = new Intent(this, MessageActivity.class);
			    startActivity(messageIntent);
			    break;
			case R.id.action_checkin:
				Intent checkInIntent = new Intent(this, CheckInActivity.class);
			    startActivity(checkInIntent);
			    break;
			case R.id.action_schedule:
				Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
			    startActivity(scheduleIntent);
			    break;
			default:
			    break;
		}
	}
	
	// Uses AsyncTask to create a task away from the main UI thread.
    private class registerUserTask extends AsyncTask<String, Void, Boolean> {
	    
    	
		@Override
		protected Boolean doInBackground(String... vals) {
			
			Map<String,String> userData = new HashMap<String,String>();
			SharedPreferences sharedPref = getSharedPreferences(
					getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
			String user = sharedPref.getString(getString(R.string.user_name), null);
			String email = sharedPref.getString(getString(R.string.user_email), null);
			String unique = sharedPref.getString(getString(R.string.user_unique_key), null);

			userData.put(getString(R.string.user_name), user);
			userData.put(getString(R.string.user_email), email);
			userData.put(getString(R.string.user_unique_key), unique);

			return Login.register(MainActivity.this, userData);
			
       }
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(Boolean result) {
		   setProgressBarIndeterminateVisibility(false);
		   if(! result){
				Log.d("MTA","something went wrong. we need to reregister");
				Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(LoginIntent);
		   }
		}
	}
	
	
}
