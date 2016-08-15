package com.mta.db;

import com.mta.main.R;

import android.content.Context;
import android.util.Log;

public class DatabaseUpdate {

  public void update(Context context) {
	  Log.d("MTA","@DatabaseUpdate.update");
	  String url = context.getString(R.string.url_site);
	  String url2 = context.getString(R.string.url_db_update);
	  url += url2;
	  DatabaseUpdateModel dbModel = new DatabaseUpdateModel(context);
	  dbModel.updateServer(url);
  }
}