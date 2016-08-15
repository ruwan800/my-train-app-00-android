package com.mta.db;

import com.mta.schedule.ScheduleHistoryModel;
import com.mta.station.StationModel;
import com.mta.util.SelectListModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class BaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mta.db";
    public Context context;
    
    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
		Log.d("EEE","@BaseHelper.BaseHelper");//TODO ####
    }
    public void onCreate(SQLiteDatabase db) {
		Log.d("EEE","@BaseHelper.onCreate");//TODO ####
    	//TODO run all create tables
		StationModel stationModel = new StationModel(context);
		stationModel.createTable(db);
		ScheduleHistoryModel scheduleHistoryModel = new ScheduleHistoryModel(context);
		scheduleHistoryModel.createTable(db);
		DatabaseUpdateModel databaseUpdateModel = new DatabaseUpdateModel(context);
		databaseUpdateModel.createTable(db);
		SelectListModel selectListModel = new SelectListModel(context);
		selectListModel.createTable(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("EEE","@BaseHelper.onUpgrade");//TODO ####
		StationModel stationModel = new StationModel(context);
		stationModel.deleteTable(db);
		ScheduleHistoryModel scheduleHistoryModel = new ScheduleHistoryModel(context);
		scheduleHistoryModel.deleteTable(db);
		DatabaseUpdateModel databaseUpdateModel = new DatabaseUpdateModel(context);
		databaseUpdateModel.deleteTable(db);
		SelectListModel selectListModel = new SelectListModel(context);
		selectListModel.deleteTable(db);
        
		onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("EEE","@BaseHelper.onDowngrade");//TODO ####
        onUpgrade(db, oldVersion, newVersion);
    }
    



    
    
}