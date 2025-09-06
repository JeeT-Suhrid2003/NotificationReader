package com.example.notificationreader

import android.app.Notification
import android.app.Person
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.MessagingStyle


class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val notification = sbn.notification
        val extras = notification.extras

        var title: String? = extras.getString(Notification.EXTRA_TITLE)
        var text: String? = extras.getString(Notification.EXTRA_TEXT)

        // Try extracting MessagingStyle messages (for chat apps)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)

            style?.messages?.lastOrNull()?.let { message ->
                val messageText = message.text?.toString()
                val senderName = message.sender?.toString()

                if (senderName != null && messageText != null) {
                    title = senderName
                    text = messageText
                }
            }
        }

        val intent = Intent("com.example.notificationreader.NOTIFICATION_LISTENER").apply {
            putExtra("title", title)
            putExtra("text", text)
        }

        sendBroadcast(intent)
        Log.d("NotificationListener", "Sent broadcast: $title - $text")
    }
}
