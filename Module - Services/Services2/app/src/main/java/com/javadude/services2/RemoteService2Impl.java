package com.javadude.services2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RemoteService2Impl extends Service {
	RemoteService2.Stub binder = new RemoteService2.Stub() {
		public void reset() {
			i = 1;
		}
		public void add(RemoteService2Reporter reporter) {
			reporters.add(reporter);
		}
		public void remove(RemoteService2Reporter reporter) {
			reporters.remove(reporter);
		}
	};
	private List<RemoteService2Reporter> reporters = new ArrayList<>();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("StartedService", "onCreate");
	}

	private volatile int i = 1;
	private class CounterThread extends Thread {
		@Override public void run() {
			for(i = 1; !isInterrupted() && i <=100; i++) {
				Log.d("StartedService", "count = " + i);
				for(RemoteService2Reporter reporter : reporters) {
					try {
						reporter.report(i);
					} catch (RemoteException e) {
						Log.e(getClass().getSimpleName(), "Could not send report", e);
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
			stopSelf();
		}
	}

	private CounterThread counterThread;

	@Override
	public void onDestroy() {
		super.onDestroy();
		counterThread.interrupt();
		Log.d("StartedService", "onDestroy");
	}

	@Nullable @Override public synchronized IBinder onBind(Intent intent) {
		if (counterThread == null) {
			counterThread = new CounterThread();
			counterThread.start();
		}
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("StartedService", "onUnbind");
		return super.onUnbind(intent);
	}
}
