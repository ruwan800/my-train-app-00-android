package com.mta.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.auto.AutoInfoActivity;
import com.mta.main.R;
import com.mta.util.DownloadWebPage;




import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ScheduleViewActivity extends ListActivity {

	private ArrayList<String[]> refArray = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_schedule_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		
		Intent intent = getIntent();
		String siteUrl =(String) getResources().getText(R.string.url_site);
		String[] fromTo = intent.getStringArrayExtra("schedule");
		String stringUrl = siteUrl+"schedule/from/"+fromTo[2]+"/to/"+fromTo[3];
		download(stringUrl);
		
		//save item in history list database
		new UpdateHistoryTask().execute(fromTo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_schedule_view, menu);
		return true;
	}
	// Uses AsyncTask to create a task away from the main UI thread.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	       	
       @Override
	   protected String doInBackground(String... urls) {
    	   DownloadWebPage downloader = new DownloadWebPage();
    	   return downloader.retrieve(urls[0]);
       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(String result) {
	    	viewResult(result);
	  }
	}
    
    // Uses AsyncTask to create a task away from the main UI thread.
    private class UpdateHistoryTask extends AsyncTask<String, Void, String> {
	       	
		@Override
		protected String doInBackground(String... vals) {
			ScheduleHistoryModel shModel = new ScheduleHistoryModel(getBaseContext());
			
            String fromTo = ScheduleHistoryModel.FROM_TO;
            String fromStation = ScheduleHistoryModel.FROM_STATION;
            String toStation = ScheduleHistoryModel.TO_STATION;
            String fromUri = ScheduleHistoryModel.FROM_STATION_URI;
            String toUri = ScheduleHistoryModel.TO_STATION_URI;
            String count = ScheduleHistoryModel.COUNT;
            
    		Map<String,String> map = new HashMap<String,String>();
			map.put(fromTo, vals[2]+"-"+vals[3]);
			map.put(fromStation, vals[0]);
			map.put(toStation, vals[1]);
			map.put(fromUri, vals[2]);
			map.put(toUri, vals[3]);
			map.put(count, String.valueOf(Integer.parseInt(vals[4]) +1));
			shModel.insertItem(map);
			return null;

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(String a) {
	   }
	}
    
    private void download(String url){
    	// Do HTTP request
        ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadWebpageTask().execute(url);
        } else {
            //textView.setText("No network connection available.");
        	Log.d("D", "No network connection available.");//####
        }
    }

    private void viewResult(String result){
    	
    	ArrayList<Map<String,String>> stringArray = new ArrayList<Map<String,String>>();
    	String keys[] = {"t_begin","t_end","duration","name"};
    	int views[] = {R.id.t_begin, R.id.t_end, R.id.duration, R.id.train_name};
		try {
			JSONArray jsonArray = new JSONArray(result);
		    for (int i = 0; i < jsonArray.length(); i++) {
		    	JSONObject rec = jsonArray.getJSONObject(i);
		    	//String train = rec.getString("train") != null ? rec.getString("train") : "Train from "+rec.getString("start")+" to "+rec.getString("finish")+"." ;
		    	String train = "Train from "+rec.getString("start")+" to "+rec.getString("finish") ;
		    	HashMap<String,String> singleRow = new HashMap<String, String>();
		    	String values[] = {rec.getString("begin_time"),rec.getString("end_time"),rec.getString("duration"), train};//TODO find duration
		    	for (int j=0; j<views.length; j++){
			    	singleRow.put(keys[j], values[j]);
		    	}
				stringArray.add(singleRow);
				this.refArray.add(new String[]{rec.getString("ref1"), rec.getString("ref2")});
    		}
	    } catch (Exception e) {
	    	e.printStackTrace();
        }

		SimpleAdapter adapter = new SimpleAdapter (this, stringArray, R.layout.schedule_list_item, keys , views );
		setListAdapter(adapter);
		
		setProgressBarIndeterminateVisibility(false);
    }

	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		String[] ref = refArray.get(position);
		
		Intent intent = new Intent(this, AutoInfoActivity.class);
        intent.putExtra("controller", "schedule");
        intent.putExtra("view", "info");
		intent.putExtra("params", new String[]{ref[0],ref[1]});
        startActivity(intent);
		
    }
}
