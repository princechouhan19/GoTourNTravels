package com.gotourntravels.ui.screens.customer

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
import com.gotourntravels.viewmodel.HomeViewModel

@Composable
fun HelpSupportScreen(navController: NavController) {
    val vm: HomeViewModel = hiltViewModel()
    val business by vm.business.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Help & Support", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            business?.let { b ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Maroon)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(b.name, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(b.tagline, color = GoldLight, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(12.dp))
                        HelpRow(Icons.Default.Phone, "Call us", b.phone)
                        HelpRow(Icons.Default.Email, "Email", b.email)
                        HelpRow(Icons.Default.LocationOn, "Visit us", "${b.address.line1}, ${b.address.city}, ${b.address.state} - ${b.address.pincode}")
                        if (b.workingHours.is24x7) HelpRow(Icons.Default.Schedule, "Hours", "Open 24x7")
                        else HelpRow(Icons.Default.Schedule, "Hours", "${b.workingHours.open} - ${b.workingHours.close}")
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (b.emergencyContacts.isNotEmpty()) {
                    SectionHeader("Emergency Contacts")
                    b.emergencyContacts.forEach { c ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Maroon)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(c.name, fontWeight = FontWeight.SemiBold)
                                    Text("${c.role} • ${c.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Green)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            SectionHeader("Frequently Asked Questions")
            FaqItem("How do I book a vehicle?",
                "Browse available vehicles from Home or Search, open the vehicle details, tap Book Now, choose rental type and dates, then complete payment via Razorpay.")
            FaqItem("Is the security deposit refundable?",
                "Yes — the security deposit is fully refundable after vehicle return inspection. It is processed within 3-5 working days.")
            FaqItem("Can I rent a vehicle with a driver?",
                "Yes — cars marked 'with driver' include a trained local driver. The driver's name and phone are shown in vehicle details.")
            FaqItem("What documents do I need?",
                "A valid driving licence (for self-drive), government-issued photo ID, and the credit/debit card used for payment.")
            FaqItem("How do I cancel a booking?",
                "Open the booking summary and tap Cancel Booking. Free cancellation up to 2 hours before pickup; after that 50% of the base amount is non-refundable.")
            FaqItem("What if the vehicle breaks down?",
                "Use the SOS button in your active booking screen or call our 24x7 helpline. We arrange a replacement vehicle or refund as appropriate.")
            FaqItem("Do you offer airport transfers?",
                "Yes — we provide airport transfers to/from Udaipur and Ahmedabad airports. Book an Innova or Ertiga with driver and mention 'airport transfer' in notes.")
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Call Business Now") { /* ACTION_DIAL */ }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun HelpRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Gold)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = GoldLight, fontSize = 11.sp)
            Text(value, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp), onClick = { expanded = !expanded }) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(question, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            if (expanded) {
                Spacer(Modifier.height(6.dp))
                Text(answer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
