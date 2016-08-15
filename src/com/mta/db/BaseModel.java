package com.mta.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mta.util.DatabaseInfo;
import com.mta.util.HTTPRequest;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseModel{
	protected enum TYPE {
		INTEGER("INTEGER"),
		TEXT("TEXT"),
		BOOLEAN("BOOLEAN"),
		REAL("REAL"),
		TIMESTAMP("INTEGER");
		private final String value;
		TYPE(String x){
			this.value = x;
		}
		public String value() {
			return value;
		}
	}
	//protected static String INTEGER = "INTEGER";
	//protected static String TEXT = "TEXT";
	//protected static String BOOLEAN = "BOOLEAN";
	//protected static String REAL = "REAL";
	//protected static String TIMESTAMP = "INTEGER";
	protected static String PRIMARY = "PRIMARY KEY";
	protected static String NOT_NULL = "NOT NULL";
	protected static String UNIQUE = "UNIQUE";
	protected static String AI = "AUTOINCREMENT";
	protected static String DEFAULT = "DEFAULT";

	protected String ID = BaseContract.Model._ID;
	protected BaseHelper helper;
	protected Context context;
	protected SQLiteDatabase db;
	protected ArrayList<Column> columns = new ArrayList<Column>();
	protected static String table = "TABLE";
	
	class Column {
	       private TYPE type;
	       private String[] params;
	   public Column(TYPE t_type, String... cols) {
	      this.type = t_type;
	      this.params = cols;
	   }
	   public TYPE getType(){
		   return type;
	   }
	   public String[] getParams(){
		   return params;
	   }
	   public String getColumnName(){
		   return params[0];
	   }
	}
	
	
	public BaseModel(Context context){
		
		
		Log.d("EEE","@BaseModel.ModelBase");//TODO ####
		tableStructure();
		this.helper = new BaseHelper(context);
		this.context = context;
	}

	public void tableStructure(){
		Log.d("EEE","@BaseModel.tableStructure");//TODO ####

	}
	
	protected void addColumn(TYPE type,String... val){
		Column col = new Column(type,val);
		columns.add(col);
	}
	
	protected void openWritable(){
		Log.i("MTA","opening db connection for writing");
		db = this.helper.getWritableDatabase();
	}

	protected void openReadable(){
		Log.i("MTA","opening db connection for reading");
		db = this.helper.getReadableDatabase();
	}

	protected void close(){
		Log.i("MTA","closing db connection");
		db.close();
	}

	protected void createTable(SQLiteDatabase db){
		Log.d("EEE","@BaseModel.createTable");//TODO ####
		String query = "CREATE TABLE " + table  + " (" ;
		query += ID + " " + TYPE.INTEGER.value() + " " + PRIMARY + " " + AI + " " + NOT_NULL + " , ";
		for (int i = 0; i < columns.size(); i++) {
			String[] col = columns.get(i).getParams();
			String qPart = "";
			for (int j = 0; j < col.length; j++) {
				qPart += " "+ col[j];
			}
			query += " "+ qPart;
			if(i< columns.size()-1){
				query += " , ";
			}
		}
		query += " ) ";
		Log.i("MTA", query);//TODO####
		try{
			db.execSQL(query);
		} catch(Exception e){
			Log.w("MTA",e);
		}
	}

	protected void deleteTable(SQLiteDatabase db){
		Log.d("EEE","@BaseModel.deleteTable");//TODO ####
		String query = "DROP TABLE IF EXISTS " + table;
		Log.i("MTA", query);//TODO####
		try{
			db.execSQL(query);
		} catch(Exception e){
			Log.w("MTA",e);
		}
	}
	
	/**
	 * Insert new item to the database.
	 *
	 * @deprecated use {@link #insertItem()} instead.
	 */
	@Deprecated
	public boolean insertItem(Map<String,String> map){
		Log.d("EEE","@BaseModel.insertItem");//TODO ####
		openWritable();
		ContentValues values = new ContentValues();
		for (int i = 0; i < columns.size(); i++) {
			TYPE type = columns.get(i).getType();
			String key = columns.get(i).getColumnName();
			if (type.equals(TYPE.TIMESTAMP)) {
				if( values.get(key) == null){
					Date date= new Date();
					long timestamp = date.getTime();
					values.put(key, timestamp);
				}
			}
			if( ! map.containsKey(key)){
				continue;
			}
			if (type.equals(TYPE.TEXT)) {
				values.put(key, map.get(key));
			}
			else if (type.equals(TYPE.BOOLEAN)) {
				values.put(key, Boolean.parseBoolean(map.get(key)));
			}
			else if (type.equals(TYPE.INTEGER)) {
				values.put(key, Integer.parseInt(map.get(key)));
			}
			else {
				values.put(key, map.get(key));//TODO
			}
		}
		try{
			// Insert the new row, returning the primary key value of the new row
			db.insertWithOnConflict( table,	null, values, SQLiteDatabase.CONFLICT_REPLACE);
		} catch(Exception e){
			Log.w("MTA",e);
			return false;
		} finally{
			db.close();
		}
		return true;
		
	}
	
	public boolean insertItem(ContentValues values){
		Log.d("EEE","@BaseModel.insertItem");//TODO ####
		openWritable();
		for (int i = 0; i < columns.size(); i++) {
			TYPE type = columns.get(i).getType();
			String key = columns.get(i).getColumnName();
			if (type.equals(TYPE.TIMESTAMP)) {
				if( values.get(key) == null){
					Date date= new Date();
					long timestamp = date.getTime();
					values.put(key, timestamp);
				}
			}
		}
		try{
			// Insert the new row, returning the primary key value of the new row
			db.insertWithOnConflict( table,	null, values, SQLiteDatabase.CONFLICT_REPLACE);
		} catch(Exception e){
			Log.w("MTA",e);
			return false;
		} finally{
			db.close();
		}
		return true;
		
	}

	public boolean insertAllItems(ArrayList<Map<String,String>> vals) {
		Log.d("EEE","@BaseModel.insertAllItems");//TODO ####
		openWritable();
		ContentValues values = new ContentValues();
		Map<String,String> map = new HashMap<String,String>();
		for (int i = 0; i < vals.size(); i++) {
			map.clear();
			map.putAll(vals.get(i));
			values.clear();
			for (int j = 0; j < columns.size(); j++) {
				TYPE type = columns.get(j).getType();
				String key = columns.get(j).getColumnName();
				if( ! map.containsKey(key)){
					continue;
				}
				if (type.equals(TYPE.TEXT)) {
					values.put(key, map.get(key));
				}
				else if (type.equals(TYPE.BOOLEAN)) {
					values.put(key, Boolean.parseBoolean(map.get(key)));
				}
				else if (type.equals(TYPE.INTEGER)) {
					values.put(key, Integer.parseInt(map.get(key)));
				}
				else {
					values.put(key, map.get(key));//TODO
				}
			}
			// Insert the new row, returning the primary key value of the new row
			try{
				db.insert(table, null, values);
			} catch(Exception e){
				Log.w("MTA",e);
			}
		}
		close();
		return true;
	}

	public ArrayList<Map<String,String>> getItems(String[] fields, String where, String[] whereArgs, String sortBy, String limit){
		Log.d("EEE","@BaseModel.getItems");//TODO ####
		ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();
		try{
			openReadable();
			Cursor c = db.query(
				table,  											// The table to query
				fields,                         					// The columns to return
				where,                         						// The columns for the WHERE clause
				whereArgs,                   						// The values for the WHERE clause
			    null,                                     			// don't group the rows
			    null,                                     			// don't filter by row groups
			    sortBy,                                 			// The sort order
			    limit												// Set limit
			    );

			if (c.moveToFirst()) {
	            while (c.isAfterLast() == false) {
					Map<String,String> row = new HashMap<String,String>();
					if(fields == null){
						fields = c.getColumnNames();
					}
	            	for (int i = 0; i < fields.length; i++) {
						row.put(fields[i], c.getString(i));
					}
	                result.add(row);
	                c.moveToNext();
	            }
	        }
		    return result;
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return null;
	    
	}
	
	public ArrayList<Map<String,String>> getItems(String[] fields, String where, String[] whereArgs, String sortBy){
		return getItems(fields, where, whereArgs, sortBy, null);
	}
	
	public Map<String,String> getItem(String[] fields, String where, String[] whereArgs){
		ArrayList<Map<String,String>> result = getItems(fields, where, whereArgs, null);
		if(0 < result.size()){
			return result.get(0);
		}
		else{
			return null;
		}
	}
	
	public boolean updateItem(ContentValues values, String where, String[] whereArgs){
		Log.d("EEE","@BaseModel.insertItem");//TODO ####
		openWritable();
		for (int i = 0; i < columns.size(); i++) {
			TYPE type = columns.get(i).getType();
			String key = columns.get(i).getColumnName();
			if (type.equals(TYPE.TIMESTAMP)) {
				if( values.get(key) == null){
					Date date= new Date();
					long timestamp = date.getTime();
					values.put(key, timestamp);
				}
			}
		}
		try{
			// Insert the new row, returning the primary key value of the new row
			 db.updateWithOnConflict (table, values, where, whereArgs, SQLiteDatabase.CONFLICT_IGNORE);
		} catch(Exception e){
			Log.w("MTA",e);
			return false;
		} finally{
			db.close();
		}
		return true;
	}
	
	public void deleteAll(){
		Log.d("EEE","@BaseModel.deleteAll");//TODO ####
		openWritable();
		try{
			db.delete(table, null, null);
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			db.close();
		}
	}

	public boolean updateTable(String url) {
		Log.d("EEE","@BaseModel.updateTable");//TODO ####
		ArrayList<Map<String,String>> data = new ArrayList<Map<String,String>>();
		HTTPRequest downloader = new HTTPRequest(context);
		String result = downloader.get(url);
		deleteAll();
    	try {
			JSONArray jsonArray = new JSONArray(result);
		    for (int i = 0; i < jsonArray.length(); i++) {
		    	JSONObject rec = jsonArray.getJSONObject(i);
				Map<String,String> row = new HashMap<String,String>();
				for (int j = 0; j < columns.size(); j++) {
					String key = columns.get(j).getColumnName();
					if(rec.has(key)){
						row.put(key, rec.getString(key));
					}
				}
		    	data.add(row);
	    	}
			insertAllItems(data);
			DatabaseInfo dbinfo = new DatabaseInfo(context);
			dbinfo.setUpdated(table);
		 } catch (Exception e) {
		    e.printStackTrace();
	     }
		return true;
	}

	public ArrayList<Map<String,String>> getStationsFromServer(String url){
		Log.d("EEE","@BaseModel.getStationsFromServer");//TODO ####
		ArrayList<Map<String,String>> data = new ArrayList<Map<String,String>>();
		HTTPRequest downloader = new HTTPRequest(context);
		String result = downloader.get(url);
		try {
			JSONArray jsonArray = new JSONArray(result);
		    for (int i = 0; i < jsonArray.length(); i++) {
				Map<String,String> row = new HashMap<String,String>();
		    	JSONObject rec = jsonArray.getJSONObject(i);
		    	for (int j = 0; j < columns.size(); j++) {
					String key = columns.get(j).getColumnName();
					if(rec.has(key)){
						row.put(key, rec.getString(key));
					}
				}
		    	data.add(row);
	    	}
		    return data;
		 } catch (Exception e) {
		    e.printStackTrace();
	     }
	    return null;
	}
}