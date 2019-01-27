package com.javadude.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class TodoContentProvider extends ContentProvider {
	public static final String AUTHORITY = "todo.javadude.com";
	public static final String BASE = "todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + '/' + BASE);

	public static final String TABLE = "TODO";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	public static final String COLUMN_PRIORITY = "PRIORITY";
//	public static final String COLUMN_STATE = "STATE";

	public static final int DB_VERSION = 1;
	public static final int ALL_TODOS = 1;
	public static final int TODO_ITEM = 2;

	public static final String MIME_ALL_TODOS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.todo.javadude.com";
	public static final String MIME_TODO_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.todo.javadude.com";

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		//   content://todo.javadude.com/todo
		URI_MATCHER.addURI(AUTHORITY, BASE, ALL_TODOS);
		//   content://todo.javadude.com/todo/42
		URI_MATCHER.addURI(AUTHORITY, BASE + "/#", TODO_ITEM);
	}


	private static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, "TODO", null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.beginTransaction();

				// create VERSION 1 of the table
				String sql = String.format(
						"CREATE TABLE %s (%s integer primary key autoincrement, %s text, %s text, %s text)",
						TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_PRIORITY
				);

				db.execSQL(sql);
				onUpgrade(db, 1, DB_VERSION);

				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			try {
//				db.beginTransaction();
//
//				switch(oldVersion) {
//					default: throw new IllegalStateException("Unexpected old version");
//
//					case 1: // handle upgrade from 1 -> 2
//						db.execSQL(String.format("ALTER TABLE %s ADD %s INTEGER",
//								TABLE, COLUMN_STATE));
//						// FALLTHRU!!!
//					case 2: // handle upgrade from 2 -> 3
//						db.execSQL(String.format("ALTER TABLE %s ADD %s INTEGER",
//								TABLE, COLUMN_ANOTHER_COLUMN));
//				}
//
//				db.setTransactionSuccessful();
//			} finally {
//				db.endTransaction();
//			}
		}
	}

	private SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		db = new OpenHelper(getContext()).getWritableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {

		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS: {
				Cursor c = db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
				if (getContext() == null) {
					throw new RuntimeException("No content available!");
				}
				c.setNotificationUri(getContext().getContentResolver(), uri);
				return c;
			}
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				Cursor c = db.query(
						TABLE,
						projection,
						COLUMN_ID + " = ?",
						new String[] {id},
						null, null,
						sortOrder);
				if (getContext() == null) {
					throw new RuntimeException("No content available!");
				}
				c.setNotificationUri(getContext().getContentResolver(), uri);
				return c;
			}
			default:
				return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS:
				return MIME_ALL_TODOS;
			case TODO_ITEM:
				return MIME_TODO_ITEM;
			default:
				return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = db.insert(TABLE, null, values);
		notifyChange(uri);
		return ContentUris.withAppendedId(CONTENT_URI, id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {

		int numChanges = 0;
		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS:
				numChanges = db.update(TABLE, values, selection, selectionArgs);
				break;
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				numChanges = db.update(TABLE, values, COLUMN_ID + "=?", new String[] {id});
			}
		}

		if (numChanges != 0) {
			notifyChange(uri);
		}
		return numChanges;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int numDeleted = 0;
		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS:
				numDeleted = db.delete(TABLE, selection, selectionArgs);
				break;
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				numDeleted = db.delete(TABLE, COLUMN_ID + "=?", new String[] {id});
			}
		}
		if (numDeleted != 0) {
			notifyChange(uri);
		}
		return numDeleted;
	}
	private void notifyChange(Uri uri) {
		if (getContext() == null) {
			throw new RuntimeException("No content available!");
		}
		getContext().getContentResolver().notifyChange(uri, null);
	}
}
