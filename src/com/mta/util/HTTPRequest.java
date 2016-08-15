package com.mta.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.mta.main.R;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class HTTPRequest {

	private Context context;

	public HTTPRequest(Context ctx){
		context = ctx;
	}
	

	/*
	 * Given a URL, establishes an HttpUrlConnection and retrieves
	 * the web page content as a InputStream, which it returns as
	 * a string.
	 */
	public static String get(Context context, String urlstr) throws NetworkOnMainThreadException{
		URL url;
		HttpURLConnection urlConnection = null;
		try {
			url = new URL(formatUrl(context, urlstr));
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			Log.i("MTA","Url request: "+url.toString());								//####
			urlConnection = (HttpURLConnection) url.openConnection();
			Log.d("MTA","Url result code: "+urlConnection.getResponseCode());	//####
			Log.d("MTA","Url response: "+urlConnection.getResponseMessage());	//####
		} catch (EOFException e){
			e.printStackTrace();
			Log.d("MTA","retry to load page");
			get(context, urlstr);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
		Map<String, List<String>> headers = urlConnection.getHeaderFields();
		
		for(String ss : headers.keySet()){
			if(ss == "Set-Cookie"){
				for(String val : headers.get(ss)){
					if(val == null){
						Log.i("MTA", "cookie:None");
						continue;
					}
					Log.i("MTA:", "cookie:"+val);
				}
			}
		}
		try {
			InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			return readIt(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			urlConnection.disconnect();
		}
	}
	
	
	/*
	 * Given a URL, establishes an HttpUrlConnection and retrieves
	 * the web page content as a InputStream, which it returns as
	 * a string.
	 */
	public String get(String urlstr) throws NetworkOnMainThreadException{
		
		URL url;
		HttpURLConnection urlConnection = null;
		try {
			url = new URL(formatUrl(context, urlstr));
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			Log.i("MTA","Url request: "+urlstr);								//####
			urlConnection = (HttpURLConnection) url.openConnection();
			Log.d("MTA","Url result code: "+urlConnection.getResponseCode());	//####
			Log.d("MTA","Url response: "+urlConnection.getResponseMessage());	//####
		} catch (EOFException e){
			e.printStackTrace();
			Log.d("MTA","retry to load page");
			get(urlstr);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
		Map<String, List<String>> headers = urlConnection.getHeaderFields();
		
		for(String ss : headers.keySet()){
			if(ss == "Set-Cookie"){
				for(String val : headers.get(ss)){
					if(val == null){
						Log.i("MTA", "cookie:None");
						continue;
					}
					Log.i("MTA:", "cookie:"+val);
				}
			}
		}
		try {
			InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			return readIt(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			urlConnection.disconnect();
		}
    }
	
	private static String formatUrl(Context context, String urlstr) {
		if(urlstr.contains(context.getString(R.string.url_site))){
			return urlstr;
		}
		return context.getString(R.string.url_site)+urlstr;
	}

	/*
	 * Do a POST request for given url string with data included in a 
	 * string key->value Map.
	 */
	public static String post(Context context, String urlstr, Map<String, String> data) throws NetworkOnMainThreadException{
			

	    
	    Object[] keys =  data.keySet().toArray();
		String content = "";
		for(int i=0; i<keys.length; i++) {
			if(i!=0) { content += "&";	}
			try {
				content += String.valueOf(keys[i]) + "=" + URLEncoder.encode(data.get(keys[i]), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    
	    
	    URL url;
	    
	    
	    
	    
	    
	    
		try {
			url = new URL(formatUrl(context,urlstr));

			Log.i("MTA","Url request: "+url.toString());						//####
		    //HttpURLConnection urlConnection;
		    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			//urlConnection.setRequestMethod("POST");
		
		    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
			out.write(content);
			out.flush();
		    out.close();
		    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
		    String result = readIt(inputStream);
		    urlConnection.disconnect();

			Log.d("MTA","Url result code: "+urlConnection.getResponseCode());	//####
			Log.d("MTA","Url response: "+urlConnection.getResponseMessage());	//####
			
		    return result;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
    }
    
    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream) throws Exception {
        
        InputStreamReader is = new InputStreamReader(stream);
        StringBuilder sb=new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        
        while(read != null) {
            //System.out.println(read);
            sb.append(read);
            read =br.readLine();
        }

        //Log.i("MTA", "The result string is: " + sb.toString());//TODO
        String result = sb.toString();
        getResultError(result);
        return result;
    }

    public static String getJson(String result,String key){
    	try {
			return new JSONObject(result).getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
    }


    public static boolean getResultStatus(String result){
    	try{
    		return Boolean.parseBoolean(getJson(result,"success"));
    	}
    	catch(Exception e){
    		return false;
    	}
    }

    public static String getResultMessage(String result){
    	String message = getJson(result,"message");
    	if(message!=null){
    		return message;
    	}
    	else{
    		return "No message avialable.";
    	}
    }
    
    private static void getResultError(String result){
    	try{
    		String error = new JSONObject(result).getString("error");
    		if(error != null){
    			Log.e("MTA","REMOTE:"+error);
    		}
    	}
    	catch(Exception e){
    	}
    }
}
