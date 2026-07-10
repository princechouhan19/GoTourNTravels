package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gotourntravels.ui.components.GoTourTopBar
import com.gotourntravels.ui.components.SectionHeader
import com.gotourntravels.ui.theme.Gold
import com.gotourntravels.ui.theme.GoldLight
import com.gotourntravels.ui.theme.Maroon
import com.gotourntravels.ui.theme.MaroonDark

@Composable
fun AboutScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "About", onBack = { navController.popBackStack() })
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.verticalGradient(listOf(Maroon, MaroonDark))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Gold),
                        contentAlignment = Alignment.Center
                    ) { Text("GT", color = Maroon, fontWeight = FontWeight.Bold, fontSize = 28.sp) }
                    Spacer(Modifier.height(8.dp))
                    Text("Go Tour N Travels", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Mount Abu • Rajasthan • India", color = GoldLight, fontSize = 12.sp)
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text("About Us", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Go Tour N Travels is a Mount Abu–based travel agency and vehicle rental business. " +
                    "From scooters and bikes for the adventurous traveller to cars with drivers for families and groups, " +
                    "we have been serving visitors to Rajasthan's only hill station for over a decade. " +
                    "Our services include taxi services, self-drive scooters, cars with drivers, airport transfers, " +
                    "sightseeing tours, and complete Mount Abu tour packages.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                SectionHeader("Our Services")
                ServiceItem("🛵 Scooter & Bike Rentals", "Self-drive Activas, Jupiters, Royal Enfields and more")
                ServiceItem("🚗 Cars with Driver", "Swift Dzire, Ertiga, Innova for family & group travel")
                ServiceItem("✈️ Airport Transfers", "Udaipur & Ahmedabad airport pickups and drops")
                ServiceItem("🏔️ Sightseeing Tours", "Dilwara, Nakki Lake, Sunset Point, Guru Shikhar & more")
                ServiceItem("📋 Tour Packages", "1-3 day curated Mount Abu experiences")
                ServiceItem("🆘 24x7 SOS Support", "On-road emergency assistance across Mount Abu")

                Spacer(Modifier.height(16.dp))
                SectionHeader("Why Choose Us")
                Text("• Verified, well-maintained vehicles\n• Transparent pricing — no hidden charges\n• Local drivers who know every shortcut\n• Instant digital invoices & receipts\n• Real-time tracking during active rentals\n• Friendly Mount Abu hospitality", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(20.dp))
                Text("App Version 1.0.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ServiceItem(title: String, desc: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Maroon)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
