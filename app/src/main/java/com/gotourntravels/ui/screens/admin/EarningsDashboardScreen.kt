package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun EarningsDashboardScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val stats by vm.stats.collectAsStateWithLifecycle()
    val revenueByType by vm.revenueByType.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadDashboard() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Earnings", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            stats?.let { s ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Green)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Total Revenue", color = MaterialTheme.colorScheme.onPrimary)
                        Text("₹${s.totalRevenue.toInt()}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("This month: ₹${s.monthRevenue.toInt()}", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), color = CreamDark) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total bookings")
                            Text(s.totalBookings.toString(), fontWeight = FontWeight.Bold, color = Maroon, fontSize = 24.sp)
                        }
                    }
                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), color = CreamDark) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("This month")
                            Text(s.monthBookings.toString(), fontWeight = FontWeight.Bold, color = Maroon, fontSize = 24.sp)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                SectionHeader("Revenue by Vehicle Type")
                if (revenueByType.isEmpty()) Text("No data yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                revenueByType.forEach { r ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(r._id.replaceFirstChar { it.uppercase() }, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text("${r.count} bookings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(16.dp))
                        Text("₹${r.revenue.toInt()}", fontWeight = FontWeight.Bold, color = Green)
                    }
                    LinearProgressIndicator(
                        progress = { (r.revenue / (revenueByType.maxOf { it.revenue }.coerceAtLeast(1.0))).toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Green
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
