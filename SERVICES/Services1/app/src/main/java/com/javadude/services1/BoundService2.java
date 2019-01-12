package com.javadude.services1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BoundService2 extends Service {
	public interface Reporter {
		void report(int i);
	}
	public class BoundServiceBinder extends Binder {
		public void reset() {
			i = 1;
		}
		public void addReporter(Reporter reporter) {
			reporters.add(reporter);
		}
		public void removeReporter(Reporter reporter) {
			reporters.remove(reporter);
		}
	}
	private List<Reporter> reporters = new ArrayList<>();

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
				for(Reporter reporter : reporters) {
					reporter.report(i);
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
		return new BoundServiceBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("StartedService", "onUnbind");
		return super.onUnbind(intent);
	}
}
