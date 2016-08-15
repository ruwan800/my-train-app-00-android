package com.mta.location;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.mta.main.R;
import com.mta.util.HTTPRequest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class LocationRecordService extends Service implements LocationListener{
	
	LocationManager locationManager;
	
	public void requestLocationUpdates() {
		int sampleDistance = 1* 1000;
		int sampleInterval =  1 * 1000 * 60;
		Log.i("MTA", "Setting up location updates with sample distance " + sampleDistance + " m and sample interval "
				+ sampleInterval + " ms.");

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> enabledProviders = locationManager.getProviders(true);
		for (String provider:enabledProviders){
			Log.i("MTA", "Requesting location updates from provider " + provider);
			locationManager.requestLocationUpdates(provider, sampleInterval, sampleDistance, this);
		}
	}
	


	
	
	

	@Override
	public void onLocationChanged(Location location) {
		Log.w("MTA", String.valueOf(location.getLatitude())+" : "+String.valueOf(location.getLongitude()));
		
		DateFormat df = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss",Locale.US);
		Date today = Calendar.getInstance().getTime();
		String reportTime = df.format(today);

		String url = getString(R.string.url_site)+getString(R.string.url_location)
						+"/"+String.valueOf(location.getLatitude())+"/"
						+String.valueOf(location.getLongitude())+"/"+reportTime;
		
		SharedPreferences sharedPref = getSharedPreferences(
				getString(R.string.http_cookie_file_key), Context.MODE_PRIVATE);
	
		String siteUrl = getString(R.string.url_site);
		String siteDomain = getString(R.string.domain_site);
		URI uri = null;
		try {
			uri = new URI(siteUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		CookieManager cookieManager = new CookieManager( null, CookiePolicy.ACCEPT_ALL );
	
		for( String key :sharedPref.getAll().keySet()){
			HttpCookie cookie = new HttpCookie(key, sharedPref.getString(key, null));
			cookie.setDomain(siteDomain);
			cookie.setPath("/");
			cookie.setVersion(0);
			cookieManager.getCookieStore().add(uri, cookie);
		}
		CookieHandler.setDefault( cookieManager );
		
		new DownloadWebpageTask().execute(url);
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("MTA", "onProviderDisabled");
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("MTA", "onProviderEnabled");
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("MTA", "onStatusChanged");
		
	}







	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MTA", "onStartCommand()");
		super.onStartCommand(intent, flags, startId);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.i("MTA", "onCreate()");

		requestLocationUpdates();



	}

	@Override
	public void onDestroy() {
		Log.i("MTA", "onDestroy()");
		super.onDestroy();
		locationManager.removeUpdates(this);
	}
	
	
	// Uses AsyncTask to create a task away from the main UI thread.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	       	
       @Override
	   protected String doInBackground(String... urls) {
			ConnectivityManager connMgr = (ConnectivityManager) 
			getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				return HTTPRequest.get(LocationRecordService.this, urls[0]);
			} else {
				return "Not connected to internet";
			}
       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(String result) {
			Log.i("MTA", "updated location at the server");
			try {
				Log.w("MTA", result);
			} catch (Exception e) {
				Log.i("MTA", "Err:"+e);
			}
	  }
	}
	
	
}