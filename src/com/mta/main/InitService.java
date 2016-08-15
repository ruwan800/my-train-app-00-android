package com.mta.main;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.mta.util.Login;

import android.app.IntentService;
import android.content.Intent;

public class InitService extends IntentService {

	/**
	* A constructor is required, and must call the super IntentService(String)
	* constructor with a name for the worker thread.
	*/
	public InitService() {
		super("InitService");
	}

	/**
	* The IntentService calls this method from the default worker thread with
	* the intent that started the service. When this method returns, IntentService
	* stops the service, as appropriate.
	*/
	@Override
	protected void onHandleIntent(Intent intent) {

		long diff = Calendar.getInstance().getTime().getTime() - Login.getLastLogin(this).getTime();
	    int difference = (int) TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
		if(12 < difference){
			Login.updateRegistration(this);
		}
		
		if(! Login.gcmRegisterComplete(this)){
			Login.RegisterGCM(this);
		}
		
		
		//check database updates
		//TODO enable db update
		//DatabaseUpdate dbUpdate = new DatabaseUpdate();
		//dbUpdate.update(this);


		
	}
}