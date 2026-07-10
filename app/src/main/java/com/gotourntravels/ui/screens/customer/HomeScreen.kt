package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gotourntravels.models.Advertisement
import com.gotourntravels.models.Vehicle
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val vm: HomeViewModel = hiltViewModel()
    val featured by vm.featured.collectAsStateWithLifecycle()
    val allVehicles by vm.allVehicles.collectAsStateWithLifecycle()
    val ads by vm.ads.collectAsStateWithLifecycle()
    val business by vm.business.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Gold), contentAlignment = Alignment.Center) {
                        Text("GT", color = Maroon, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Go Tour N Travels", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Mount Abu • Rajasthan", color = GoldLight, fontSize = 11.sp)
                    }
                    IconButton(onClick = { navController.navigate(Dest.Notifications.route) }) {
                        BadgedBox(badge = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            if (loading && featured.isEmpty()) {
                LoadingBlock()
                return@Column
            }
            if (error != null && featured.isEmpty()) {
                ErrorState(error!!, onRetry = { vm.load() })
                return@Column
            }
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState).padding(bottom = 24.dp)) {
                // --- Search bar ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Maroon, MaroonDark)))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { navController.navigate(Dest.searchVehicles()) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search scooters, bikes, cars…", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        shape = RoundedCornerShape(24.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.White,
                            disabledContainerColor = Color.White.copy(alpha = 0.15f),
                            disabledBorderColor = Color.Transparent
                        )
                    )
                }

                // --- SOS strip ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(12.dp)).background(Red.copy(alpha = 0.1f)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Red)
                    Spacer(Modifier.width(8.dp))
                    Text("Emergency? Tap to raise SOS", modifier = Modifier.weight(1f), color = Red, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { navController.navigate(Dest.Sos.route) }) { Text("SOS", color = Red, fontWeight = FontWeight.Bold) }
                }

                // --- Banner ads ---
                if (ads.isNotEmpty()) {
                    val pager = rememberPagerState(pageCount = { ads.size })
                    val scope = rememberCoroutineScope()
                    LaunchedEffect(Unit) {
                        while (true) {
                            kotlinx.coroutines.delay(4000)
                            scope.launch { pager.animateScrollToPage((pager.currentPage + 1) % ads.size) }
                        }
                    }
                    HorizontalPager(state = pager, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { i ->
                        BannerAdCard(ads[i])
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // --- Vehicle type chips ---
                SectionHeader("Browse by Type", subtitle = "Pick a category to find your ride")
                LazyRow(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        listOf(
                            "scooter" to "🛵 Scooters",
                            "bike" to "🏍️ Bikes",
                            "activa" to "🛵 Activas",
                            "car" to "🚗 Cars",
                            "suv" to "🚙 SUVs"
                        )
                    ) { (key, label) ->
                        AssistChip(
                            onClick = { navController.navigate(Dest.searchVehicles(key)) },
                            label = { Text(label) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = CreamDark)
                        )
                    }
                }

                // --- Featured ---
                if (featured.isNotEmpty()) {
                    SectionHeader("Featured Rides", subtitle = "Hand-picked for your Mount Abu trip", action = "See all", onAction = { navController.navigate(Dest.searchVehicles()) })
                    LazyRow(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(featured.take(6)) { v ->
                            VehicleCard(v) { navController.navigate(Dest.vehicleDetails(v.id)) }
                        }
                    }
                }

                // --- All vehicles ---
                SectionHeader("All Vehicles", subtitle = "${allVehicles.size} rides available")
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    allVehicles.take(8).forEach { v ->
                        VehicleCard(v) { navController.navigate(Dest.vehicleDetails(v.id)) }
                    }
                }

                // --- Quick links ---
                SectionHeader("Explore Mount Abu", subtitle = "Tourist map & nearby services")
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickLink("Tourist Map", Icons.Default.Map) { navController.navigate(Dest.TouristMap.route) }
                    QuickLink("Attractions", Icons.Default.PhotoCamera) { navController.navigate(Dest.TouristAttractions.route) }
                    QuickLink("Petrol", Icons.Default.LocalGasStation) { navController.navigate(Dest.NearbyPetrol.route) }
                    QuickLink("Hospitals", Icons.Default.LocalHospital) { navController.navigate(Dest.NearbyHospitals.route) }
                    QuickLink("Police", Icons.Default.LocalPolice) { navController.navigate(Dest.NearbyPolice.route) }
                }

                Spacer(Modifier.height(20.dp))
                if (business != null) {
                    Surface(
                        color = CreamDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Need help?", fontWeight = FontWeight.Bold, color = Maroon)
                            Spacer(Modifier.height(4.dp))
                            Text("Call us: ${business!!.phone}", style = MaterialTheme.typography.bodyMedium)
                            Text(business!!.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            SecondaryButton("Help & Support", onClick = { navController.navigate(Dest.HelpSupport.route) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerAdCard(ad: Advertisement) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = ad.imageUrl,
            contentDescription = ad.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, MaroonDark.copy(alpha = 0.8f))))) {
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(ad.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (ad.subtitle.isNotBlank()) Text(ad.subtitle, color = GoldLight, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RowScope.QuickLink(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(CreamDark).clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = Maroon, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Ink)
    }
}
