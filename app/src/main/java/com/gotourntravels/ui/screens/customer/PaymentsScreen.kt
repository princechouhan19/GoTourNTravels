package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.PaymentsViewModel

@Composable
fun PaymentsScreen(navController: NavController) {
    val vm: PaymentsViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Payments & Invoices", onBack = { navController.popBackStack() })
        val totalPaid = items.filter { it.status == "captured" }.sumOf { it.amount }
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total paid to Go Tour N Travels", color = MaterialTheme.colorScheme.onPrimary)
                Text("₹$totalPaid", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
            }
        }
        when {
            loading && items.isEmpty() -> LoadingBlock()
            error != null -> ErrorState(error!!, onRetry = { vm.load() })
            items.isEmpty() -> EmptyState("No payments yet", "Your payment history will appear here", Icons.Default.Receipt)
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { p ->
                    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.invoiceNumber.ifBlank { p.paymentNumber }, fontWeight = FontWeight.SemiBold)
                                Text(p.booking?.bookingNumber ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(p.paidAt?.take(10) ?: "Pending", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${p.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                StatusChip(p.status)
                            }
                        }
                    }
                }
            }
        }
    }
}
