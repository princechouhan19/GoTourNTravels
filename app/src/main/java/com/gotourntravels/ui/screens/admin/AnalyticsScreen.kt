package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun AnalyticsScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val bookings by vm.bookingsChart.collectAsStateWithLifecycle()
    val revenue by vm.revenueByType.collectAsStateWithLifecycle()
    val top by vm.topVehicles.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadDashboard() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Analytics", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            SectionHeader("Bookings — last 14 days", subtitle = "${bookings.size} days")
            Card(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(12.dp)) {
                if (bookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No data", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LineChart(bookings.map { it.count.toFloat() }, Maroon)
                }
            }
            Spacer(Modifier.height(20.dp))

            SectionHeader("Revenue — last 14 days")
            Card(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(12.dp)) {
                if (bookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No data", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LineChart(bookings.map { it.revenue.toFloat() }, Green)
                }
            }
            Spacer(Modifier.height(20.dp))

            SectionHeader("Revenue by Vehicle Type")
            revenue.forEach { r ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(r._id.replaceFirstChar { it.uppercase() }, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Text("₹${r.revenue.toInt()} (${r.count})", color = Green, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))

            SectionHeader("Top Vehicles")
            top.forEach { v ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
                        Text(v.name, fontWeight = FontWeight.SemiBold)
                        Text("${v.totalBookings} bookings • ⭐ ${v.rating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun LineChart(values: List<Float>, color: Color) {
    if (values.size < 2) return
    val max = values.max().coerceAtLeast(1f)
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val step = w / (values.size - 1)
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = i * step
            val y = h - (v / max) * (h - 20f) - 10f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = color, style = Stroke(width = 4f))
        values.forEachIndexed { i, v ->
            val x = i * step
            val y = h - (v / max) * (h - 20f) - 10f
            drawCircle(color = color, radius = 4f, center = Offset(x, y))
        }
    }
}
