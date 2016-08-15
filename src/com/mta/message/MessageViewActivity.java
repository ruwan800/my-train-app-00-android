package com.mta.message;

import com.mta.main.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MessageViewActivity extends Activity {

	String TAG = "MTA";
	String messageType;
	String senderReference;
	String targetReference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"@MessageViewActivity::onCreate");//#####
		setContentView(R.layout.activity_message_view);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		messageType = extras.getString("mMessageType");
		String mSender = extras.getString("mSender");
		String mMessage = extras.getString("mMessage");
		String mTarget = extras.getString("mTarget");
		senderReference = extras.getString("mSenderReference");
		targetReference = extras.getString("mTargetReference");
		

		final Button button2 = (Button) findViewById(R.id.button2);
		final Button button1 = (Button) findViewById(R.id.button1);
		final TextView textView1 = (TextView) findViewById(R.id.textView2);
		button1.setText(mSender);
		textView1.setText(mMessage);
		if(messageType.equals("STATION") || messageType.equals("TRAIN")){
			button2.setText(mTarget);
		}
		if(messageType.equals("PERSON")){
			button2.setText("PRIVATE MESSAGE");
			button2.setEnabled(false);
		}
		if(messageType.equals("GLOBAL")){
			button2.setText("PUBLIC MESSAGE");
			button2.setEnabled(false);
		}
			
		Log.d(TAG,String.valueOf(extras.size()));//#####
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static String setTitle(Bundle data) {
		String type = (String)data.get("mMessageType");
		if(type.equals("TRAIN") || type.equals("STATION")){
			return (String)data.get("mTarget");
		}
		else if(type.equals("PERSON") || type.equals("GLOBAL")){
			return (String)data.get("mSender");
		}
		return "TA message";
	}
}
