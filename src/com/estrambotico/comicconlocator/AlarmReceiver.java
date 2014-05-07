package com.estrambotico.comicconlocator;

import java.text.DateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
private static final String DEBUG_TAG = AlarmReceiver.class.toString();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(DEBUG_TAG,"Logging alarm at:" + DateFormat.getDateTimeInstance().format(new Date()));
		
		
		//start the service
		Intent serviceIntent = new Intent(context, LocationService.class);
		context.startService(serviceIntent);
	}

}
