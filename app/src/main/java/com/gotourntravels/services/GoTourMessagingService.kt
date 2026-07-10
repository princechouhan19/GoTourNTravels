package com.gotourntravels.services

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.gotourntravels.GoTourApp
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
        val n = NotificationCompat.Builder(this, GoTourApp.CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), n)
    }
}
