package com.gotourntravels.ui.screens.customer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.BuildConfig
import com.gotourntravels.location.LocationProvider
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.SosViewModel
import kotlinx.coroutines.launch

@Composable
fun SosScreen(navController: NavController) {
    val vm: SosViewModel = hiltViewModel(); val context = LocalContext.current; val scope = rememberCoroutineScope()
    val loading by vm.loading.collectAsStateWithLifecycle(); val created by vm.created.collectAsStateWithLifecycle(); val error by vm.error.collectAsStateWithLifecycle(); val items by vm.items.collectAsStateWithLifecycle()
    val locationProvider = remember { LocationProvider(context) }
    var type by remember { mutableStateOf("breakdown") }; var note by remember { mutableStateOf("") }; var lat by remember { mutableStateOf(24.5925) }; var lng by remember { mutableStateOf(72.7156) }
    fun dial(number: String) = context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
    LaunchedEffect(Unit) { locationProvider.currentLocation()?.let { lat = it.latitude; lng = it.longitude }; vm.loadMine() }
    LaunchedEffect(created) { if (created != null) { Toast.makeText(context, "SOS sent with your live location.", Toast.LENGTH_LONG).show(); vm.loadMine() } }

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Emergency & roadside help", { navController.popBackStack() })
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Red), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) { Text("Need urgent help?", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Call emergency services first. We will also alert Go Tour N Travels with your GPS location.", color = Color.White); Spacer(Modifier.height(12.dp)); Button(onClick = { dial("112") }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Icon(Icons.Default.Phone, null, tint = Red); Spacer(Modifier.width(8.dp)); Text("Call 112 emergency", color = Red) } }
            }
            Spacer(Modifier.height(16.dp)); Text("Quick contacts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ContactRow(Icons.Default.LocalPolice, "Police emergency", "100 / 112", "100", ::dial)
            ContactRow(Icons.Default.LocalHospital, "Global Hospital, Mount Abu", "02974 238347", "02974238347", ::dial)
            ContactRow(Icons.Default.LocalGasStation, "Vehicle stopped / fuel emergency", "Contact Go Tour N Travels", BuildConfig.BUSINESS_PHONE.removePrefix("+"), ::dial)
            Spacer(Modifier.height(18.dp)); Text("Send live SOS to Go Tour N Travels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Your latest location is sent to the admin immediately.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp)); LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("accident" to "Accident", "breakdown" to "Vehicle stopped", "medical" to "Medical", "safety" to "Safety").forEach { (key, label) -> item { FilterChip(type == key, { type = key }, { Text(label) }) } } }
            Spacer(Modifier.height(10.dp)); OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text("What happened? (optional)") }, minLines = 3)
            Spacer(Modifier.height(10.dp)); Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.MyLocation, null); Spacer(Modifier.width(8.dp)); Text("Live GPS: %.5f, %.5f".format(lat, lng), style = MaterialTheme.typography.bodySmall) } }
            Spacer(Modifier.height(12.dp)); error?.let { Text(it, color = Red) }
            Button(onClick = { scope.launch { locationProvider.currentLocation()?.let { lat = it.latitude; lng = it.longitude }; vm.raise(type, note, lat, lng, "Current GPS location") } }, Modifier.fillMaxWidth().height(54.dp), enabled = !loading, colors = ButtonDefaults.buttonColors(containerColor = Red)) { Icon(Icons.Default.Sos, null); Spacer(Modifier.width(8.dp)); Text(if (loading) "Sending SOS…" else "Send SOS with live location", fontWeight = FontWeight.Bold) }
            if (items.isNotEmpty()) { Spacer(Modifier.height(20.dp)); SectionHeader("Your SOS requests"); items.forEach { s -> Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(s.type.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold); Text(s.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.bodySmall) }; StatusChip(s.status) } } } }
        }
    }
}

@Composable private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, number: String, onDial: (String) -> Unit) = Card(onClick = { onDial(number) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.Phone, null) } }
