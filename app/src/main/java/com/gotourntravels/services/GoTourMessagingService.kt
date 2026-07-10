package com.gotourntravels.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gotourntravels.GoTourApp
import com.gotourntravels.MainActivity
import com.gotourntravels.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoTourMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // TODO: send token to backend via ProfileViewModel.updateFcmToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Go Tour N Travels"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        notify(title, body)
    }

    private fun notify(title: String, body: String) {
        val nm = getSystemService(NotificationManager::class.java) ?: return

        // Tapping the notification opens the app
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val n = NotificationCompat.Builder(this, GoTourApp.CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), n)
    }
}
