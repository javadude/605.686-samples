package com.javadude.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent1 = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 15000, pendingIntent);
	}
}
