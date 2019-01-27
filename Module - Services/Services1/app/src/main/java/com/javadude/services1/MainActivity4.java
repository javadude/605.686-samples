package com.javadude.services1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class MainActivity4 extends AppCompatActivity {
	private static final int RESET = 1;
	private static final int ADD_REPORTER = 2;
	private static final int REMOVE_REPORTER = 3;
	private static final int REPORT = 100;

	private ProgressBar progressBar;

	private static class MyHandler extends Handler {
		private WeakReference<MainActivity4> serviceRef;

		public MyHandler(MainActivity4 remoteService1) {
			serviceRef = new WeakReference<>(remoteService1);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity4 activity = serviceRef.get();
			if (activity != null) {
				switch(msg.what) {
					case REPORT:
						activity.progressBar.setProgress(msg.arg1);
						break;
					default:
						super.handleMessage(msg);
				}
			}
		}
	}

	private Messenger messenger = new Messenger(new MyHandler(this));



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressBar = findViewById(R.id.progressBar);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent();
		intent.setClassName("com.javadude.services2", "com.javadude.services2.RemoteService1");
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		unbindService(serviceConnection);
		super.onStop();
	}

	public void onResetPressed(View view) {
		if (remoteService != null) {
			Message message = Message.obtain(null, RESET, 0, 0);
			try {
				remoteService.send(message);
			} catch (RemoteException e) {
				Log.e(getClass().getSimpleName(), "Error sending message", e);
			}
		}
	}

	private Messenger remoteService;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			remoteService = new Messenger(service);
			Message message = Message.obtain(null, ADD_REPORTER);
			message.replyTo = messenger;
			try {
				remoteService.send(message);
			} catch (RemoteException e) {
				Log.e(getClass().getSimpleName(), "Error adding reporter", e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			remoteService = null;
		}
	};
}
