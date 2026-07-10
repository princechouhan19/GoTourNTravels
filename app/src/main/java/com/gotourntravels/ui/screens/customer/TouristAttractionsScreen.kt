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
import androidx.compose.ui.graphics.Brush
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
fun TouristAttractionsScreen(navController: NavController) {
    val vm: PlacesViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.load("attraction") }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Tourist Attractions", onBack = { navController.popBackStack() })
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Brush.verticalGradient(listOf(Maroon, MaroonDark)))
        ) {
            Column(modifier = Modifier.padding(16.dp).align(Alignment.CenterStart)) {
                Text("Explore Mount Abu", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Text("Curated tourist spots in & around town", color = GoldLight, style = MaterialTheme.typography.bodySmall)
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (loading && items.isEmpty()) LoadingBlock()
            else items.forEach { p ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(14.dp)) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                .background(Brush.verticalGradient(listOf(Gold, Maroon))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                        }
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("⭐ ${p.rating}", color = Gold, fontWeight = FontWeight.SemiBold)
                            }
                            Text(p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text("📍 ${p.address}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
