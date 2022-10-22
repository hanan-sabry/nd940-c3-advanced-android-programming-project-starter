package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 1
private const val REQUEST_CODE = 0
private const val FLAGS = 0

fun NotificationManager.sendNotification(
    applicationContext: Context,
    title: String,
    body: String,
    notificationBundle: Bundle?
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    notificationBundle?.let {
        contentIntent.putExtras(notificationBundle)
    }
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val statusActionIntent = Intent(applicationContext, DetailActivity::class.java)
    val statusPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        statusActionIntent,
        0
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

fun NotificationManager.createChannel(
    channelId: String,
    channelName: String,
    channelDesc: String,
    importance: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            importance
            )
        if (importance == NotificationManager.IMPORTANCE_HIGH) {
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = channelDesc
        }

        createNotificationChannel(notificationChannel)
    }
}