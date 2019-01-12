package com.javadude.services1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class StartedService extends Service {
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("StartedService", "onCreate");
	}

	private Thread counterThread = new Thread() {
		@Override public void run() {
			for(int i = 1; !isInterrupted() && i <=100; i++) {
				Log.d("StartedService", "count = " + i);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
			stopSelf();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("StartedService", "onStartService");
		counterThread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		counterThread.interrupt();
		Log.d("StartedService", "onDestroy");
	}

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}
}
