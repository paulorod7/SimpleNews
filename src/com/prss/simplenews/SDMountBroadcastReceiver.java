
package com.prss.simplenews;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import com.prss.simplenews.service.RefreshService;

public class SDMountBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getBooleanExtra("read-only", false)) {
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			
			manager.restartPackage(Strings.PACKAGE);
			
			try {
				if (PreferenceManager.getDefaultSharedPreferences(context.createPackageContext(Strings.PACKAGE, 0)).getBoolean(Strings.SETTINGS_REFRESHENABLED, false)) {
					context.startService(new Intent(context, RefreshService.class));
				}
			} catch (NameNotFoundException e) {
				
			}
			context.sendBroadcast(new Intent(Strings.ACTION_UPDATEWIDGET));
		}
	}

}
