package com.javadude.permissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
	private static final int RECORD_AUDIO_REQUEST = 42;
	private static final int TODO_REQUEST = 43;
	private Button recordButton;
	private Button dumpTodosButton;
	private TextView text;
	private PermissionManager permissionManager = new PermissionManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recordButton = (Button) findViewById(R.id.recordButton);
		dumpTodosButton = (Button) findViewById(R.id.dumpTodosButton);
		text = (TextView) findViewById(R.id.text);
	}

	private PermissionAction dumpTodosAction =
			new PermissionAction(this, "dump the current todo list", "com.javadude.provider.todo") {
		@Override
		protected void onPermissionGranted() {
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

		@Override
		protected void onPermissionDenied() {
			dumpTodosButton.setEnabled(false);
		}
	};

	public void onRecordPressed(View view) {
	}
	public void onDumpTodosPressed(View view) {
		permissionManager.run(dumpTodosAction);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public static final String AUTHORITY = "todo.javadude.com";
	public static final String BASE = "todo";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + '/' + BASE);
}
