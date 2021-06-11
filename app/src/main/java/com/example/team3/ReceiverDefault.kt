package com.example.team3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class ReceiverDefault : BroadcastReceiver() {
    val CHANNEL_ID = "DEFAULT"
    val textTitle = "Alarm"
    var textContent = "default 채널"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //var importance = MyApplication.prefs.getString("priority", "3")

        var notificationManager: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Default Channel"
            val descriptionText = "testing default"
            var channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val alarmDBHelper = AlarmDBHelper(context, "alarmDB.db")
        val data = alarmDBHelper.getAlarmData(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
        textContent = data.content

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with (NotificationManagerCompat.from(context)){
            notify(data.id, builder.build())
            alarmDBHelper.deleteAlarm(data.id)
        }
    }
}