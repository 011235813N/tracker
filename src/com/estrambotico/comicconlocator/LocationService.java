package com.estrambotico.comicconlocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

public class LocationService extends Service implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;
	public static final String APPTAG = LocationService.class.toString();

	private boolean mUpdatesRequested = false;

	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	public static final int FAST_CEILING_IN_SECONDS = 1;
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* FAST_CEILING_IN_SECONDS;

	// /preferencias
	private static SharedPreferences sharedPref = null;
	private static String PREF_USER_NAME = "";
	private static String PREF_HOUSE_NAME = "";
	// private static String PREF_SYNC_FREQ = "";
	private static String PREF_URL_API = "";

	private static Location mLocation = null;

	private static final String PREF_USER_NAME_KEY = "prefUsername";
	private static final String PREF_HOUSE_NAME_KEY = "prefHousename";
	private static final String PREF_SYNC_FREQ_KEY = "prefSyncFrequency";
	private static final String PREF_URL_API_KEY = "prefURLAPI";

	@Override
	public void onDestroy() {
		mLocationClient.disconnect();

		Log.i(APPTAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onCreate() {

		// obtener las configuraciones de la app.
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		PREF_USER_NAME = sharedPref.getString(PREF_USER_NAME_KEY, "");
		PREF_HOUSE_NAME = sharedPref.getString(PREF_HOUSE_NAME_KEY, "");
		// PREF_SYNC_FREQ = sharedPref.getString(PREF_SYNC_FREQ_KEY, "");
		PREF_URL_API = sharedPref.getString(PREF_URL_API_KEY, "");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest
				.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		mUpdatesRequested = false;

		mLocationClient = new LocationClient(this, this, this);

		mLocationClient.connect();

		Log.i(APPTAG, "onCreate");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(APPTAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

		getLocation();
		stopSelf();

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getLocation() {

		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			Location currentLocation = mLocationClient.getLastLocation();
			if (currentLocation != null) {
				mLocation = currentLocation;
				new postData().execute();
			}
			Log.i(APPTAG, currentLocation.toString());
		}
	}

	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		Log.d(APPTAG, "" + resultCode);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(APPTAG, getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			/*
			 * Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
			 * this, 0); if (dialog != null) { ErrorDialogFragment errorFragment
			 * = new ErrorDialogFragment(); errorFragment.setDialog(dialog);
			 * errorFragment.show(getSupportFragmentManager(),
			 * LocationUtils.APPTAG); }
			 */
			return false;
		}
	}

	private class postData extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			HttpClient cliente = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(cliente.getParams(),
					10000);

			SimpleDateFormat dfaq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			HttpResponse response;
			JSONObject json = new JSONObject();

			String URL = PREF_URL_API;
			try {
				HttpPost post = new HttpPost(URL);
				json.put("cosplayer", PREF_USER_NAME);
				json.put("house", PREF_HOUSE_NAME);
				json.put("lat", mLocation.getLatitude());
				json.put("lon", mLocation.getLongitude());
				json.put("time", mLocation.getTime());
				json.put("last_seen", dfaq.format(mLocation.getTime()));

				StringEntity se = new StringEntity(json.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));

				post.setEntity(se);
				Log.i(APPTAG, json.toString());

				response = cliente.execute(post);
				if (response != null) {
					InputStream is = response.getEntity().getContent();

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					StringBuilder sb = new StringBuilder();

					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Log.i(APPTAG, sb.toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(APPTAG, "error " + e.getMessage());
			}
			return 1;

		}
	}

}
