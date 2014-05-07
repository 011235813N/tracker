package com.estrambotico.comicconlocator;

import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.CompoundButton;
import android.widget.Toast;

import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private static final long INITIAL_ALARM_DELAY = 15000L;
	protected static final long JITTER = 5000L;
	private AlarmManager alarm;
	NotificationManager mNotificationManager;
	private Intent alarmReceiver;
	private PendingIntent pendingAlarmReceiver;
	private String TAG = MainActivity.class.toString();
	private boolean mStarted = false;
	private String mKey = "started";
	private static final int RESULT_SETTINGS = 1;
	private static ToggleButton btn_start;
	public static final String PREFS_NAME = "ComicConPref";
	private String syncFreqPref = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		alarmReceiver = new Intent(MainActivity.this, AlarmReceiver.class);
		pendingAlarmReceiver = PendingIntent.getBroadcast(MainActivity.this, 0,
				alarmReceiver, 0);

		btn_start = (ToggleButton) findViewById(R.id.tg_activar);

		// Leemos de las preferencias para saber si el servicio esta corriendo.
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mStarted = settings.getBoolean(mKey, false);

		// leer los valores guardados de las preferencias
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		syncFreqPref = sharedPref.getString("prefSyncFrequency", "1");

		btn_start
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							if (!mStarted) {// si no esta iniciado, iniciar la alarma.
								startAlarm();
							}

						} else {
							alarm.cancel(pendingAlarmReceiver);
							Log.i(TAG, "Alarma detenida");
							cancelNotificacion();

							mStarted = btn_start.isChecked();

							SharedPreferences settings = getSharedPreferences(
									PREFS_NAME, 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean(mKey, mStarted);

							// Commit the edits!
							editor.commit();
							Toast.makeText(getApplicationContext(), "Apagado",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	public void startAlarm() {
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
				Integer.parseInt(syncFreqPref) * 60 * 1000L,
				pendingAlarmReceiver); // factor
		Log.i(TAG, "Iniciada la alarma: " + syncFreqPref.toString());
		displayNotificacion();

		mStarted = btn_start.isChecked();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(mKey, mStarted);

		// Commit the edits!
		editor.commit();

		Toast.makeText(getApplicationContext(), "Encendido", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		// leer el valor del toggle
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		btn_start.setChecked(settings.getBoolean(mKey, false));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// startAlarm();
		// leer el valor del toggle
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		btn_start.setChecked(settings.getBoolean(mKey, false));

	}

	public void displayNotificacion() {

		// ---PendingIntent to launch activity if the user selects
		// the notification---
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent detailsIntent = PendingIntent.getActivity(this, 0, i, 0);

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_launcher,
				"¡Comic Con!", System.currentTimeMillis());

		CharSequence from = "¡ComicCon Locator!";
		CharSequence message = "Tu posición se esta enviando";
		notif.setLatestEventInfo(this, from, message, detailsIntent);

		// ---100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms--
		nm.notify(0, notif);
	}

	public void cancelNotificacion() {
		(mNotificationManager).cancel(0);
	}

	public void updateNotification() {
		//
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		mStarted = btn_start.isChecked();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(mKey, mStarted);

		// Commit the edits!
		editor.commit();

		Log.i(TAG, "onpause: " + mStarted);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// guardando estado actual.
		mStarted = btn_start.isChecked();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(mKey, mStarted);

		// Commit the edits!
		editor.commit();

		Log.i(TAG, "onstop: " + mStarted);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}

		return true;
	}

}
