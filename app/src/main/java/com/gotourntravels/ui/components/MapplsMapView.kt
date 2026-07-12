package com.gotourntravels.ui.components

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mappls.sdk.maps.MapView
import com.mappls.sdk.maps.MapplsMap
import com.mappls.sdk.maps.camera.CameraUpdateFactory
import com.mappls.sdk.maps.annotations.MarkerOptions
import com.mappls.sdk.maps.geometry.LatLng
import com.mappls.sdk.maps.OnMapReadyCallback

data class MapMarker(
    val position: LatLng,
    val title: String,
    val snippet: String? = null
)

@Composable
fun MapplsMapView(
    center: LatLng,
    zoom: Double = 14.0,
    markers: List<MapMarker> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Keep track of the MapView instance
    val mapView = remember { MapView(context) }

    // State to hold MapplsMap reference once it's loaded
    var mapplsMapInstance by remember { mutableStateOf<MapplsMap?>(null) }

    // Sync Lifecycle with MapView
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Effect to update camera center and zoom when it changes
    LaunchedEffect(mapplsMapInstance, center, zoom) {
        mapplsMapInstance?.let { map ->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom))
        }
    }

    // Effect to update markers when the list changes
    LaunchedEffect(mapplsMapInstance, markers) {
        mapplsMapInstance?.let { map ->
            map.clear() // Remove existing markers
            markers.forEach { markerData ->
                val markerOptions = MarkerOptions()
                    .position(markerData.position)
                    .title(markerData.title)
                markerData.snippet?.let { markerOptions.snippet(it) }
                map.addMarker(markerOptions)
            }
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(map: MapplsMap) {
                        mapplsMapInstance = map
                        // Perform initial configurations
                        try {
                            map.uiSettings?.isLogoEnabled = false
                            map.uiSettings?.isCompassEnabled = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom))
                    }

                    override fun onMapError(errorCode: Int, errorMessage: String?) {
                        // Map error callback
                    }
                })
            }
        },
        modifier = modifier
    )
}
