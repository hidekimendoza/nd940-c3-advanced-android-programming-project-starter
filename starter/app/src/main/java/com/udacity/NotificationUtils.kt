package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String, applicationContext: Context,
    url: String = "",
    status: String = ""
) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        Log.i("sendNotification", "url -> $url")
        this.putExtra("URL", url.toString())
        this.putExtra("STATUS", status)
    }

    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(
        0,
        applicationContext.getText(R.string.notification_button),
        detailPendingIntent
    )
        .build()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setOnlyAlertOnce(true)
        .addAction(action)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

