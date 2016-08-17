package com.vampirefreaks.wrapper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GCMIntentService extends GcmListenerService {

	private static final String TAG = "===GCMIntentService===";

	@Override
	public void onMessageReceived(String from, Bundle data) {
		String message = data.getString("message");
		String url = data.getString("url");
		Log.i(TAG, "GCM Message = " + message);
		Log.i(TAG, "GCM URL = " + url);
		generateNotification(message, url);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private void generateNotification(String message, String url) {

		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		String title = this.getString(R.string.app_name);
		
		// when the notification comes in, set globals flag
		Intent notificationIntent = new Intent(this, web.class);
		Globals.isNotificationWaiting = true;
		Globals.notificationUrl = url;
		
		//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification.Builder builder = new Notification.Builder(this);
		builder.setAutoCancel(false);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(pendingIntent);
		builder.setWhen(when);
		builder.build();

		Notification myNotication = builder.getNotification();
		notificationManager.notify(0, myNotication);
	}
}
