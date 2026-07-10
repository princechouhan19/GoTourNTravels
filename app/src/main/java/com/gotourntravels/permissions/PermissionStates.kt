package com.gotourntravels.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionState(onResult: (Boolean) -> Unit = {}): Boolean {
    val state = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(state.status) { onResult(state.status.isGranted) }
    return state.status.isGranted
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberNotificationPermissionState(onResult: (Boolean) -> Unit = {}): Boolean {
    val state = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(state.status) { onResult(state.status.isGranted) }
    return state.status.isGranted
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraPermissionState(onResult: (Boolean) -> Unit = {}): Boolean {
    val state = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(state.status) { onResult(state.status.isGranted) }
    return state.status.isGranted
}
