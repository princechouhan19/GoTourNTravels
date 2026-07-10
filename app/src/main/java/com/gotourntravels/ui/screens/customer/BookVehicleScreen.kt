package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.viewmodel.BookingViewModel
import com.gotourntravels.viewmodel.VehiclesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookVehicleScreen(navController: NavController, vehicleId: String) {
    val vehiclesVm: VehiclesViewModel = hiltViewModel()
    val bookingVm: BookingViewModel = hiltViewModel()
    val vehicle by vehiclesVm.vehicle.collectAsStateWithLifecycle()
    val loading by bookingVm.loading.collectAsStateWithLifecycle()
    val error by bookingVm.error.collectAsStateWithLifecycle()
    val created by bookingVm.created.collectAsStateWithLifecycle()

    LaunchedEffect(vehicleId) { vehiclesVm.loadVehicle(vehicleId) }
    LaunchedEffect(created) {
        created?.let {
            navController.navigate(Dest.bookingSummary(it.id)) {
                popUpTo(Dest.CustomerHome.route)
            }
            bookingVm.resetCreated()
        }
    }

    var rentalType by remember { mutableStateOf("daily") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + 24 * 60 * 60 * 1000) }
    var withDriver by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val v = vehicle
    val isoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
    val dispFmt = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Book Vehicle", onBack = { navController.popBackStack() })
        if (v == null) { LoadingBlock(); return@Column }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            // Vehicle summary
            Surface(color = CreamDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(v.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("₹${if (rentalType == "hourly") v.hourlyRate else if (rentalType == "daily") v.dailyRate else v.weeklyRate}", color = Maroon, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Rental Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("hourly" to "Hourly", "daily" to "Daily", "weekly" to "Weekly").forEach { (key, label) ->
                    FilterChip(selected = rentalType == key, onClick = { rentalType = key }, label = { Text(label) }, modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Pickup Date & Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = dispFmt.format(Date(startDate)),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = { IconButton(onClick = { showStartPicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } }
            )
            Spacer(Modifier.height(12.dp))
            Text("Return Date & Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = dispFmt.format(Date(endDate)),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = { IconButton(onClick = { showEndPicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } }
            )
            Spacer(Modifier.height(16.dp))

            if (v.withDriver) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = withDriver, onCheckedChange = { withDriver = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Include driver (+₹${if (rentalType == "hourly") 100 else 500})")
                }
                Spacer(Modifier.height(16.dp))
            }

            Text("Notes (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp), placeholder = { Text("Any special requests…") })
            Spacer(Modifier.height(20.dp))

            // Pricing summary
            val hours = ((endDate - startDate) / (60 * 60 * 1000)).coerceAtLeast(1).toInt()
            val base = when (rentalType) {
                "hourly" -> v.hourlyRate * hours
                "daily" -> v.dailyRate * ((hours + 23) / 24).coerceAtLeast(1)
                "weekly" -> v.weeklyRate * ((hours + (24 * 7 - 1)) / (24 * 7)).coerceAtLeast(1)
                else -> 0
            }
            val gst = (base * 0.05).toInt()
            val total = base + v.securityDeposit + gst
            Surface(color = Maroon.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PriceLine("Base amount", "₹$base")
                    PriceLine("Security deposit", "₹${v.securityDeposit}")
                    PriceLine("GST (5%)", "₹$gst")
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Total payable", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text("₹$total", color = Maroon, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            error?.let {
                Text(it, color = Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("Confirm Booking", isLoading = loading) {
                bookingVm.create(
                    vehicleId = v.id,
                    rentalType = rentalType,
                    startDateIso = isoFmt.format(Date(startDate)),
                    endDateIso = isoFmt.format(Date(endDate)),
                    withDriver = withDriver,
                    notes = notes
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showStartPicker) {
        DatePickerModal(startDate) { ts -> startDate = ts; showStartPicker = false }
    }
    if (showEndPicker) {
        DatePickerModal(endDate) { ts -> endDate = ts; showEndPicker = false }
    }
}

@Composable
private fun PriceLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DatePickerModal(initial: Long, onConfirm: (Long) -> Unit) {
    val dpState = rememberDatePickerState(initialMillis = initial)
    AlertDialog(
        onDismissRequest = { onConfirm(initial) },
        confirmButton = {
            TextButton(onClick = {
                val date = dpState.selectedDateMillis ?: initial
                // Combine date with current time
                val cal = Calendar.getInstance().apply {
                    timeInMillis = date
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                }
                onConfirm(cal.timeInMillis)
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = { onConfirm(initial) }) { Text("Cancel") } },
        text = { DatePicker(state = dpState) }
    )
}
