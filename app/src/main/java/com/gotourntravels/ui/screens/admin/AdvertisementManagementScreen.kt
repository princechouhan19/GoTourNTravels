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
import com.gotourntravels.models.Advertisement
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun AdvertisementManagementScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val ads by vm.ads.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Advertisement?>(null) }
    LaunchedEffect(Unit) { vm.loadAds() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Advertisements", onBack = { navController.popBackStack() }, actions = {
            IconButton(onClick = { editing = null; showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (showForm) {
                AdForm(editing, onCancel = { showForm = false; editing = null }) { ad ->
                    vm.saveAd(ad, editing != null) { showForm = false; editing = null }
                }
                Spacer(Modifier.height(16.dp))
            }
            if (ads.isEmpty()) EmptyState("No ads", "Add a banner for the home screen", Icons.Default.Campaign)
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ads) { ad ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(ad.title, fontWeight = FontWeight.Bold)
                            Text(ad.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Placement: ${ad.placement} • Active: ${ad.isActive}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${ad.clicks} clicks • ${ad.impressions} impressions", style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AssistChip(onClick = { editing = ad; showForm = true }, label = { Text("Edit") })
                                AssistChip(onClick = { vm.deleteAd(ad.id) }, label = { Text("Delete") }, leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Red) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdForm(ad: Advertisement?, onCancel: () -> Unit, onSave: (Advertisement) -> Unit) {
    var title by remember { mutableStateOf(ad?.title ?: "") }
    var subtitle by remember { mutableStateOf(ad?.subtitle ?: "") }
    var imageUrl by remember { mutableStateOf(ad?.imageUrl ?: "") }
    var actionLabel by remember { mutableStateOf(ad?.actionLabel ?: "Book Now") }
    var placement by remember { mutableStateOf(ad?.placement ?: "home_banner") }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(if (ad == null) "New Advertisement" else "Edit Advertisement", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            GoTourTextField(title, { title = it }, "Title")
            Spacer(Modifier.height(8.dp))
            GoTourTextField(subtitle, { subtitle = it }, "Subtitle")
            Spacer(Modifier.height(8.dp))
            GoTourTextField(imageUrl, { imageUrl = it }, "Image URL")
            Spacer(Modifier.height(8.dp))
            GoTourTextField(actionLabel, { actionLabel = it }, "Action button label")
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("home_banner" to "Home Banner", "home_strip" to "Home Strip", "search_top" to "Search Top").forEach { (k, l) ->
                    FilterChip(selected = placement == k, onClick = { placement = k }, label = { Text(l) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = {
                        val a = (ad ?: Advertisement()).copy(
                            title = title, subtitle = subtitle, imageUrl = imageUrl, actionLabel = actionLabel, placement = placement,
                            endDate = ad?.endDate ?: java.util.Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000).toString()
                        )
                        onSave(a)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() && imageUrl.isNotBlank()
                ) { Text("Save") }
            }
        }
    }
}
