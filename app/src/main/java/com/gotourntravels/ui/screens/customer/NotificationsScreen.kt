package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(navController: NavController) {
    val vm: NotificationsViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val unread by vm.unread.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(
            title = "Notifications",
            onBack = { navController.popBackStack() },
            actions = {
                if (unread > 0) {
                    TextButton(onClick = { vm.markAllRead() }) {
                        Text("Mark all read", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        )
        when {
            loading && items.isEmpty() -> LoadingBlock()
            items.isEmpty() -> EmptyState("No notifications", "Booking, payment, and promo alerts will show here", Icons.Default.Notifications)
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { n ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (!n.isRead) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                when (n.type) {
                                    "booking" -> Icons.Default.Receipt
                                    "payment" -> Icons.Default.Payment
                                    "sos" -> Icons.Default.Warning
                                    "promo" -> Icons.Default.LocalOffer
                                    "review" -> Icons.Default.Star
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(n.title, fontWeight = FontWeight.SemiBold)
                                Text(n.body, style = MaterialTheme.typography.bodySmall)
                                Text(n.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (!n.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.Top)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
