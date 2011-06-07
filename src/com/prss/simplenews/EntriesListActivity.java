
package com.prss.simplenews;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import com.prss.simplenews.provider.FeedData;

public class EntriesListActivity extends ListActivity {
	public static final String EXTRA_SHOWREAD = "show_read";
	
	public static final String EXTRA_SHOWFEEDINFO = "show_feedinfo";

	private Uri uri;
	
	private EntriesListAdapter entriesListAdapter;
	
	private byte[] iconBytes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		iconBytes = getIntent().getByteArrayExtra(FeedData.FeedColumns.ICON);

		if (iconBytes != null && iconBytes.length > 0) { // overwrite mas reserva o icon
			if (!requestWindowFeature(Window.FEATURE_LEFT_ICON)) {
				iconBytes = null;
			}
		}
        
		setContentView(R.layout.entries);
		uri = getIntent().getData();
		entriesListAdapter = new EntriesListAdapter(this, uri, getIntent().getBooleanExtra(EXTRA_SHOWFEEDINFO, false));
        setListAdapter(entriesListAdapter);
        
        String title = getIntent().getStringExtra(FeedData.FeedColumns.NAME);
        
        if (title != null) {
        	setTitle(title);
        }
        if (iconBytes != null && iconBytes.length > 0) {
        	setFeatureDrawable(Window.FEATURE_LEFT_ICON, new BitmapDrawable(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length)));
        }
        RSSOverview.notificationManager.cancel(0);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		startActivity(new Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(uri, id)).putExtra(EXTRA_SHOWREAD, entriesListAdapter.isShowRead()).putExtra(FeedData.FeedColumns.ICON, iconBytes));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, R.string.contextmenu_markasread).setIcon(android.R.drawable.ic_menu_revert);
		menu.add(1, 1, Menu.NONE, R.string.contextmenu_hideread).setCheckable(true).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.setGroupVisible(0, entriesListAdapter.getCount() > 0);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == 0) {
			new Thread() { // pode demorar um pouco :s estudar melhor..
				public void run() {
					getContentResolver().update(uri, RSSOverview.getReadContentValues(), null, null);
				}
			}.start();
		} else {
			if (item.isChecked()) {
				item.setChecked(false).setTitle(R.string.contextmenu_hideread).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				entriesListAdapter.showRead(true);
			} else {
				item.setChecked(true).setTitle(R.string.contextmenu_showread).setIcon(android.R.drawable.ic_menu_view);
				entriesListAdapter.showRead(false);
			}
		}
		return true;
	}
	
}
