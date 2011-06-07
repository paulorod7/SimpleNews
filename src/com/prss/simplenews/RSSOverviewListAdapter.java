
package com.prss.simplenews;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import com.prss.simplenews.provider.FeedData;

public class RSSOverviewListAdapter extends ResourceCursorAdapter {
	private static final String COUNT_UNREAD = "COUNT(*) - COUNT(readdate)";
	
	private static final String COUNT = "COUNT(*)";
	
	private static final String COLON = ": ";
	
	private static final String COMMA = ", ";
	
	private int nameColumnPosition;
	
	private int lastUpdateColumn;
	
	private int idPosition;
	
	private int linkPosition;
	
	private int errorPosition;
	
	private int iconPosition;
	
	public RSSOverviewListAdapter(Activity context) {
		super(context, R.layout.listitem, context.managedQuery(FeedData.FeedColumns.CONTENT_URI, null, null, null, null));
		nameColumnPosition = getCursor().getColumnIndex(FeedData.FeedColumns.NAME);
		lastUpdateColumn = getCursor().getColumnIndex(FeedData.FeedColumns.LASTUPDATE);
		idPosition = getCursor().getColumnIndex(FeedData.FeedColumns._ID);
		linkPosition = getCursor().getColumnIndex(FeedData.FeedColumns.URL);
		errorPosition = getCursor().getColumnIndex(FeedData.FeedColumns.ERROR);
		iconPosition = getCursor().getColumnIndex(FeedData.FeedColumns.ICON);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView textView = ((TextView) view.findViewById(android.R.id.text1));
		
		textView.setSingleLine();
		
		Cursor countCursor = context.getContentResolver().query(FeedData.EntryColumns.CONTENT_URI(cursor.getString(idPosition)), new String[] {COUNT_UNREAD, COUNT}, null, null, null);
		
		countCursor.moveToFirst();
		
		int unreadCount = countCursor.getInt(0);
		
		int count = countCursor.getInt(1);
		
		countCursor.close();
		
		long date = cursor.getLong(lastUpdateColumn);
		
		TextView updateTextView = ((TextView) view.findViewById(android.R.id.text2));;
		
		if (cursor.isNull(errorPosition)) {
			updateTextView.setText(new StringBuilder(context.getString(R.string.update)).append(COLON).append(date == 0 ? context.getString(R.string.never) : new StringBuilder(EntriesListAdapter.DATEFORMAT.format(new Date(date))).append(COMMA).append(unreadCount).append('/').append(count).append(' ').append(context.getString(R.string.unread))));
		} else {
			updateTextView.setText(new StringBuilder(context.getString(R.string.error)).append(COLON).append(cursor.getString(errorPosition)));
		}
		if (unreadCount > 0) {
			textView.setTypeface(Typeface.DEFAULT_BOLD);
			textView.setEnabled(true);
			updateTextView.setEnabled(true);
		} else {
			textView.setTypeface(Typeface.DEFAULT);
			textView.setEnabled(false);
			updateTextView.setEnabled(false);
		}
		
		byte[] iconBytes = cursor.getBlob(iconPosition);
		
		if (iconBytes != null && iconBytes.length > 0) {
			textView.setText(" " + (cursor.isNull(nameColumnPosition) ? cursor.getString(linkPosition) : cursor.getString(nameColumnPosition)));
			textView.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length)), null, null, null);
			view.setTag(iconBytes);
		} else {
			view.setTag(null);
			textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			textView.setText(cursor.isNull(nameColumnPosition) ? cursor.getString(linkPosition) : cursor.getString(nameColumnPosition));
		}
	}
}
