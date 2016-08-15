package com.mta.auto;


import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.main.R;
import com.mta.util.DownloadWebPage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AutoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_notification);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		String siteUrl =(String) getResources().getText(R.string.url_site);
		String stringUrl = siteUrl+"schedule/info/2ec0747bbe624a23ab2175adf4e9cf03/bbd7773c33774119b99d62c532e4e9a5";
		new UpdateHistoryTask().execute(stringUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_schedule_info, menu);
		return true;
	}

    // Uses AsyncTask to create a task away from the main UI thread.
    private class UpdateHistoryTask extends AsyncTask<String, Void, String> {
	       	
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
    
    private void viewResult(String result) {

		try {
			TableLayout tl = new TableLayout(this);
			JSONObject jsonObj = new JSONObject(result);
			JSONArray structure = jsonObj.getJSONArray("structure");
			JSONObject data = jsonObj.getJSONObject("data");
		    for (int i = 0; i < structure.length(); i++) {
		    	TableRow tr = new TableRow(this);
		    	tr.setId(i);
		    	JSONObject row = structure.getJSONObject(i);
		    	JSONArray columns = row.getJSONArray("columns");
			    for (int j = 0; j < columns.length(); j++) {
			    	JSONObject col = columns.getJSONObject(j);
			    	String type = col.getString("type");
			    	if(type.equals("TEXTVIEW")){
			    		String name = col.has("name") ? col.getString("name") : data.getString(col.getString("name_target"));
			    		TextView tv = new TextView(this);
			    		tv.setText(name);
			    		tv.setId(i+100*j);
			    		tr.addView(tv, new TableRow.LayoutParams(R.integer.autoview_column_width_1, TableRow.LayoutParams.WRAP_CONTENT));
			    	}
			    	else if(type.equals("BUTTON")){
			    		String name = col.has("name") ? col.getString("name") : data.getString(col.getString("name_target"));
			    		String link = col.has("link") ? col.getString("link") : data.getString(col.getString("link_target"));
			    		Button btn = new Button(this);
			    		btn.setId(i+100*j);
			    		btn.setText(name);
			    		btn.setTag(R.id.ref,link);
			    		tr.addView(btn, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
			    	}
			    }
	    		tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    		}
		    addContentView(tl, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
	    } catch (Exception e) {
	    	e.printStackTrace();
        }
		
		Log.i("MTA","DONE");//TODO ####
		setProgressBarIndeterminateVisibility(false);
    	
		
	}
	
	
	
}







