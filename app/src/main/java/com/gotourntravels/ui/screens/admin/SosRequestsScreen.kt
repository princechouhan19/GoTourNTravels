package com.gotourntravels.ui.screens.admin

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
import com.gotourntravels.viewmodel.SosViewModel

@Composable
fun SosRequestsScreen(navController: NavController) {
    val vm: SosViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadAll() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "SOS Requests", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            error?.let { ErrorState(it) { vm.loadAll() }; return@Column }
            if (items.isEmpty()) EmptyState("No SOS requests", "All clear — no emergencies reported", Icons.Default.CheckCircle)
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { s ->
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${s.sosNumber}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    StatusChip(s.status)
                                }
                                s.user?.let { Text("${it.name} • ${it.phone}", style = MaterialTheme.typography.bodySmall) }
                                Text("Type: ${s.type.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (s.description.isNotBlank()) Text("Description: ${s.description}", style = MaterialTheme.typography.bodySmall)
                                Text("📍 ${s.location.address} (${s.location.lat}, ${s.location.lng})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(s.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (s.status == "open") {
                                        AssistChip(onClick = { vm.acknowledge(s.id) }, label = { Text("Acknowledge") })
                                    }
                                    if (s.status == "acknowledged" || s.status == "open") {
                                        AssistChip(onClick = { vm.resolve(s.id, "Resolved by admin") }, label = { Text("Resolve") })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
