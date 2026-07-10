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
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun ReviewsScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val reviews by vm.reviews.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadReviews() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Customer Reviews", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (reviews.isEmpty()) EmptyState("No reviews yet", "Customer reviews will show here", Icons.Default.Star)
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reviews) { r ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(r.user?.name ?: "Anonymous", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                repeat(r.rating) { Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp)) }
                            }
                            if (r.title.isNotBlank()) Text(r.title, fontWeight = FontWeight.SemiBold)
                            Text(r.comment, style = MaterialTheme.typography.bodyMedium)
                            r.vehicle?.let { Text("Vehicle: ${it.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            Text(r.createdAt.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AssistChip(onClick = { vm.updateReview(r.id, !r.isApproved, r.isFeatured, r.adminReply); vm.loadReviews() }, label = { Text(if (r.isApproved) "Unpublish" else "Publish") })
                                AssistChip(onClick = { vm.updateReview(r.id, r.isApproved, !r.isFeatured, r.adminReply); vm.loadReviews() }, label = { Text(if (r.isFeatured) "Unfeature" else "Feature") })
                            }
                        }
                    }
                }
            }
        }
    }
}
