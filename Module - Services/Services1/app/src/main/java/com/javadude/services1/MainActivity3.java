package com.javadude.services1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity3 extends AppCompatActivity {

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressBar = findViewById(R.id.progressBar);
	}

	private BoundService2.Reporter reporter = new BoundService2.Reporter() {
		@Override
		public void report(int i) {
			progressBar.setProgress(i);
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, BoundService2.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		Intent intent = new Intent(this, BoundService2.class);
		stopService(intent);
		super.onStop();
	}

	public void onResetPressed(View view) {
		if (boundService != null)
			boundService.reset();
	}

	private BoundService2.BoundServiceBinder boundService;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundService = (BoundService2.BoundServiceBinder) service;
			boundService.addReporter(reporter);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			boundService = null;
		}
	};
}
