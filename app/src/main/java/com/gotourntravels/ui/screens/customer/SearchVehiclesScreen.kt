package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.viewmodel.VehiclesViewModel

@Composable
fun SearchVehiclesScreen(navController: NavController, initialType: String) {
    val vm: VehiclesViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(initialType) }

    LaunchedEffect(initialType) {
        if (initialType.isNotBlank()) vm.setType(initialType)
        else vm.search()
        type = initialType
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Search Vehicles", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            GoTourTextField(
                value = query,
                onValueChange = { query = it; vm.setQuery(it) },
                label = "Search by name, brand, model",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                keyboardType = KeyboardType.Text
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All" to "", "Scooter" to "scooter", "Bike" to "bike", "Car" to "car", "SUV" to "suv").forEach { (label, key) ->
                    FilterChip(
                        selected = type == key,
                        onClick = { type = key; vm.setType(key.ifBlank { null }) },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            if (loading && items.isEmpty()) LoadingBlock()
            else if (error != null) ErrorState(error!!, onRetry = { vm.search() })
            else if (items.isEmpty()) EmptyState("No vehicles found", "Try a different search or filter")
            else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { v ->
                        VehicleCard(v) { navController.navigate(Dest.vehicleDetails(v.id)) }
                    }
                }
            }
        }
    }
}
