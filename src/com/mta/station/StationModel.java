package com.mta.station;

import com.mta.db.BaseModel;

import android.content.Context;
import android.util.Log;

public class StationModel extends BaseModel{



    
    public static final String name = "name";
    public static final String uri = "uri";
    public static final String active = "active";
	
	public StationModel(Context context) {
		super(context);
		Log.d("EEE","@StationModel.StationModel");//TODO ####
	}

	@Override
	public void tableStructure(){
		Log.d("EEE","@StationModel.tableStructure");//TODO ####
		table = "station";
		addColumn(TYPE.TEXT, name, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, uri, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, active, TYPE.BOOLEAN.value());
	}
	
	/*
	public ArrayList<String[]> getStations(){
		Log.d("EEE","@StationModel.getStations");//TODO ####
		ArrayList<String[]> stations = new ArrayList<String[]>();
		try{
			openReadable();
			Cursor c = db.query(
				table,  											// The table to query
				new String[]{name,uri},                         	// The columns to return
				active + " = 1 ",                         			// The columns for the WHERE clause
				null,                   							// The values for the WHERE clause
			    null,                                     			// don't group the rows
			    null,                                     			// don't filter by row groups
			    name                                 				// The sort order
			    );
			if (c.moveToFirst()) {
	            while (c.isAfterLast() == false) {
	                String[] fromTo = {c.getString(0), c.getString(1)};
	                stations.add(fromTo);
	                c.moveToNext();
	            }
	        }
		    return stations;
		} catch(Exception e){
			Log.w("MTA",e);
		} finally{
			close();
		}
		return null;
	    
	}
	*/
	
	public void deleteAll(){
		Log.d("EEE","@StationModel.deleteAll");//TODO ####
		openWritable();
		db.delete(table, null, null);
		close();
	}
}
