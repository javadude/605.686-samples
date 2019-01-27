package com.javadude.services1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity2 extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, BoundService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		Intent intent = new Intent(this, BoundService.class);
		stopService(intent);
		super.onStop();
	}

	public void onResetPressed(View view) {
		if (boundService != null)
			boundService.reset();
	}

	private BoundService.BoundServiceBinder boundService;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundService = (BoundService.BoundServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			boundService = null;
		}
	};
}
