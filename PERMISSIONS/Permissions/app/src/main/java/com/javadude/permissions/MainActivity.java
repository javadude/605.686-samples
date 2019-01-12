package com.javadude.permissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	private static final int RECORD_AUDIO_REQUEST = 42;
	private static final int TODO_REQUEST = 43;
	private Button recordButton;
	private Button dumpTodosButton;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recordButton = (Button) findViewById(R.id.recordButton);
		dumpTodosButton = (Button) findViewById(R.id.dumpTodosButton);
		text = (TextView) findViewById(R.id.text);
	}

	public void onRecordPressed(View view) {
		tryToRecord();
	}
	public void onDumpTodosPressed(View view) {
		tryToDumpTodos();
	}

	private void tryToRecord() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			record();
		} else {
			int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
			if (result == PackageManager.PERMISSION_GRANTED) {
				record();
			} else {
				requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO},
						RECORD_AUDIO_REQUEST);
				// NOTE: THIS IS ASYNCHRONOUS!!!
			}
		}
	}

	private void tryToDumpTodos() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			dumpTodos();
		} else {
			int result = ContextCompat.checkSelfPermission(this, "com.javadude.provider.todo");
			if (result == PackageManager.PERMISSION_GRANTED) {
				dumpTodos();
			} else {
				requestPermissions(new String[] {"com.javadude.provider.todo"},
						TODO_REQUEST);
				// NOTE: THIS IS ASYNCHRONOUS!!!
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == RECORD_AUDIO_REQUEST) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				record();
			} else {
				// report to user? toast/snackbar and disable feature?
				Toast.makeText(this, "Cannot record; permission denied!", Toast.LENGTH_SHORT).show();
				recordButton.setEnabled(false);
			}
		} else if (requestCode == TODO_REQUEST) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				dumpTodos();
			} else {
				// report to user? toast/snackbar and disable feature?
				Toast.makeText(this, "Cannot dump todos; permission denied!", Toast.LENGTH_SHORT).show();
				dumpTodosButton.setEnabled(false);
			}
		}
	}

	private void record() {
		// do some recording
	}

	public static final String AUTHORITY = "todo.javadude.com";
	public static final String BASE = "todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + '/' + BASE);

	private void dumpTodos() {
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(CONTENT_URI, new String[]{"NAME", "DESCRIPTION"}, null, null, null);
			if (cursor != null) {
				String data = "";
				while (cursor.moveToNext()) {
					data += cursor.getString(0) + ": " + cursor.getString(1) + "\n";
				}
				text.setText(data);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
}
