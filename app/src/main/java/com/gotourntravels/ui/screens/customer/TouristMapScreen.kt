package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.PlacesViewModel
import com.mappls.sdk.maps.geometry.LatLng

@Composable
fun TouristMapScreen(navController: NavController) {
    val vm: PlacesViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.load(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Tourist Map — Mount Abu")
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Mappls Map
            Card(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                val center = LatLng(24.5925, 72.7078) // Nakki Lake (Mount Abu center)
                val mapMarkers = items.map { place ->
                    com.gotourntravels.ui.components.MapMarker(
                        position = LatLng(place.lat, place.lng),
                        title = place.name,
                        snippet = "${place.rating}★ - ${place.address}"
                    )
                }
                com.gotourntravels.ui.components.MapplsMapView(
                    center = center,
                    zoom = 12.0,
                    markers = mapMarkers,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(16.dp))

            // Quick filters
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = { navController.navigate(Dest.TouristAttractions.route) }, label = { Text("Attractions") }, leadingIcon = { Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp)) })
                AssistChip(onClick = { navController.navigate(Dest.NearbyPetrol.route) }, label = { Text("Petrol") }, leadingIcon = { Icon(Icons.Default.LocalGasStation, contentDescription = null, modifier = Modifier.size(16.dp)) })
                AssistChip(onClick = { navController.navigate(Dest.NearbyHospitals.route) }, label = { Text("Hospitals") }, leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(16.dp)) })
                AssistChip(onClick = { navController.navigate(Dest.NearbyPolice.route) }, label = { Text("Police") }, leadingIcon = { Icon(Icons.Default.LocalPolice, contentDescription = null, modifier = Modifier.size(16.dp)) })
            }
            Spacer(Modifier.height(16.dp))

            SectionHeader("All Places", subtitle = "${items.size} nearby points of interest")
            if (loading && items.isEmpty()) LoadingBlock()
            else items.forEach { p ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(
                                when (p.category) {
                                    "attraction" -> Gold
                                    "petrol" -> Green
                                    "hospital" -> Red
                                    "police" -> Maroon
                                    else -> InkMuted
                                }
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when (p.category) {
                                    "attraction" -> Icons.Default.PhotoCamera
                                    "petrol" -> Icons.Default.LocalGasStation
                                    "hospital" -> Icons.Default.LocalHospital
                                    "police" -> Icons.Default.LocalPolice
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.SemiBold)
                            Text(p.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (p.phone.isNotBlank()) Text("📞 ${p.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("${p.rating}★", fontWeight = FontWeight.SemiBold, color = Gold)
                    }
                }
            }
        }
    }
}
