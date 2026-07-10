package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.AuthViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val dark by vm.darkMode.collectAsStateWithLifecycle()
    var push by remember { mutableStateOf(true) }
    var sms by remember { mutableStateOf(true) }
    var promo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Settings", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            SectionHeader("Appearance")
            SettingRow("Dark mode", "Use dark theme throughout the app", dark) { vm.toggleDarkMode(it) }
            Spacer(Modifier.height(12.dp))
            SectionHeader("Notifications")
            SettingRow("Push notifications", "Booking & payment alerts", push) { push = it }
            SettingRow("SMS alerts", "OTP and booking confirmations", sms) { sms = it }
            SettingRow("Promotional offers", "Discounts and tour packages", promo) { promo = it }
            Spacer(Modifier.height(12.dp))
            SectionHeader("About")
            InfoRow("App version", "1.0.0")
            InfoRow("Built for", "Go Tour N Travels, Mount Abu")
            InfoRow("Support", "+91 90000 00000")
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Medium)
        }
    }
}
