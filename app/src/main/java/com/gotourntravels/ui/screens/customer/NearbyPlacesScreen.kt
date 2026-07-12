package com.gotourntravels.ui.screens.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    LaunchedEffect(category) { vm.load(category) }

    val (title, icon, color) = when (category) {
        "petrol" -> Triple("Nearby Petrol Pumps", Icons.Default.LocalGasStation, Green)
        "hospital" -> Triple("Nearby Hospitals", Icons.Default.LocalHospital, Red)
        "police" -> Triple("Nearby Police Stations", Icons.Default.LocalPolice, Maroon)
        else -> Triple("Nearby Places", Icons.Default.Place, Maroon)
    }
    fun navigate(name: String, lat: Double, lng: Double) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(name)})")))
    }

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar(title, onBack = { navController.popBackStack() })
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Tap a place for live directions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            when {
                loading && items.isEmpty() -> LoadingBlock()
                error != null -> ErrorState(error!!, onRetry = { vm.load(category) })
                items.isEmpty() -> EmptyState("Nothing nearby", "No $category places found")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { p ->
                        Card(onClick = { navigate(p.name, p.lat, p.lng) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(color), contentAlignment = Alignment.Center) {
                                    Icon(icon, null, tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(p.name, fontWeight = FontWeight.Bold)
                                    Text(p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    Text(p.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("★ ${p.rating}", style = MaterialTheme.typography.labelMedium, color = Gold)
                                        Spacer(Modifier.weight(1f))
                                        TextButton(onClick = { navigate(p.name, p.lat, p.lng) }) {
                                            Icon(Icons.Default.Directions, null, Modifier.size(14.dp), color)
                                            Spacer(Modifier.width(3.dp)); Text("Directions", color = color)
                                        }
                                        if (p.phone.isNotBlank()) {
                                            TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${p.phone}"))) }) {
                                                Icon(Icons.Default.Phone, null, Modifier.size(14.dp), Maroon)
                                                Spacer(Modifier.width(3.dp)); Text("Call", color = Maroon)
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
