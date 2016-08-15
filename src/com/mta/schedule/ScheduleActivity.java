package com.mta.schedule;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mta.main.R;
import com.mta.station.StationModel;
import com.mta.station.StationUpdateService;
import com.mta.util.DatabaseInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;

public class ScheduleActivity extends ListActivity implements OnItemSelectedListener {

	private ArrayList<String> stationUriArray = new ArrayList<String>();
	private ArrayList<String[]> historyUriArray = new ArrayList<String[]>();
	private String[] schedule = new String[5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_schedule);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		
		new checkStationUptoDateTask().execute();

		//retrieve station list from server
		//String stringUrl =(String) getResources().getText(R.string.url_site)+getResources().getText(R.string.url_stations_all);
		//download(stringUrl);
	}

	@Override
	protected void onResume(){
		super.onResume();
		//get history list from database
		new RetrieveHistoryTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_schedule, menu);
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
	
    // Check station table upto-date. uptoDate ? RetrieveStationTask : checkInitiallyUpdatedTask;
    private class checkStationUptoDateTask extends AsyncTask<String, Void, Boolean> {
	       	
		protected Boolean doInBackground(String... urls) {
			Log.d("EEE","@checkStationUptoDateTask.doInBackground");//TODO ####
			DatabaseInfo dbInfo = new DatabaseInfo(getBaseContext());
			return dbInfo.isUptoDate(getString(R.string.table_station));
        }
		// onPostExecute displays the results of the AsyncTask.
		protected void onPostExecute(Boolean result) {
			Log.d("EEE","@checkStationUptoDateTask.onPostExecute");//TODO ####
			if(result){
			   new GetStationTask().execute();
			}
			else{
			   new checkInitiallyUpdatedTask().execute();
			}
		}
	}
    
    // Check table ever updated. neverUpdated ? StationUpdateTask : RetrieveStationTask, startStationService
    private class checkInitiallyUpdatedTask extends AsyncTask<String, Void, Boolean> {
	       	
		@Override
		protected Boolean doInBackground(String... urls) {
			Log.d("EEE","@checkInitiallyUpdatedTask.doInBackground");//TODO ####
			DatabaseInfo dbInfo = new DatabaseInfo(getBaseContext());
			return dbInfo.isNeverUpdated(getString(R.string.table_station));
        }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(Boolean result) {
			Log.d("EEE","@checkInitiallyUpdatedTask.onPostExecute");//TODO ####
			if(result){
				new RetrieveStationTask().execute();
			}
			else{
		    	new GetStationTask().execute();
			}
			startStationUpdateService();
	  }
	}

    // get station list from server . next:viewResult
    private class RetrieveStationTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
	       	
		@Override
		protected ArrayList<Map<String, String>> doInBackground(String... urls) {
			Log.d("EEE","@RetrieveStationTask.doInBackground");//TODO ####
			String stringUrl = getString(R.string.url_site) + getString(R.string.url_stations_all);
		  	StationModel station = new StationModel(getBaseContext());
		  	return station.getStationsFromServer(stringUrl);
       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String, String>> result) {
			Log.d("EEE","@RetrieveStationTask.onPostExecute");//TODO ####
	    	viewResult(result);
	  }
	}
    
    // get saved station list from the database next:viewResult
    private class GetStationTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
	       	
       @Override
	   protected ArrayList<Map<String, String>> doInBackground(String... urls) {
			Log.d("EEE","@GetStationTask.doInBackground");//TODO ####
    	    StationModel station = new StationModel(getBaseContext());
			return station.getItems(new String[]{"name","uri"}, "active <> 0 ", null, "name");
       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String, String>> result) {
			Log.d("EEE","@GetStationTask.onPostExecute");//TODO ####
	    	viewResult(result);
	  }
	}
     
    // get history for schedule search.
    private class RetrieveHistoryTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
	       	
		@Override
		protected ArrayList<Map<String, String>> doInBackground(String... urls) {
			Log.d("EEE","@RetrieveHistoryTask.doInBackground");//TODO ####
			ScheduleHistoryModel shModel = new ScheduleHistoryModel(getBaseContext());
			ArrayList<Map<String, String>> historyMap = new ArrayList<Map<String, String>>();
			// Define a projection that specifies which columns from the database
			// you will actually use after this query.
			historyMap = shModel.getItems(
					new String[]{
							ScheduleHistoryModel.FROM_STATION,
							ScheduleHistoryModel.TO_STATION,
							ScheduleHistoryModel.FROM_STATION_URI,
							ScheduleHistoryModel.TO_STATION_URI,
							ScheduleHistoryModel.COUNT},
					null,
					null,
					ScheduleHistoryModel.COUNT + " DESC , "+ ScheduleHistoryModel.TIMESTAMP + " DESC "
					);
    	    return historyMap;
       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(ArrayList<Map<String, String>> result) {
			Log.d("EEE","@RetrieveHistoryTask.onPostExecute");//TODO ####
	    	viewScheduleHistory(result);
	  }
	}

    // start station table update process on a separate service
    private void startStationUpdateService(){
		Intent intent = new Intent(this, StationUpdateService.class);
		startService(intent);
    }

    // update spinner from:RetrieveStationTask
    private void viewResult(ArrayList<Map<String, String>> result){
		Log.d("EEE","@viewResult");//TODO ####
    	ArrayList<String> stringArray = new ArrayList<String>();
	    for (int i = 0; i < result.size(); i++) {
			stringArray.add(result.get(i).get("name"));
			this.stationUriArray.add(result.get(i).get("uri"));
    	}
    	String myString = getString(R.string.schedule_default_station);
		Spinner fromSpinner = (Spinner) findViewById(R.id.from_spinner);
		Spinner toSpinner = (Spinner) findViewById(R.id.to_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_dropdown_item, stringArray );
		int defaultPosition = adapter.getPosition(myString);
		fromSpinner.setAdapter(adapter);
		toSpinner.setAdapter(adapter);
		fromSpinner.setSelection(defaultPosition);
		toSpinner.setSelection(defaultPosition);
		fromSpinner.setOnItemSelectedListener(this);
		toSpinner.setOnItemSelectedListener(this);
		setProgressBarIndeterminateVisibility(false);
		
    }

    // update history list from:RetrieveHistoryTask
	private void viewScheduleHistory(ArrayList<Map<String, String>> result) {
		Log.d("EEE","@viewScheduleHistory");//TODO ####
		ArrayList<String> historyArray = new ArrayList<String>();
		this.historyUriArray.clear();
		Map<String,String> row = new HashMap<String,String>();
		for (int i = 0; i < result.size(); i++) {
			row.clear();
			row = result.get(i);
		    String[] s = new String[]{
						    	row.get(ScheduleHistoryModel.FROM_STATION),
						    	row.get(ScheduleHistoryModel.TO_STATION),
						    	row.get(ScheduleHistoryModel.FROM_STATION_URI),
						    	row.get(ScheduleHistoryModel.TO_STATION_URI),
						    	row.get(ScheduleHistoryModel.COUNT)
		    				};
			historyArray.add(s[0] +" - "+ s[1]);
			String[] fromTo = {s[0],s[1],s[2],s[3],s[4]};
			this.historyUriArray.add(fromTo);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        android.R.layout.simple_list_item_1, historyArray );
		setListAdapter(adapter);
	}

	// handle spinner click events
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (parent.getId()== R.id.from_spinner){
			this.schedule[0] = parent.getItemAtPosition(pos).toString();
			this.schedule[2] = this.stationUriArray.get(pos);
		}
		else if(parent.getId()== R.id.to_spinner){
			this.schedule[1] = parent.getItemAtPosition(pos).toString();
			this.schedule[3] = this.stationUriArray.get(pos);
		}
		this.schedule[4] = "0";
	}

	// handle spinner click events. comes default with OnItemSelectedListener
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	// handle history list click events.
	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		String[] fromTo = this.historyUriArray.get(position);
		
		Intent intent = new Intent(this, ScheduleViewActivity.class);
        intent.putExtra("schedule", fromTo);
        startActivity(intent);
    }
	
	// start schedule view activity
	public void viewSchedule(View view){
		Log.d("EEE","@viewSchedule");//TODO ####
		Intent intent = new Intent(this, ScheduleViewActivity.class);
        intent.putExtra("schedule", this.schedule);
        startActivity(intent);
        
	}

}
