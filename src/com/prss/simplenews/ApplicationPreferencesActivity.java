package com.prss.simplenews;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import com.prss.simplenews.service.RefreshService;

public class ApplicationPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		
		Preference enablePreference = (Preference) findPreference(Strings.SETTINGS_REFRESHENABLED);

		enablePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (Boolean.TRUE.equals(newValue)) {
					new Thread() {
						public void run() {
							startService(new Intent(ApplicationPreferencesActivity.this, RefreshService.class));
						}
					}.start();
				} else {
					stopService(new Intent(ApplicationPreferencesActivity.this, RefreshService.class));
				}
				return true;
			}
		});
		
		Preference showTabsPreference = (Preference) findPreference(Strings.SETTINGS_SHOWTABS);
		
		showTabsPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (MainTabActivity.INSTANCE != null ) {
					MainTabActivity.INSTANCE.setTabWidgetVisible(Boolean.TRUE.equals(newValue));
				}
				return true;
			}
		});		
	}
	
}
