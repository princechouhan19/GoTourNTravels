package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.models.Vehicle
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.VehiclesViewModel

@Composable
fun AddEditVehicleScreen(navController: NavController, vehicleId: String?) {
    val vm: VehiclesViewModel = hiltViewModel()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val isEdit = vehicleId != null

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
            GoTourTextField(v.primaryImage, { v = v.copy(primaryImage = it) }, "Primary Image URL")
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
