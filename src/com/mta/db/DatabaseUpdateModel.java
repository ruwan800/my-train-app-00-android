package com.mta.db;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.util.HTTPRequest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DatabaseUpdateModel extends BaseModel{

	

    public static final String TABLE_NAME = "database_info";
    public static final String NAME = "name";
    public static final String LOCAL = "local";
    public static final String SERVER = "server";
	

	public DatabaseUpdateModel(Context context){
		super(context);
		Log.d("EEE","@DatabaseUpdateModel.DatabaseUpdateModel");//TODO ####
	}

	@Override
	public void tableStructure(){
		Log.d("EEE","@DatabaseUpdateModel.tableStructure");//TODO ####
		table = "database_info";
		addColumn(TYPE.TEXT, NAME, TYPE.TEXT.value(), UNIQUE);
		addColumn(TYPE.INTEGER, LOCAL, TYPE.INTEGER.value(), DEFAULT+" 0", NOT_NULL);
		addColumn(TYPE.INTEGER, SERVER, TYPE.INTEGER.value(), DEFAULT+" 1", NOT_NULL);
	}

	public boolean isUptoDate(String tableName){
		Log.d("EEE","@DatabaseUpdateModel.isUptoDate");//TODO ####
		try{
			openReadable();
			Cursor c = db.query(
				table,  											// The table to query
				new String[]{"1"},                               	// The columns to return
				NAME + " = ? AND "+ SERVER +" <= "+LOCAL,         	// The columns for the WHERE clause
				new String[]{tableName},             				// The values for the WHERE clause
			    null,                                     			// don't group the rows
			    null,                                     			// don't filter by row groups
			    null                                 				// The sort order
			    );
			return 0 < c.getCount() ? true : false ;
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return false;
	}
	
	public boolean isNeverUpdated(String tableName){
		Log.d("EEE","@DatabaseUpdateModel.isNeverUpdated");//TODO ####
		try{
			openReadable();
	
			Cursor c = db.query(
				table,  											// The table to query
				new String[]{"1"},                               	// The columns to return
				NAME + " = ? AND 0 < " + LOCAL,                     // The columns for the WHERE clause
				new String[]{tableName},                   			// The values for the WHERE clause
			    null,                                     			// don't group the rows
			    null,                                     			// don't filter by row groups
			    null                                 				// The sort order
			    );
			return 0 < c.getCount() ? false : true ;
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return true;
	}

	public boolean updateLocal(String tableName) {
		Log.d("EEE","@DatabaseUpdateModel.updateLocal");//TODO ####
		try{
			openWritable();
			Calendar cal = Calendar.getInstance();
			cal.getTimeInMillis();
			ContentValues values = new ContentValues();
			values.put(NAME, tableName);
			values.put(LOCAL, cal.getTimeInMillis());
        	db.insertWithOnConflict( table,	null, values, SQLiteDatabase.CONFLICT_REPLACE);
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return true;
	}

	public boolean updateServer(String url) {
		Log.d("EEE","@DatabaseUpdateModel.updateServer");//TODO ####
		HTTPRequest downloader = new HTTPRequest(context);
		String result = downloader.get(url);
		try {
			JSONArray jsonArray = new JSONArray(result);
		    for (int i = 0; i < jsonArray.length(); i++) {
		    	JSONObject rec = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
    			values.put(SERVER, rec.getString("timestamp"));
    			// Insert the new row, returning the primary key value of the new row
    			db.updateWithOnConflict(table, values, NAME+" = ? ", new String[]{rec.getString("name")}, SQLiteDatabase.CONFLICT_IGNORE);
    			db.insert(table, null, values);
	    	}
		 } catch (Exception e) {
				Log.w("MTA",e);
		} finally{
			close();
		}
		return true;
	}
	
}
