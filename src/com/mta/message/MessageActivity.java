package com.mta.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mta.main.R;
import com.mta.util.HTTPRequest;
import com.mta.util.SelectList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class MessageActivity extends Activity {

	private SelectList station_list;
	private SelectList friend_list;
	private SelectList train_list;
	private enum message_type { FRIEND, STATION, TRAIN, PUBLIC	}
	private message_type current_message_type = message_type.FRIEND;
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_message);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		spinner = (Spinner) findViewById(R.id.spinner1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setFriend(View v){
		setProgressBarIndeterminateVisibility(true);
		clearSelected();
		v.setSelected(true);
		current_message_type = message_type.FRIEND;
		if(friend_list == null){
			new retrieveFriendList().execute();
		}
		else{
			setFriendList(friend_list.getSelectList());
		}
		
		
	}
	
	public void setStation(View v){
		setProgressBarIndeterminateVisibility(true);
		clearSelected();
		v.setSelected(true);
		current_message_type = message_type.STATION;
		if(station_list == null){
			new retrieveStationList().execute();
		}
		else{
			setStationList(station_list.getSelectList());
		}
		
		
	}
	
	public void setTrain(View v){
		setProgressBarIndeterminateVisibility(true);
		clearSelected();
		v.setSelected(true);
		current_message_type = message_type.TRAIN;
		if(train_list == null){
		new retrieveTrainList().execute();
		}
		else{
			setTrainList(train_list.getSelectList());
		}
	}
	
	public void setPublic(View v){
		clearSelected();
		v.setSelected(true);
		current_message_type = message_type.PUBLIC;
		spinner.setVisibility(View.INVISIBLE);
	}
	
	private void clearSelected(){
		//spinner.setEnabled(true);
		spinner.setVisibility(View.VISIBLE);
		ImageButton b1 = (ImageButton) findViewById(R.id.button1);
		b1.setSelected(false);
		ImageButton b2 = (ImageButton) findViewById(R.id.button2);
		b2.setSelected(false);
		ImageButton b3 = (ImageButton) findViewById(R.id.button3);
		b3.setSelected(false);
		ImageButton b4 = (ImageButton) findViewById(R.id.button4);
		b4.setSelected(false);
	}

	
    // Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveFriendList extends AsyncTask<String, Void, ArrayList<Map<String,String>>> {
	    
    	
		@Override
		protected ArrayList<Map<String,String>> doInBackground(String... vals) {

			String keys[] = {"name", "id"};
			friend_list = new SelectList(getBaseContext());
			friend_list.setUseWebService();
			friend_list.setUseHistory();
			friend_list.setKeys(keys);
			friend_list.setHistroyTag(getString(R.string.history_message_friend));
			friend_list.setKeyColumn("id");
			friend_list.setFirstItemAsDescriptor("  -- SELECT FRIEND --");
			friend_list.setWSLink("friends/all");
			return friend_list.getSelectList();

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String,String>> result) {
		   setFriendList(result);


	   }
	}
	
    // Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveStationList extends AsyncTask<String, Void, ArrayList<Map<String,String>>> {
	    
    	
		@Override
		protected ArrayList<Map<String,String>> doInBackground(String... vals) {

			String keys[] = {"name","uri","active"};
			station_list = new SelectList(getBaseContext());
			station_list.setUseWebService();
			station_list.setUseHistory();
			station_list.setKeys(keys);
			station_list.setHistroyTag(getString(R.string.history_message_station));
			station_list.setKeyColumn("name");
			station_list.setFirstItemAsDescriptor("  -- SELECT STATION --");
			station_list.setWSLink(getString(R.string.url_stations_all));
			return station_list.getSelectList();

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String,String>> result) {
		   setStationList(result);


	   }
	}
    
    
 // Uses AsyncTask to create a task away from the main UI thread.
    private class retrieveTrainList extends AsyncTask<String, Void, ArrayList<Map<String,String>>> {
	    
    	
		@Override
		protected ArrayList<Map<String,String>> doInBackground(String... vals) {

			String keys[] = {"info", "number", "name"};
			train_list = new SelectList(getBaseContext());
			train_list.setUseWebService();
			train_list.setUseHistory();
			train_list.setKeys(keys);
			train_list.setHistroyTag(getString(R.string.history_message_train));
			train_list.setKeyColumn("number");
			train_list.setFirstItemAsDescriptor("  -- SELECT TRAIN --");
			train_list.setWSLink("trains/all");
			return train_list.getSelectList();

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String,String>> result) {
		   setTrainList(result);


	   }
	}
    
    private void setFriendList(ArrayList<Map<String,String>> result){

		String keys[] = {"name"};
    	int views[] = {android.R.id.text1};
		SimpleAdapter adapter = new SimpleAdapter (this, result, android.R.layout.simple_spinner_item, keys , views );
		spinner.setAdapter(adapter);
		setProgressBarIndeterminateVisibility(false);
    	
    }
    
    private void setStationList(ArrayList<Map<String,String>> result){

		String keys[] = {"name"};
    	int views[] = {android.R.id.text1};
		SimpleAdapter adapter = new SimpleAdapter (this, result, android.R.layout.simple_spinner_item, keys , views );
		spinner.setAdapter(adapter);
		setProgressBarIndeterminateVisibility(false);
		
    	
    }
    

	private void setTrainList(ArrayList<Map<String, String>> result) {

		String keys[] = {"info"};
    	int views[] = {android.R.id.text1};
		SimpleAdapter adapter = new SimpleAdapter (this, result, android.R.layout.simple_spinner_item, keys , views );
		spinner.setAdapter(adapter);
		setProgressBarIndeterminateVisibility(false);
		
	}
    

    public void sendMessage(View v) {
		setProgressBarIndeterminateVisibility(true);
		new sendMessageTask().execute();
    }
    
 // Uses AsyncTask to create a task away from the main UI thread.
    private class sendMessageTask extends AsyncTask<String, Void, Void> {
	    
    	
		@Override
		protected Void doInBackground(String... vals) {

	    	int position = spinner.getSelectedItemPosition();
	    	if(position < 1){
	    		//TODO toast say "Please specify a receiver.";
	    		Log.i("MTA","reciever not specified to send message");//#####
		    	return null;
	    	}
			EditText mEdit   = (EditText)findViewById(R.id.editText1);
			String editText = mEdit.getText().toString();
	    	if(editText == null){
	    		//TODO toast say "Cannot send empty message.";
	    		Log.i("MTA","Cannot send empty message.");//#####
		    	return null;
	    	}
	    	String target_type;
	    	String target;
	    	Map<String, String> item;
			switch (current_message_type) {
		        case FRIEND:
		    		friend_list.updateHistory(position);
		    		item = friend_list.getSelectedItem(position);
		    		target = item.get("id");
		    		target_type = "USER";
		        	break;
		        case STATION:
		    		station_list.updateHistory(position);
		    		item = station_list.getSelectedItem(position);
		    		target = item.get("uri");
		    		target_type = "STATION";
	             	break;
		        case TRAIN:
		    		train_list.updateHistory(position);
		    		item = train_list.getSelectedItem(position);
		    		target = item.get("number");
		    		target_type = "TRAIN";
		            break;
		        case PUBLIC:
		    		target_type = "PUBLIC";
	            	target = "";
	             	break;
	            default:
	            	target_type = "USER";
	            	target = "";
			}
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("TYPE", target_type);
			data.put("TARGET", target);
			data.put("MESSAGE", editText);
			
			HTTPRequest.post(MessageActivity.this, getString(R.string.url_mesage_write), data);
			return null;

		}
		
		@Override
		protected void onPostExecute(Void result) {
			setProgressBarIndeterminateVisibility(false);
			//TODO toast message sent.
			//TODO goto messages.

		}
	}
}
