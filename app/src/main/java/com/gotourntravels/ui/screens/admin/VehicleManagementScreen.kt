package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.gotourntravels.models.Vehicle
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.viewmodel.VehiclesViewModel

@Composable
fun VehicleManagementScreen(navController: NavController) {
    val vm: VehiclesViewModel = hiltViewModel()
    val vehicles by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Vehicle?>(null) }
    LaunchedEffect(Unit) { vm.search() }

    pendingDelete?.let { vehicle ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, null) },
            title = { Text("Delete ${vehicle.name}?") },
            text = { Text("This permanently removes ${vehicle.registrationNumber.ifBlank { "this vehicle" }} and cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(vehicle.id) { pendingDelete = null; vm.search() }
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
        )
    }

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Vehicle Management", onBack = { navController.popBackStack() })
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Your fleet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${vehicles.size} vehicles • ${vehicles.count { it.status == "available" }} available", style = MaterialTheme.typography.bodyMedium)
                    }
                    FilledTonalButton(onClick = { navController.navigate(Dest.AdminAddVehicle.route) }) {
                        Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Add vehicle")
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            when {
                loading && vehicles.isEmpty() -> LoadingBlock()
                error != null -> ErrorState(error!!, onRetry = vm::search)
                vehicles.isEmpty() -> EmptyState("No vehicles yet", "Add a vehicle to start managing your fleet", Icons.Default.DirectionsCar)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(vehicles, key = { it.id }) { vehicle ->
                        Card(onClick = { navController.navigate(Dest.editVehicle(vehicle.id)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                                    Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(vehicle.name, fontWeight = FontWeight.Bold)
                                    Text(vehicle.registrationNumber.ifBlank { "No number plate" }, style = MaterialTheme.typography.bodySmall)
                                    Text("₹${vehicle.dailyRate}/day • ${vehicle.totalBookings} bookings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusChip(vehicle.status)
                                    Row {
                                        IconButton(onClick = { navController.navigate(Dest.editVehicle(vehicle.id)) }) { Icon(Icons.Default.Edit, "Edit vehicle") }
                                        IconButton(onClick = { pendingDelete = vehicle }) { Icon(Icons.Default.DeleteOutline, "Delete vehicle", tint = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
