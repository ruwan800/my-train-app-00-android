package com.mta.util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.mta.main.R;
import com.mta.notification.GCMConnector;

public class Login {

	private static CookieManager cookieManager = new CookieManager( null, CookiePolicy.ACCEPT_ALL );
	
	public static void updateRegistration(Context context) throws NetworkOnMainThreadException{
		Log.d("MTA","@Login.updateRegistration");
		
		HTTPRequest downloader = new HTTPRequest(context);
		String result = downloader.get(context.getString(R.string.url_user_logincheck));
		if (HTTPRequest.getResultStatus(result)){
			Log.i("MTA","User already logged in");
			setLastLogin(context);
		}
		else{
			Log.i("MTA","User not registered");
			//register(context);
			//TODO we need to login again. but we did not implemented that.
		}
		updateCookies(context);
	}

	public static void setCookies(Context context) {
		Log.d("MTA","@Login.setCookies");
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.http_cookie_file_key), Context.MODE_PRIVATE);
	
		String siteUrl =context.getString(R.string.url_site);
		String siteDomain =context.getString(R.string.domain_site);
		URI uri = null;
		try {
			uri = new URI(siteUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	
		for( String key :sharedPref.getAll().keySet()){
			HttpCookie cookie = new HttpCookie(key, sharedPref.getString(key, null));
			cookie.setDomain(siteDomain);
			cookie.setPath("/");
			cookie.setVersion(0);
			cookieManager.getCookieStore().add(uri, cookie);
		}
		CookieHandler.setDefault( cookieManager );
	}

	public static void updateCookies(Context context) {
		Log.d("MTA","@Login.updateCookies");
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.http_cookie_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		String siteUrl =context.getString(R.string.url_site);
		URI uri = null;
		try {
			uri = new URI(siteUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		for(HttpCookie ck : cookieManager.getCookieStore().get(uri)){
			editor.putString(ck.getName(), ck.getValue());
		}
		editor.commit();
	}

	public static boolean register(Context context, Map<String,String> userData) throws NetworkOnMainThreadException {
		Log.d("MTA","@Login.register");
		
		String result = HTTPRequest.post(context, context.getString(R.string.url_user_register), userData);
		setRegistrationCompleteState(context, true);
		if (HTTPRequest.getResultStatus(result)){
			Log.i("MTA","User Login success");
			updateCookies(context);
			setLastLogin(context);
			setLoginCompleteState(context, true);
			return true;
		}
		else{
			Log.i("MTA","User Login failed");
			setLoginCompleteState(context, false);
		}
		return false;
	}
	
	public static boolean isRegistered(Context context){
		Log.d("MTA","@Login.isRegistered");
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		String result = sharedPref.getString(context.getString(R.string.user_registration_success), null);
		if(result != null && Boolean.parseBoolean(result)){
			return true;
		}
		return false;
	}
	
	public static boolean isLoggedIn(Context context){
		Log.d("MTA","@Login.isLoggedIn");
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		String result = sharedPref.getString(context.getString(R.string.user_login_success), null);
		if(result != null && Boolean.parseBoolean(result)){
			return true;
		}
		return false;
		
	}
	
	public static Date getLastLogin(Context context){

		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		String date = sharedPref.getString(context.getString(R.string.user_last_login), "20000101");
        DateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.US);
        try {
			return (Date) format.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;

	}
	

	public static void setLastLogin(Context context){
		DateFormat df = new SimpleDateFormat("yyyyMMdd",Locale.US);
		Date today = Calendar.getInstance().getTime();
		String todaystr = df.format(today);
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(context.getString(R.string.user_last_login), todaystr);
		editor.commit();
		
	}
	
	public static boolean gcmRegisterComplete(Context context){
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		String gcm_success = sharedPref.getString(context.getString(R.string.user_gcm_success), null);
		return Boolean.parseBoolean(gcm_success);
	}
	
	public static void setRegistrationCompleteState(Context context, boolean success){
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(context.getString(R.string.user_registration_success), String.valueOf(success));
		editor.commit();
	}
	
	public static void setLoginCompleteState(Context context, boolean success){
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.user_information_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(context.getString(R.string.user_login_success), String.valueOf(success));
		editor.commit();
	}
	

	public static void RegisterGCM(Context context){
		GCMConnector gcmc = new GCMConnector(context);
		if ( ! gcmc.isRegistered()) {
			gcmc.register();
        }else{
        	Log.d("MTA", "@GCMConnector::sendRegistrationIdToBackend");	//####
    	    HTTPRequest downloader = new HTTPRequest(context);
    	    SharedPreferences sharedPref0 = context.getSharedPreferences(
    				context.getString(R.string.gcm_registration), Context.MODE_PRIVATE);
    	    String registrationId = sharedPref0.getString(
    	    							context.getString(R.string.property_registration_id), null);

    		//TODO make this as a post request
    	    String result = downloader.get(context.getString(R.string.url_user_gcm_id)+"/"+registrationId);
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
	}
	
	/**
	 * Return pseudo unique ID
	 * @return ID
	 */
	public static String getUniquePsuedoID()
	{
	    // If all else fails, if the user does have lower than API 9 (lower
	    // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
	    // returns 'null', then simply the ID returned will be solely based
	    // off their Android device information. This is where the collisions
	    // can happen.
	    // Thanks http://www.pocketmagic.net/?p=1662!
	    // Try not to use DISPLAY, HOST or ID - these items could change.
	    // If there are collisions, there will be overlapping data
	    String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

	    // Thanks to @Roman SL!
	    // http://stackoverflow.com/a/4789483/950427
	    // Only devices with API >= 9 have android.os.Build.SERIAL
	    // http://developer.android.com/reference/android/os/Build.html#SERIAL
	    // If a user upgrades software or roots their phone, there will be a duplicate entry
	    String serial = null;
	    try
	    {
	        serial = android.os.Build.class.getField("SERIAL").get(null).toString();

	        // Go ahead and return the serial for api => 9
	        String uk = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
		    Log.i("MTA","UNIQUE-KEY:"+uk);
		    return uk;
	    }
	    catch (Exception e)
	    {
	        // String needs to be initialized
	        serial = "serial"; // some value
	    }

	    // Thanks @Joe!
	    // http://stackoverflow.com/a/2853253/950427
	    // Finally, combine the values we have found by using the UUID class to create a unique identifier
	    String uk = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	    Log.i("MTA","UNIQUE-KEY:"+uk);
	    return uk;
	}
}

