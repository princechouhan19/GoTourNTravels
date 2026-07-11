package com.gotourntravels.ui.screens.admin

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.gotourntravels.models.Vehicle
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.VehiclesViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddEditVehicleScreen(navController: NavController, vehicleId: String?) {
    val vm: VehiclesViewModel = hiltViewModel()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val isEdit = vehicleId != null

    val context = LocalContext.current
    var tempCameraFile by remember { mutableStateOf<File?>(null) }

    var v by remember {
        mutableStateOf(
            Vehicle(
                type = "scooter",
                fuelType = "petrol",
                transmission = "automatic",
                seatingCapacity = 2,
                hourlyRate = 80,
                dailyRate = 500,
                weeklyRate = 2800,
                securityDeposit = 1000
            )
        )
    }

    val loadedVehicle by vm.vehicle.collectAsStateWithLifecycle()
    LaunchedEffect(vehicleId) {
        if (vehicleId != null) vm.loadVehicle(vehicleId)
    }
    LaunchedEffect(loadedVehicle) {
        loadedVehicle?.let { v = it }
    }

    fun getTempUri(): Uri? {
        val file = try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.cacheDir
            File.createTempFile("camera_image_${timeStamp}_", ".jpg", storageDir).apply {
                tempCameraFile = this
            }
        } catch (e: Exception) {
            null
        }
        return file?.let {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                it
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraFile != null) {
            vm.uploadVehicleImage(tempCameraFile!!) { url ->
                v = v.copy(primaryImage = url)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) {
                vm.uploadVehicleImage(file) { url ->
                    v = v.copy(primaryImage = url)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = if (isEdit) "Edit Vehicle" else "Add Vehicle", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            GoTourTextField(v.name, { v = v.copy(name = it) }, "Vehicle Name")
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("scooter" to "Scooter", "activa" to "Activa", "bike" to "Bike", "car" to "Car", "suv" to "SUV").forEach { (k, l) ->
                    FilterChip(selected = v.type == k, onClick = { v = v.copy(type = k) }, label = { Text(l) })
                }
            }
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.brand, { v = v.copy(brand = it) }, "Brand")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.model, { v = v.copy(model = it) }, "Model")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.registrationNumber, { v = v.copy(registrationNumber = it) }, "Registration Number")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.color, { v = v.copy(color = it) }, "Color")
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("petrol" to "Petrol", "diesel" to "Diesel", "electric" to "Electric").forEach { (k, l) ->
                    FilterChip(selected = v.fuelType == k, onClick = { v = v.copy(fuelType = k) }, label = { Text(l) })
                }
            }
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.hourlyRate.toString(), { v = v.copy(hourlyRate = it.toIntOrNull() ?: 0) }, "Hourly Rate (₹)", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.dailyRate.toString(), { v = v.copy(dailyRate = it.toIntOrNull() ?: 0) }, "Daily Rate (₹)", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.weeklyRate.toString(), { v = v.copy(weeklyRate = it.toIntOrNull() ?: 0) }, "Weekly Rate (₹)", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.securityDeposit.toString(), { v = v.copy(securityDeposit = it.toIntOrNull() ?: 0) }, "Security Deposit (₹)", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.seatingCapacity.toString(), { v = v.copy(seatingCapacity = it.toIntOrNull() ?: 2) }, "Seating Capacity", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.description, { v = v.copy(description = it) }, "Description")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(v.features.joinToString(", "), { v = v.copy(features = it.split(",").map { s -> s.trim() }.filter { it.isNotBlank() }) }, "Features (comma separated)")
            Spacer(Modifier.height(12.dp))

            // Premium Image Upload Section
            Text("Vehicle Image", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (v.primaryImage.isNotBlank()) {
                        AsyncImage(
                            model = v.primaryImage,
                            contentDescription = "Vehicle Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (v.primaryImage.isBlank()) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("No image uploaded", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Upload a picture of the vehicle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = Color.Green
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Image uploaded successfully", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
                            
                            Button(
                                onClick = {
                                    if (cameraPermissionState.status.isGranted) {
                                        val cameraUri = getTempUri()
                                        if (cameraUri != null) {
                                            cameraLauncher.launch(cameraUri)
                                        }
                                    } else {
                                        cameraPermissionState.launchPermissionRequest()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Camera")
                            }
                            
                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Gallery")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            GoTourTextField(v.primaryImage, { v = v.copy(primaryImage = it) }, "Primary Image URL (auto-filled on upload)")

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = v.withDriver, onCheckedChange = { v = v.copy(withDriver = it) })
                Spacer(Modifier.width(8.dp))
                Text("Available with driver")
            }
            Spacer(Modifier.height(12.dp))
            if (v.withDriver) {
                GoTourTextField(v.driverName, { v = v.copy(driverName = it) }, "Driver Name")
                Spacer(Modifier.height(12.dp))
                GoTourTextField(v.driverPhone, { v = v.copy(driverPhone = it) }, "Driver Phone")
                Spacer(Modifier.height(12.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = v.isFeatured, onCheckedChange = { v = v.copy(isFeatured = it) })
                Spacer(Modifier.width(8.dp))
                Text("Feature on home screen")
            }
            Spacer(Modifier.height(20.dp))
            error?.let { Text(it, color = Red); Spacer(Modifier.height(8.dp)) }
            PrimaryButton(if (isEdit) "Update Vehicle" else "Create Vehicle", isLoading = loading) {
                vm.createOrUpdate(v, isEdit) { ok ->
                    if (ok) navController.popBackStack()
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        tempFile.deleteOnExit()
        tempFile.outputStream().use { output ->
            inputStream.use { input ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
