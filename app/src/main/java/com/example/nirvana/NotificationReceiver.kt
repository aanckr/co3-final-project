package com.example.nirvana

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            // Check for the post notification permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                val notificationID = 1
                val notification = NotificationCompat.Builder(context, YOUR_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("FRIDAY!!!")
                    .setContentText("It's time to set your weekend trip")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                with(NotificationManagerCompat.from(context)) {
                    notify(notificationID, notification)
                }
            } else {
                Toast.makeText(context, "Error retrieving location", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val YOUR_NOTIFICATION_CHANNEL_ID = "weekend_notification_channel"
    }
}
