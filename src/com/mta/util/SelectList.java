package com.mta.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.main.R;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class SelectList {

	Boolean use_history = false;
	Boolean use_favourites = false;
	Boolean use_service = false;
	String WS_url;
	String history_tag;
	String key_column;
	int result_limit = 0;
	int history_limit = 100;
	int favourite_limit = 100;
	int history_threshold = 1;
	String[] result_keys;
	String first_item_description = null;
	ArrayList<Map<String,String>> result_set;
	Context context;

	public SelectList(Context ctx){
		context = ctx;
	}
	
	public void setUseHistory(){
		use_history = true;
	}
	
	public void setUseFavourites(){
		use_favourites = true;
	}
	
	public void setUseWebService(){
		use_service = true;
	}
	
	public void setWSLink(String link){
		String siteUrl =context.getString(R.string.url_site);
		WS_url = siteUrl+link;
	}
	
	public void setHistroyTag(String ht){
		history_tag = ht;
	}
	
	public void setResultLimit(int lim){
		result_limit = lim;
	}
	
	public void setHistoryLimit(int lim){
		history_limit = lim;
	}
	
	public void setFavouriteLimit(int lim){
		favourite_limit = lim;
	}
	
	public void setHistoryThreshold(int thresh){
		history_threshold = thresh;
	}

	public void setKeys(String[] keys){
		result_keys = keys;
	}
	
	public void setKeyColumn(String column){
		key_column = column;
	}

	public void setFirstItemAsDescriptor(String description) {
		first_item_description = description;
		
	}
	
	
	public ArrayList<Map<String,String>> getSelectList(){
		if(result_set != null){
			if(use_history){addInitialHistory();}
			return result_set;
		}
		result_set = new ArrayList<Map<String,String>>();
		if(first_item_description != null){addFirstItemDescription();}
		if(use_history){addTodayHistory();}
		if(use_favourites){addFavourites();}
		if(use_history){addRecentHistory();}
		if(use_service){addFromWebService();}
		
		if(0 < result_limit && result_limit < result_set.size()){
			return (ArrayList<Map<String, String>>) result_set.subList(0, result_limit);
		}
		return result_set;
    }

	public Map<String,String> getSelectedItem(int position){
		return result_set.get(position);
	}
	
	private void addFirstItemDescription() {
		HashMap<String,String> singleRow = new HashMap<String, String>();
    	for (int i=0; i<result_keys.length; i++){
    		if(0 < i){
    			singleRow.put(result_keys[i], "");
    		}
    		else{
    			singleRow.put(result_keys[i], first_item_description);
    		}
    	}
    	result_set.add(singleRow);
		
	}

	private void addInitialHistory() {
		SelectListModel slModel = new SelectListModel(context);


		Date date= new Date();
		long timestamp = date.getTime()-86400000;
		
        String type = SelectListModel.H_TYPE;
        String ts = SelectListModel.TSTAMP;
        
    	String where = type+" = ? AND "+ts+" > ?";
        String[] whereArgs = {history_tag, String.valueOf(timestamp)};
        ArrayList<Map<String, String>> result = modifyResult(slModel.getItems(null, where, whereArgs, ts));
        if(result==null){return;}

        for (int i = 0; i < result.size(); i++) {
        	Map<String, String> rec = result.get(i);
	    	if(result_set.contains(rec)){
	    		result_set.remove(rec);
	    	}
	    	if(first_item_description == null){
	    		result_set.add(0,rec);
	    	}
	    	else{
	    		result_set.add(1,rec);
	    	}
	    }
		
	}
	private void addTodayHistory() {
		SelectListModel slModel = new SelectListModel(context);


		Date date= new Date();
		long timestamp = date.getTime()-86400000;
		
        String type = SelectListModel.H_TYPE;
        String ts = SelectListModel.TSTAMP;
        
    	String where = type+" = ? AND "+ts+" > ?";
        String[] whereArgs = {history_tag, String.valueOf(timestamp)};
        ArrayList<Map<String, String>> result = modifyResult(slModel.getItems(null, where, whereArgs, ts+" DESC"));
        if(result==null){return;}
        for (int i = 0; i < result.size(); i++) {
        	Map<String, String> rec = result.get(i);
	    	if( ! result_set.contains(rec)){
		    	result_set.add(rec);
	    	}
	    }
		
	}

	private void addFavourites() {
		// TODO Auto-generated method stub
		
	}

	private void addRecentHistory() {
		SelectListModel slModel = new SelectListModel(context);

        String count = SelectListModel.COUNT;
        String type = SelectListModel.H_TYPE;
        
    	String where = type+" = ? AND "+count+" >= ?";
        String[] whereArgs = {history_tag,Integer.toString(history_threshold)};
        ArrayList<Map<String, String>> result = modifyResult(slModel.getItems(null, where, whereArgs, count, String.valueOf(history_limit)));
        if(result==null){return;}
        for (int i = 0; i < result.size(); i++) {
        	Map<String, String> rec = result.get(i);
	    	if( ! result_set.contains(rec)){
		    	result_set.add(rec);
	    	}
	    }
	}
	
	private void addFromWebService() throws NetworkOnMainThreadException{
		Log.d("MTA","@SelectList:addFromWebService");
		HTTPRequest downloader = new HTTPRequest(context);
		String result = downloader.get(WS_url);
 	   	if(result==null){return;}
		//ArrayList<Map<String,String>> stringArray = new ArrayList<Map<String,String>>();
	  	//String keys[] = {"t_begin","t_end","duration","name"};
	  	//int views[] = {R.id.t_begin, R.id.t_end, R.id.duration, R.id.train_name};
		try {
			JSONArray jsonArray = new JSONArray(result);
		    for (int i = 0; i < jsonArray.length(); i++) {
		    	JSONObject rec = jsonArray.getJSONObject(i);
		    	//String train = rec.getString("train") != null ? rec.getString("train") : "Train from "+rec.getString("start")+" to "+rec.getString("finish")+"." ;
		    	//String train = "Train from "+rec.getString("start")+" to "+rec.getString("finish") ;
		    	HashMap<String,String> singleRow = new HashMap<String, String>();
		    	for (int j=0; j<result_keys.length; j++){
			    	singleRow.put(result_keys[j], rec.getString(result_keys[j]));
		    	}
		    	if( ! result_set.contains(singleRow)){
			    	result_set.add(singleRow);
		    	}
		    }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	public void updateHistory(int position){
		if(first_item_description != null && position==0){return;}
		//save item in history list database
		new UpdateHistoryTask().execute(position);
	}


	public void updateFavourite(int position){
		//TODO update favourites
		
	}
	
	// Uses AsyncTask to create a task away from the main UI thread.
    private class UpdateHistoryTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... vals) {
			Map<String, String> data = result_set.get(vals[0]);
			SelectListModel slModel = new SelectListModel(context);

            String count = SelectListModel.COUNT;
            String type = SelectListModel.H_TYPE;
            String[] columns = {SelectListModel.F0,
			            		SelectListModel.F1,
			            		SelectListModel.F2,
			            		SelectListModel.F3,
			            		SelectListModel.F4,
			            		SelectListModel.F5,
			            		} ;

    		ContentValues values = new ContentValues();
            
            String where = SelectListModel.H_TYPE+" = ? AND "+SelectListModel.F0+" = ?";
            String[] whereArgs = {history_tag,data.get(key_column)};
            Map<String,String> item = slModel.getItem( null, where, whereArgs);
    		
    		if(item != null){
    			int countVal = Integer.parseInt(item.get(SelectListModel.COUNT));
	    		values.put(count, countVal+1);
	    		slModel.updateItem(values, where, whereArgs);
    		}
    		else{
	    		for(int i=0; i<result_keys.length; i++) {
	    			values.put(columns[i], String.valueOf(data.get(result_keys[i])));
	    		}
	    		values.put(count, 1);
	    		values.put(type, history_tag);
				slModel.insertItem(values);
    		}
			return null;

       }
	   // onPostExecute displays the results of the AsyncTask.
	   @Override
	   protected void onPostExecute(String a) {
	   }
	}
    


	private ArrayList<Map<String, String>> modifyResult(ArrayList<Map<String, String>> items) {
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String[] columns = {SelectListModel.F0,
        		SelectListModel.F1,
        		SelectListModel.F2,
        		SelectListModel.F3,
        		SelectListModel.F4,
        		SelectListModel.F5,
        		} ;
		for (int i = 0; i < items.size(); i++) {
			Map<String, String> item = items.get(i);
			HashMap<String, String> result_item = new HashMap<String, String>();
			for(int j=0; j<result_keys.length; j++) {
				result_item.put(result_keys[j], String.valueOf(item.get(columns[j])));
			}
			result.add(result_item);
		}
		return result;
	}

}
