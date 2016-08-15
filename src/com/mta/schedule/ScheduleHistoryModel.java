package com.mta.schedule;

import com.mta.db.BaseModel;

import android.content.Context;
import android.util.Log;

public class ScheduleHistoryModel extends BaseModel{


    public static final String FROM_TO = "from_to";
    public static final String FROM_STATION = "from_station";
    public static final String FROM_STATION_URI = "from_station_uri";
    public static final String TO_STATION = "to_station";
    public static final String TO_STATION_URI = "to_station_uri";
    public static final String TIMESTAMP = "timestamp";
	public static final String COUNT = "hit_count";
    
	
	public ScheduleHistoryModel(Context context) {
		super(context);
		Log.d("EEE","@ScheduleHistoryModel.StationModel");//TODO ####
	}

	@Override
	public void tableStructure(){
		Log.d("EEE","@ScheduleHistoryModel.tableStructure");//TODO ####
		table = "schedule_history";
		addColumn(TYPE.TEXT, FROM_TO, TYPE.TEXT.value(), UNIQUE);
		addColumn(TYPE.TEXT, FROM_STATION, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, FROM_STATION_URI, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, TO_STATION, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, TO_STATION_URI, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, TIMESTAMP, TYPE.TEXT.value());
		addColumn(TYPE.INTEGER, COUNT, TYPE.INTEGER.value());
	}
	
}
