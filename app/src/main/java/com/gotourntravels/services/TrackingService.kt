package com.gotourntravels.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.gotourntravels.GoTourApp
import com.gotourntravels.MainActivity
import com.gotourntravels.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Foreground location service for live rental tracking.
 * Started during an active booking; stops when rental completes.
 */
@AndroidEntryPoint
class TrackingService : Service() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var callback: LocationCallback
    private var bookingId: String? = null

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationProviderClientFactory.create(this)
        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onLocation(it) }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bookingId = intent?.getStringExtra(EXTRA_BOOKING_ID)
        startForeground(NOTIF_ID, buildNotification())
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30_000L)
            .setMinUpdateIntervalMillis(15_000L)
            .build()
        try {
            fusedClient.requestLocationUpdates(req, callback, Looper.getMainLooper())
        } catch (_: SecurityException) { /* permission may have been revoked */ }
    }

    private fun onLocation(loc: Location) {
        // Emit location to backend via WorkManager or directly via Retrofit
        // (omitted for brevity — wire through BookingViewModel.updateLocation)
    }

    override fun onDestroy() {
        try { if (::callback.isInitialized) fusedClient.removeLocationUpdates(callback) } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, GoTourApp.CHANNEL_TRACKING)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(getString(R.string.tracking_notification_text))
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    companion object {
        const val NOTIF_ID = 9001
        const val EXTRA_BOOKING_ID = "booking_id"
    }
}

/** Wrapper to allow testing/mocking without leaking FusedLocationProvider constructor. */
object LocationProviderClientFactory {
    fun create(ctx: Service): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(ctx)
}
