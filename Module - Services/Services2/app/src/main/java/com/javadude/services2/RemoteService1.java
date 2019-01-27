package com.javadude.services2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RemoteService1 extends Service {
	public static final int RESET = 1;
	public static final int ADD_REPORTER = 2;
	public static final int REMOVE_REPORTER = 3;
	public static final int REPORT = 100;
	public void reset() {
		i = 1;
	}

	private static class MyHandler extends Handler {
		private WeakReference<RemoteService1> serviceRef;

		public MyHandler(RemoteService1 remoteService1) {
			serviceRef = new WeakReference<>(remoteService1);
		}

		@Override
		public void handleMessage(Message msg) {
			RemoteService1 remoteService1 = serviceRef.get();
			if (remoteService1 != null) {
				switch(msg.what) {
					case RESET:
						remoteService1.reset();
						break;
					case ADD_REPORTER:
						remoteService1.reporters.add(msg.replyTo);
						break;
					case REMOVE_REPORTER:
						remoteService1.reporters.remove(msg.replyTo);
						break;
					default:
						super.handleMessage(msg);
				}
			}
		}
	}
	private List<Messenger> reporters = new ArrayList<>();
	private Messenger messenger = new Messenger(new MyHandler(this));


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("StartedService", "onCreate");
	}

	private volatile int i = 1;
	private class CounterThread extends Thread {
		@Override public void run() {
			for(i = 1; !isInterrupted() && i <=100; i++) {
				Log.d("RemoteService1", "count = " + i);
				Message message = Message.obtain(null, REPORT, i, 0);
				for(Messenger messenger : reporters) {
					try {
						messenger.send(message);
					} catch (RemoteException e) {
						Log.e(getClass().getSimpleName(), "Error sending report", e);
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
		Log.d("RemoteService1", "onDestroy");
	}

	@Nullable @Override public synchronized IBinder onBind(Intent intent) {
		if (counterThread == null) {
			counterThread = new CounterThread();
			counterThread.start();
		}
		return messenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("RemoteService1", "onUnbind");
		return super.onUnbind(intent);
	}
}
