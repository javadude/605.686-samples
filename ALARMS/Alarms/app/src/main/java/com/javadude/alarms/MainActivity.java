package com.javadude.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

	private AlarmManager alarmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	}

	public void setAlarm(View view) {
		// NOTE: As of Oreo, this MUST be an explicit intent!
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 15000, pendingIntent);
	}

	public void cancelAlarm(View view) {
		// NOTE: As of Oreo, this MUST be an explicit intent!
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.cancel(pendingIntent);
	}
}
