package com.javadude.rest;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TodoContentProvider extends ContentProvider {
	public static final String AUTHORITY = "todo.javadude.com";
	public static final String BASE = "todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + '/' + BASE);

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	public static final String COLUMN_PRIORITY = "PRIORITY";

	public static final String[] PROJECTION_ALL = {
		COLUMN_ID,
		COLUMN_NAME,
		COLUMN_DESCRIPTION,
		COLUMN_PRIORITY
	};

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

	private String server;


	@Override
	public boolean onCreate() {
		server = "http://10.0.2.2:8080/restserver/todo";
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		if (selection != null) {
			throw new IllegalArgumentException("Selection arguments not supported by this content provider");
		}

		if (getContext() == null) {
			throw new RuntimeException("No context available!");
		}
		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS: {
				String uriString = server;
				try {
					Result result = httpRequest(Method.GET, uriString, null);
					if (result.getStatusCode() < 300) {
						JSONArray jsonArray = new JSONArray(result.getContent());
						MatrixCursor matrixCursor = new MatrixCursor(PROJECTION_ALL);
						for(int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.optJSONObject(i);
							matrixCursor.newRow()
									.add(jsonObject.optLong("id"))
									.add(jsonObject.optString("name"))
									.add(jsonObject.optString("description"))
									.add(jsonObject.optInt("priority"));
						}
						matrixCursor.setNotificationUri(getContext().getContentResolver(), uri);
						return matrixCursor;
					} else {
						throw new RuntimeException("Could not access data: " + result.getStatusCode() + ": " + result.getStatusMessage());
					}
				} catch (Throwable e) {
					throw new RuntimeException("Could not access data", e);
				}
			}
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				String uriString = server + "/" + id;
				try {
					Result result = httpRequest(Method.GET, uriString, null);
					if (result.getStatusCode() < 300) {
						JSONObject jsonObject = new JSONObject(result.getContent());
						MatrixCursor matrixCursor = new MatrixCursor(PROJECTION_ALL);
						matrixCursor.newRow()
								.add(jsonObject.optLong("id"))
								.add(jsonObject.optString("name"))
								.add(jsonObject.optString("description"))
								.add(jsonObject.optInt("priority"));
						matrixCursor.setNotificationUri(getContext().getContentResolver(), uri);
						return matrixCursor;
					} else {
						throw new RuntimeException("Could not access data: " + result.getStatusCode() + ": " + result.getStatusMessage());
					}
				} catch (Throwable e) {
					throw new RuntimeException("Could not access data", e);
				}
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
		TodoItem todoItem = new TodoItem();
		todoItem.setName((String) values.get(COLUMN_NAME));
		todoItem.setDescription((String) values.get(COLUMN_DESCRIPTION));
		todoItem.setPriority((int) values.get(COLUMN_PRIORITY));
		Result result = httpRequest(Method.POST, server, todoItem);
		if (result.getStatusCode() < 300) {
			String uriResult = result.getContent();
			Uri serverUri = Uri.parse(uriResult.trim());
			String idString = serverUri.getLastPathSegment();
			long id = Long.parseLong(idString);
			notifyChange(uri);
			return ContentUris.withAppendedId(CONTENT_URI, id);
		} else {
			throw new RuntimeException("Could not insert data: " + result.getStatusCode() + ": " + result.getStatusMessage());
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {
		if (selection != null) {
			throw new IllegalArgumentException("Selection arguments not supported by this content provider");
		}

		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS:
				throw new IllegalArgumentException("Must provide id when deleting");
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				TodoItem todoItem = new TodoItem();
				todoItem.setId(Long.parseLong(id));
				todoItem.setName((String) values.get(COLUMN_NAME));
				todoItem.setDescription((String) values.get(COLUMN_DESCRIPTION));
				todoItem.setPriority((int) values.get(COLUMN_PRIORITY));
				String uriString = server + "/" + id;
				httpRequest(Method.PUT, uriString, todoItem);
				notifyChange(uri);
			}
		}
		return 1;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (selection != null) {
			throw new IllegalArgumentException("Selection arguments not supported by this content provider");
		}
		switch(URI_MATCHER.match(uri)) {
			case ALL_TODOS:
				throw new IllegalArgumentException("Must provide id when deleting");
			case TODO_ITEM: {
				String id = uri.getLastPathSegment();
				String uriString = server + "/" + id;
				httpRequest(Method.DELETE, uriString, null);
				notifyChange(uri);
			}
		}
		return 1;
	}
	private void notifyChange(Uri uri) {
		if (getContext() == null) {
			throw new RuntimeException("No content available!");
		}
		getContext().getContentResolver().notifyChange(uri, null);
	}

	private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	private Result httpRequest(final Method method, final String uriString, final TodoItem todoItem) {
		Callable<Result> callable = new Callable<Result>() {
			@Override
			public Result call() throws Exception {
				try {
					URL url = new URL(uriString);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();

					connection.setRequestMethod(method.name());
					connection.connect();
					if (todoItem != null) {
						OutputStream out = connection.getOutputStream();
						BufferedOutputStream bos = new BufferedOutputStream(out);
						bos.write(todoItem.toJsonString().getBytes());
						bos.flush();
					}
					int responseCode = connection.getResponseCode();
					String content = "";
					if (responseCode < 300) {
						InputStream in = connection.getInputStream();
						InputStreamReader isr = new InputStreamReader(in);
						BufferedReader br = new BufferedReader(isr);
						String line;
						while((line = br.readLine()) != null) {
							content += line + "\n";
						}
					}
					return new Result(responseCode, connection.getResponseMessage(), content);

				} catch (IOException | JSONException e) {
					throw new RuntimeException(e);
				}
			}
		};
		Future<Result> future = EXECUTOR_SERVICE.submit(callable);
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
