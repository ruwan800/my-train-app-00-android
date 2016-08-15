package com.mta.util;

import android.content.Context;

import com.mta.db.DatabaseUpdateModel;

public class DatabaseInfo {
	
	private Context context;
	
	public DatabaseInfo(Context context){
		this.context = context;
	}

	public boolean isUptoDate (String table){
		DatabaseUpdateModel dbUpdate = new DatabaseUpdateModel(this.context);
		return dbUpdate.isUptoDate(table);
		
	}
	
	public boolean isNeverUpdated (String table){
		DatabaseUpdateModel dbUpdate = new DatabaseUpdateModel(this.context);
		return dbUpdate.isNeverUpdated(table);
		
	}

	public void setUpdated(String table) {
		DatabaseUpdateModel dbUpdate = new DatabaseUpdateModel(this.context);
		dbUpdate.updateLocal(table);
		
	}
}
