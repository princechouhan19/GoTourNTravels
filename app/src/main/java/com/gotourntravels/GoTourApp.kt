package com.gotourntravels

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Sets up Hilt DI, WorkManager, and notification channels.
 */
@HiltAndroidApp
class GoTourApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java)

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_TRACKING,
                getString(R.string.tracking_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.tracking_channel_desc) }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SOS,
                getString(R.string.sos_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.sos_channel_desc)
                enableVibration(true)
                enableLights(true)
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_GENERAL,
                getString(R.string.general_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.general_channel_desc) }
        )
    }

    companion object {
        const val CHANNEL_TRACKING = "tracking"
        const val CHANNEL_SOS = "sos"
        const val CHANNEL_GENERAL = "general"
    }
}
