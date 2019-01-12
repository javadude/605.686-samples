package com.javadude.alarms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
	public static final String notificationChannel = "com.javadude.foo";

	@Override
	public void onReceive(Context context, Intent intent) {
		// NOTE: I've updated this code to add notification channels, which are not present in the alarm manager video
		//       Otherwise, the alarm manager video is still applicable. The Notifications video covers the concept of notification channels
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(notificationChannel, context.getString(R.string.channel_name), importance);
			channel.setDescription(context.getString(R.string.channel_description));
			notificationManager.createNotificationChannel(channel);
		}


		Intent intent1 = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(context, notificationChannel)
				.setContentTitle("Alarm!!!")
				.setContentText("Yippie!")
				.setSmallIcon(R.drawable.ic_android_black_24dp)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(1, notification);
	}
}
