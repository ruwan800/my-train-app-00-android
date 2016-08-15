package com.mta.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.util.Log;


/**
 * Download web page.
 *
 * @deprecated use {@link #HTTPRequest} instead.
 */
@Deprecated
public class DownloadWebPage {


    /**
	 * Given a URL, establishes an HttpUrlConnection and retrieves
     * the web page content as a InputStream, which it returns as
     * a string.
	 *
	 * @deprecated use {@link #HTTPRequest}.{@link #get()} instead.
	 */
	@Deprecated
	public String retrieve(String urlstr){
		
		URL url;
		HttpURLConnection urlConnection;
		try {
			url = new URL(urlstr);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			Log.i("MTA","Url request: "+urlstr);								//####
			urlConnection = (HttpURLConnection) url.openConnection();
			Log.d("MTA","Url result code: "+urlConnection.getResponseCode());	//####
			Log.d("MTA","Url response: "+urlConnection.getResponseMessage());	//####
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Map<String, List<String>> headers = urlConnection.getHeaderFields();
		
		for(String ss : headers.keySet()){
			if(ss == "Set-Cookie"){
				for(String val : headers.get(ss)){
					if(val == null){
						Log.i("TEMP", "None");
						continue;
					}
					Log.i("Cookie:", val);
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
/*
        try {
            inputStream = entity.getContent();
   	        // Convert the InputStream into a string
   	        String contentAsString = readIt(inputStream);
            return contentAsString;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            entity.consumeContent();
        }
		
		
		Log.d("MTA","@DownloadWebPage.retrieve");//TODO ####
		Log.i("MTA","URL:"+url);//TODO ####
		int attempts = 0;
		DefaultHttpClient client = new DefaultHttpClient();
	    final HttpGet getRequest = new HttpGet(url);
	    while(attempts < 3){
		    try {
		    	attempts++;
		        HttpResponse response = client.execute(getRequest);
		        final HttpEntity entity = response.getEntity();
		        final int statusCode = response.getStatusLine().getStatusCode();
		        if (statusCode != HttpStatus.SC_OK) { 
		            Log.i("MTA", "Download Error: " + statusCode + " URL: " + url);
		        }
		        System.out.println("Initial set of cookies:");
		        List<Cookie> cookies = client.getCookieStore().getCookies();
		        if (cookies.isEmpty()) {
		            System.out.println("None");
		        } else {
		            for (int i = 0; i < cookies.size(); i++) {
		            	Log.w("TEMP", cookies.get(i).toString());
		            }
		        }
		        
		        if (entity != null) {
		            InputStream inputStream = null;
		            try {
		                inputStream = entity.getContent();
			   	        // Convert the InputStream into a string
			   	        String contentAsString = readIt(inputStream);
		                return contentAsString;
		            } finally {
		                if (inputStream != null) {
		                    inputStream.close();
		                }
		                entity.consumeContent();
		            }
		        }
		    } catch (Exception e) {
		        // Could provide a more explicit error message for IOException or IllegalStateException
		        getRequest.abort();
		        Log.i("MTA", "Error:" + e.toString());
		    } finally {
		        if (client != null) {
		        	client.getConnectionManager().shutdown();
		        }
		    }
		}
		*/
	    //return null;
		//return null;
    }
    
    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream) throws Exception {
        
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
        return sb.toString();
    }

    
}
