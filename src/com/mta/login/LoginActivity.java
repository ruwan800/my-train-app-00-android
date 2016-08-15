package com.mta.login;

import java.util.HashMap;
import java.util.Map;

import com.mta.main.InitService;
import com.mta.main.R;
import com.mta.util.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	boolean retry = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		setProgressBarIndeterminateVisibility(false);
		
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onBackPressed() {
		  Intent intent = new Intent(Intent.ACTION_MAIN);
		  intent.addCategory(Intent.CATEGORY_HOME);
		  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
		  startActivity(intent);
		  finish();
		  System.exit(0);
	}
	
	public void registerUser(View v){
		setProgressBarIndeterminateVisibility(true);
		if(!retry){
			new registerUserTask().execute();
		}
		else{
		   Button button = (Button) findViewById(R.id.register);
		   button.setTextColor(Color.parseColor("#000000"));
		   button.setText("Retrying to Login...");
		   new registerUserTask().execute();
		}
	}
	
	
	// Uses AsyncTask to create a task away from the main UI thread.
    private class registerUserTask extends AsyncTask<String, Void, Boolean> {
	    
    	
		@Override
		protected Boolean doInBackground(String... vals) {
			Context context = LoginActivity.this;
			Map<String,String> userData = new HashMap<String,String>();
			EditText et1 = (EditText) findViewById(R.id.username);
			EditText et2 = (EditText) findViewById(R.id.email);
			String user = et1.getText().toString();
			String email = et2.getText().toString();
			String unique = Login.getUniquePsuedoID();
			

			SharedPreferences sharedPref = context.getSharedPreferences(
					context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.user_name), user);
			editor.putString(getString(R.string.user_email), email);
			editor.putString(getString(R.string.user_unique_key), unique);
			editor.commit();
			
			userData.put(context.getString(R.string.user_name), user);
			userData.put(context.getString(R.string.user_email), email);
			userData.put(context.getString(R.string.user_unique_key), unique);
			
			return Login.register(LoginActivity.this, userData);
			

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(Boolean result) {
		   setProgressBarIndeterminateVisibility(false);
		   if(result){
			   Intent intent = new Intent(LoginActivity.this, InitService.class);
			   startService(intent);
			   LoginActivity.this.finish();
		   }
		   else{
			   Button button = (Button) findViewById(R.id.register);
			   button.setTextColor(Color.parseColor("#FF0000"));
			   button.setText("Login Faild! Retry?");
		   }
	   }
	   
	}

}
