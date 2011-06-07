
package com.prss.simplenews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import com.prss.simplenews.service.RefreshService;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (PreferenceManager.getDefaultSharedPreferences(context.createPackageContext(Strings.PACKAGE, 0)).getBoolean(Strings.SETTINGS_REFRESHENABLED, false)) {
				context.startService(new Intent(context, RefreshService.class));
			}
			context.sendBroadcast(new Intent(Strings.ACTION_UPDATEWIDGET));
		} catch (NameNotFoundException e) {
		}
	}

}
