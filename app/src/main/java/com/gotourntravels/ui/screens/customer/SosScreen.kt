package com.gotourntravels.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.location.LocationProvider
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.SosViewModel

@Composable
fun SosScreen(navController: NavController) {
    val vm: SosViewModel = hiltViewModel()
    val ctx = LocalContext.current
    val loading by vm.loading.collectAsStateWithLifecycle()
    val created by vm.created.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val items by vm.items.collectAsStateWithLifecycle()

    val locationProvider = remember { LocationProvider(ctx) }
    var type by remember { mutableStateOf("breakdown") }
    var description by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(24.5925) }
    var lng by remember { mutableStateOf(72.7156) }

    LaunchedEffect(Unit) {
        runCatching { locationProvider.currentLocation() }.getOrNull()?.let {
            lat = it.latitude; lng = it.longitude
        }
        vm.loadMine()
    }

    LaunchedEffect(created) {
        if (created != null) {
            Toast.makeText(ctx, "SOS sent! Our team has been alerted.", Toast.LENGTH_LONG).show()
            vm.loadMine()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "SOS Emergency", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            // Big red banner
            Surface(
                color = Red,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("In case of emergency", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Tap to call 112 directly", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { /* TODO: ACTION_CALL tel:112 */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Red)
                        Spacer(Modifier.width(8.dp))
                        Text("CALL 112", color = Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("Send SOS to Go Tour N Travels", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Our team will be alerted with your live location and contact details.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            Text("Type of emergency", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("accident" to "Accident", "breakdown" to "Breakdown", "medical" to "Medical", "safety" to "Safety").forEach { (k, l) ->
                    item(k) {
                        FilterChip(
                            selected = type == k,
                            onClick = { type = k },
                            label = { Text(l) },
                            modifier = Modifier.heightIn(min = 40.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Description (optional)", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp), placeholder = { Text("Describe the situation…") })
            Spacer(Modifier.height(16.dp))

            Surface(color = CreamDark, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Maroon, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Your live location", fontWeight = FontWeight.SemiBold)
                    }
                    Text("Lat: $lat, Lng: $lng", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))

            error?.let { Text(it, color = Red); Spacer(Modifier.height(8.dp)) }

            Button(
                onClick = { vm.raise(type, description, lat, lng, "Mount Abu") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(12.dp),
                enabled = !loading
            ) {
                Icon(Icons.Default.Sos, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(if (loading) "SENDING…" else "SEND SOS NOW", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))

            if (items.isNotEmpty()) {
                SectionHeader("Your SOS history", subtitle = "${items.size} requests")
                items.forEach { s ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("#${s.sosNumber}", fontWeight = FontWeight.SemiBold)
                                Text(s.type.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(s.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusChip(s.status)
                        }
                    }
                }
            }
        }
    }
}
