package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val stats by vm.stats.collectAsStateWithLifecycle()
    val topVehicles by vm.topVehicles.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadDashboard() }

    val scroll = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Maroon, MaroonDark)))
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column {
                Text("Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                Text("Go Tour N Travels", color = GoldLight, fontSize = 12.sp)
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scroll)) {
            if (loading && stats == null) { LoadingBlock(); return@Column }
            error?.let { ErrorState(it) { vm.loadDashboard() }; return@Column }
            stats?.let { s ->
                // KPI grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kpi("Bookings", s.totalBookings.toString(), Icons.Default.Receipt, Maroon, modifier = Modifier.weight(1f))
                    Kpi("Active", s.activeBookings.toString(), Icons.Default.DirectionsCar, Gold, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kpi("Customers", s.totalCustomers.toString(), Icons.Default.People, Green, modifier = Modifier.weight(1f))
                    Kpi("Vehicles", s.totalVehicles.toString(), Icons.Default.TwoWheeler, MaroonDark, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kpi("Revenue", "₹${s.totalRevenue.toInt()}", Icons.Default.CurrencyRupee, Green, modifier = Modifier.weight(1f))
                    Kpi("Open SOS", s.openSos.toString(), Icons.Default.Warning, Red, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))

                // Quick actions
                SectionHeader("Quick Actions")
                AdminActionRow(Icons.Default.TwoWheeler, "Vehicles") { navController.navigate(Dest.AdminVehicles.route) }
                AdminActionRow(Icons.Default.ReceiptLong, "Bookings") { navController.navigate(Dest.AdminBookings.route) }
                AdminActionRow(Icons.Default.People, "Customers") { navController.navigate(Dest.AdminCustomers.route) }
                AdminActionRow(Icons.Default.CurrencyRupee, "Earnings") { navController.navigate(Dest.AdminEarnings.route) }
                AdminActionRow(Icons.Default.Insights, "Analytics") { navController.navigate(Dest.AdminAnalytics.route) }
                AdminActionRow(Icons.Default.Warning, "SOS Requests") { navController.navigate(Dest.AdminSos.route) }
                AdminActionRow(Icons.Default.Star, "Reviews") { navController.navigate(Dest.AdminReviews.route) }
                AdminActionRow(Icons.Default.Campaign, "Advertisements") { navController.navigate(Dest.AdminAds.route) }
                AdminActionRow(Icons.Default.Business, "Business Profile") { navController.navigate(Dest.AdminBusinessProfile.route) }
                Spacer(Modifier.height(20.dp))

                // Top vehicles
                if (topVehicles.isNotEmpty()) {
                    SectionHeader("Top Performing Vehicles")
                    topVehicles.forEach { v ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(v.name, fontWeight = FontWeight.SemiBold)
                                    Text("${v.totalBookings} bookings • ⭐ ${v.rating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusChip(v.status)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))

                // Back to customer view
                OutlinedButton(
                    onClick = { navController.navigate(Dest.CustomerHome.route) { popUpTo(0) } },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Switch to Customer View")
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun RowScope.Kpi(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AdminActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp), onClick = onClick) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Maroon)
            Spacer(Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
