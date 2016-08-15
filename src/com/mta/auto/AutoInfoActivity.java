package com.mta.auto;


import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.main.R;
import com.mta.util.DownloadWebPage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AutoInfoActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_notification);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		String url = getResources().getString(R.string.url_site);
		Intent intent = getIntent();
		if(intent.hasExtra("reference")){
			url += getResources().getString(R.string.url_reference_view)+"/"+intent.getStringExtra("reference");
		}
		else{
			url += intent.getStringExtra("controller");
			url += "/"+intent.getStringExtra("view");
			String[] params = intent.getStringArrayExtra("params");
			for (int i = 0; i < params.length; i++) {
				url += "/"+params[i];
			}
		}
		new UpdateHistoryTask().execute(url);
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

		TableLayout tl = new TableLayout(this);
		JSONArray structure = new JSONArray();
		JSONObject data = new JSONObject();
    	try {
			JSONObject jsonObj = new JSONObject(result);
			structure = jsonObj.getJSONArray("structure");
			data = jsonObj.getJSONObject("data");
		} catch (Exception e) {
	    	e.printStackTrace();
        }
	    for (int i = 0; i < structure.length(); i++) {
	    	try {
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
			    		tr.addView(tv, new TableRow.LayoutParams(getResources().getInteger(R.integer.autoview_column_width_1), TableRow.LayoutParams.WRAP_CONTENT));
			    	}
			    	else if(type.equals("BUTTON")){
			    		String name = col.has("name") ? col.getString("name") : data.getString(col.getString("name_target"));
			    		String link = col.has("link") ? col.getString("link") : data.getString(col.getString("link_target"));
			    		Button btn = new Button(this);
			    		btn.setId(i+100*j);
			    		btn.setText(name);
			    		btn.setOnClickListener(this);
			    		btn.setTag(R.id.ref,link);
			    		tr.addView(btn, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
			    	}
			    }
	    		tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
	    	} catch (Exception e) {
		    	e.printStackTrace();
	        }
    	}
	    addContentView(tl, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
	    
		
		Log.i("MTA","DONE");//TODO ####
		setProgressBarIndeterminateVisibility(false);

	}

	@Override
	public void onClick(View v) {
		v.getTag(R.id.ref);

		Intent intent = new Intent(this, AutoInfoActivity.class);
        intent.putExtra("reference", v.getTag(R.id.ref).toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
		
	}
	
	
	
}







