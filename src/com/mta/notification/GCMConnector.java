package com.mta.notification;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mta.main.R;
import com.mta.util.HTTPRequest;

public class GCMConnector {

    //private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID;

    

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    String TAG = "MTA";

    public GCMConnector(Context ctx){
    	Log.d(TAG, "@GCMConnector");	//####
    	context = ctx;
    	SENDER_ID = context.getString(R.string.gcm_project_id);
    }
    
    // Check device for Play Services APK. If check succeeds, proceed with
    //  GCM registration.
    public boolean isPlayServicesAvailable(){
    	Log.d(TAG, "@GCMConnector::isPlayServicesAvailable");	//####
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, context.,
                //		PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MTA", "This device is not supported.");
            }
            return false;
        }
        return true;
        	
	}
	
	public boolean isRegistered() {
    	Log.d(TAG, "@GCMConnector::isRegistered");	//####
		regid = getRegistrationId();

        if (regid.isEmpty()) {
        	return false;
        }
        return true;
	}


	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId() {
    	Log.d(TAG, "@GCMConnector::getRegistrationId");	//####
	    final SharedPreferences prefs = getGCMPreferences();
	    String registrationId = prefs.getString(context.getString(R.string.property_registration_id), "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(context.getString(R.string.property_app_version), Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences() {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return context.getSharedPreferences(context.getString(R.string.gcm_registration),
	            Context.MODE_PRIVATE);
	}
	
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private int getAppVersion(Context context) {
    	Log.d(TAG, "@GCMConnector::getAppVersion");	//####
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public String register(){
    	Log.d(TAG, "@GCMConnector::register");	//####
		String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
            storeRegistrationId(context, regid);
            sendRegistrationIdToBackend();
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
	}
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
    	Log.d(TAG, "@GCMConnector::sendRegistrationIdToBackend");	//####
	    HTTPRequest downloader = new HTTPRequest(context);
		//TODO make this as a post request
	    String result = downloader.get(context.getString(R.string.url_user_gcm_id)+"/"+regid);
	    SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPref.edit();
	    if(HTTPRequest.getResultStatus(result)){
			editor.putString(context.getString(R.string.user_gcm_success), "true");
	    }else{
			editor.putString(context.getString(R.string.user_gcm_success), "false");
	    }
		editor.commit();
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
    	Log.d(TAG, "@GCMConnector::storeRegistrationId");	//####
	    final SharedPreferences prefs = getGCMPreferences();
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(context.getString(R.string.property_registration_id), regId);
	    editor.putInt(context.getString(R.string.property_app_version), appVersion);
	    editor.commit();
	}
}
