package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.PlacesViewModel

@Composable
fun NearbyPlacesScreen(navController: NavController, category: String) {
    val vm: PlacesViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(category) { vm.load(category) }

    val (title, icon, color) = when (category) {
        "petrol" -> Triple("Nearby Petrol Pumps", Icons.Default.LocalGasStation, Green)
        "hospital" -> Triple("Nearby Hospitals", Icons.Default.LocalHospital, Red)
        "police" -> Triple("Nearby Police Stations", Icons.Default.LocalPolice, Maroon)
        else -> Triple("Nearby Places", Icons.Default.Place, Maroon)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = title, onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when {
                loading && items.isEmpty() -> LoadingBlock()
                error != null -> ErrorState(error!!, onRetry = { vm.load(category) })
                items.isEmpty() -> EmptyState("Nothing nearby", "No $category places found")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { p ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(color), contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null, tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.name, fontWeight = FontWeight.Bold)
                                    Text(p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    Text(p.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row {
                                        Text("⭐ ${p.rating}", style = MaterialTheme.typography.labelMedium, color = Gold)
                                        if (p.phone.isNotBlank()) {
                                            Spacer(Modifier.weight(1f))
                                            TextButton(onClick = { /* ACTION_DIAL intent */ }) {
                                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Maroon)
                                                Spacer(Modifier.width(4.dp))
                                                Text("Call", color = Maroon, fontWeight = FontWeight.SemiBold)
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
    }
}
