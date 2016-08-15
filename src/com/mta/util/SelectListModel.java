package com.mta.util;

import com.mta.db.BaseModel;

import android.content.Context;
import android.util.Log;

public class SelectListModel extends BaseModel{


    public static final String F0 = "c0";
    public static final String F1 = "c1";
    public static final String F2 = "c2";
    public static final String F3 = "c3";
    public static final String F4 = "c4";
    public static final String F5 = "c5";
    public static final String H_TYPE = "type";
    public static final String TSTAMP = "timestamp";
	public static final String COUNT = "hit_count";
	public static final String FAVOURITE = "favourite";
    
	
	public SelectListModel(Context context) {
		super(context);
		Log.d("EEE","@ScheduleHistoryModel.StationModel");//TODO ####
	}

	@Override
	public void tableStructure(){
		Log.d("EEE","@ScheduleHistoryModel.tableStructure");//TODO ####
		table = "select_list";
		addColumn(TYPE.TEXT, F0, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, F1, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, F2, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, F3, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, F4, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, F5, TYPE.TEXT.value());
		addColumn(TYPE.TEXT, H_TYPE, TYPE.TEXT.value());
		addColumn(TYPE.TIMESTAMP, TSTAMP, TYPE.TIMESTAMP.value());
		addColumn(TYPE.INTEGER, COUNT, TYPE.INTEGER.value());
		addColumn(TYPE.BOOLEAN, FAVOURITE, TYPE.BOOLEAN.value());
	}
	
}
