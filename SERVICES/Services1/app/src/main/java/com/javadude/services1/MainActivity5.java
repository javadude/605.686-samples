package com.javadude.services1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.javadude.services2.RemoteService2;
import com.javadude.services2.RemoteService2Reporter;

public class MainActivity5 extends AppCompatActivity {

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	private RemoteService2Reporter.Stub reporter = new RemoteService2Reporter.Stub() {
		@Override
		public void report(int i) {
			progressBar.setProgress(i);
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent();
		intent.setClassName("com.javadude.services2", "com.javadude.services2.RemoteService2Impl");
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		Intent intent = new Intent(this, BoundService2.class);
		stopService(intent);
		super.onStop();
	}

	public void onResetPressed(View view) {
		if (remoteService != null)
			try {
				remoteService.reset();
			} catch (RemoteException e) {
				Log.e(getClass().getSimpleName(), "Cannot reset service count", e);
			}
	}

	private RemoteService2 remoteService;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			remoteService = RemoteService2.Stub.asInterface(service);
			try {
				remoteService.add(reporter);
			} catch (RemoteException e) {
				Log.e(getClass().getSimpleName(), "Cannot add reporter", e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			remoteService = null;
		}
	};
}
