package com.javadude.rest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Util {
	public static TodoItem findTodo(Context context, long id) {
		Uri uri = ContentUris.withAppendedId(TodoContentProvider.CONTENT_URI, id);

		String[] projection = {
				TodoContentProvider.COLUMN_ID,
				TodoContentProvider.COLUMN_NAME,
				TodoContentProvider.COLUMN_DESCRIPTION,
				TodoContentProvider.COLUMN_PRIORITY
		};

		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, projection, null, null, null);

			if (cursor == null || !cursor.moveToFirst())
				return null;

			return new TodoItem(
				cursor.getLong(0),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getInt(3)
			);
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static void updateTodo(Context context, TodoItem todoItem) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TodoContentProvider.COLUMN_NAME, todoItem.getName());
		contentValues.put(TodoContentProvider.COLUMN_DESCRIPTION, todoItem.getDescription());
		contentValues.put(TodoContentProvider.COLUMN_PRIORITY, todoItem.getPriority());

		if (todoItem.getId() != -1) {
			Uri uri = ContentUris.withAppendedId(TodoContentProvider.CONTENT_URI, todoItem.getId());
			context.getContentResolver().update(uri, contentValues, null, null);
		} else {
			Uri uri = context.getContentResolver().insert(TodoContentProvider.CONTENT_URI, contentValues);
			if (uri == null) {
				throw new RuntimeException("No uri returned from insert");
			}
			String stringId = uri.getLastPathSegment();
			long id = Long.parseLong(stringId);
			todoItem.setId(id);
		}
	}

	public static void delete(Context context, long itemId) {
		Uri uri = ContentUris.withAppendedId(TodoContentProvider.CONTENT_URI, itemId);
		context.getContentResolver().delete(uri, null, null);
	}
}
