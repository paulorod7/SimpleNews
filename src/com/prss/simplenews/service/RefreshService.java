
package com.prss.simplenews.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.prss.simplenews.Strings;

public class RefreshService extends Service {   
	private static final String SIXTYMINUTES = "3600000";
	
    private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (Strings.SETTINGS_REFRESHINTERVAL.equals(key)) {
				restartTimer();
			}
		}
    };

    private Intent refreshBroadcastIntent;
    
	private AlarmManager alarmManager;
	
	private PendingIntent timerIntent;
	
	
	
	private SharedPreferences preferences = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		onRebind(intent);
		return null;
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;  
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			preferences = PreferenceManager.getDefaultSharedPreferences(createPackageContext(Strings.PACKAGE, 0));
		} catch (NameNotFoundException e) {
			preferences = PreferenceManager.getDefaultSharedPreferences(this);
		}
		
		refreshBroadcastIntent = new Intent(Strings.ACTION_REFRESHFEEDS);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		preferences.registerOnSharedPreferenceChangeListener(listener);
		restartTimer();
	}

	private void restartTimer() {
		if (timerIntent == null) {
			timerIntent = PendingIntent.getBroadcast(this, 0, refreshBroadcastIntent, 0);
		} else {
			alarmManager.cancel(timerIntent);
		}
		
		int time = 3600000;
		
		try {
			time = Math.max(60000, Integer.parseInt(preferences.getString(Strings.SETTINGS_REFRESHINTERVAL, SIXTYMINUTES)));
		} catch (Exception exception) {

		}
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000, time, timerIntent);
	}

	@Override
	public void onDestroy() {
		if (timerIntent != null) {
			alarmManager.cancel(timerIntent);
		}
		preferences.unregisterOnSharedPreferenceChangeListener(listener);
		super.onDestroy();
	}
}
