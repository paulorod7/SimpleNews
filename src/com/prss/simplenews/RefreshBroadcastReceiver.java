
package com.prss.simplenews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.prss.simplenews.service.FetcherService;

public class RefreshBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, FetcherService.class).putExtras(intent)); 
	}
	
}
