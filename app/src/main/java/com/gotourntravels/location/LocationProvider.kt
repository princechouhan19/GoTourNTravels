package com.gotourntravels.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationProvider @Inject constructor(@ApplicationContext private val ctx: Context) {

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): Location? {
        if (!hasPermission()) return null
        return suspendCancellableCoroutine { cont ->
            val task = LocationServices.getFusedLocationProviderClient(ctx).getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
            )
            task.addOnSuccessListener { if (cont.isActive) cont.resume(it) }
            task.addOnFailureListener { if (cont.isActive) cont.resume(null) }
        }
    }
}
