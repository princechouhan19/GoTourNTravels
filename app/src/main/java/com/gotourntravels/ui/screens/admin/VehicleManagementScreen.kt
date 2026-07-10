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
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.viewmodel.VehiclesViewModel

@Composable
fun VehicleManagementScreen(navController: NavController) {
    val vm: VehiclesViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.search() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Vehicle Management", onBack = { navController.popBackStack() }, actions = {
            IconButton(onClick = { navController.navigate(Dest.AdminAddVehicle.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when {
                loading && items.isEmpty() -> LoadingBlock()
                error != null -> ErrorState(error!!, onRetry = { vm.search() })
                items.isEmpty() -> EmptyState("No vehicles", "Add your first vehicle to get started", Icons.Default.TwoWheeler)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { v ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), onClick = {
                            navController.navigate(Dest.editVehicle(v.id))
                        }) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(v.name, fontWeight = FontWeight.Bold)
                                    Text(v.registrationNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${v.dailyRate}/day • ${v.totalBookings} bookings", style = MaterialTheme.typography.bodySmall)
                                }
                                StatusChip(v.status)
                                IconButton(onClick = { navController.navigate(Dest.editVehicle(v.id)) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
