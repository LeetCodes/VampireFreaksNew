package com.vampirefreaks.wrapper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import static com.vampirefreaks.wrapper.CommonUtilities.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	private static final String TAG = "===GCMIntentService===";

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "unregistered = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String message = intent.getStringExtra("message");
		String url = intent.getStringExtra("url");
		Log.i(TAG, "GCM Message = " + message);
		Log.i(TAG, "GCM URL = " + url);
		generateNotification(context, message, url);
	}

	@Override
	protected void onError(Context arg0, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message, String url) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String title = context.getString(R.string.app_name);
		
		// when the notification comes in, set globals flag
		Intent notificationIntent = new Intent(context, web.class);
		Globals.isNotificationWaiting = true;
		Globals.notificationUrl = url;
		
		//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setAutoCancel(false);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setSmallIcon(icon);
		builder.setContentIntent(pendingIntent);
		builder.setWhen(when);
		builder.build();

		Notification myNotication = builder.getNotification();
		notificationManager.notify(0, myNotication);
	}
}
