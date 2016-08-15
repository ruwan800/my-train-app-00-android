package com.mta.station;

import com.mta.main.R;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class StationUpdateService extends IntentService {

	  /**
	   * A constructor is required, and must call the super IntentService(String)
	   * constructor with a name for the worker thread.
	   */
	  public StationUpdateService() {
	      	super("StationUpdateService");
	  }

	  /**
	   * The IntentService calls this method from the default worker thread with
	   * the intent that started the service. When this method returns, IntentService
	   * stops the service, as appropriate.
	   */
	  @Override
	  protected void onHandleIntent(Intent intent) {
			Log.d("EEE","@StationUpdateService.onHandleIntent");//TODO ####
		  	StationModel station = new StationModel(this.getBaseContext());
			String stringUrl = getString(R.string.url_site) + getString(R.string.url_stations_all);
			station.updateTable(stringUrl);
	  }
}