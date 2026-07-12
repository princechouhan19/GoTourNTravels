package com.gotourntravels.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Advertisements", { navController.popBackStack() }, actions = {
            IconButton(onClick = { editing = null; showForm = true }) { Icon(Icons.Default.Add, "Add advertisement") }
        })
        if (showForm) AdForm(editing, vm, { showForm = false; editing = null }) { ad -> vm.saveAd(ad, editing != null) { showForm = false; editing = null } }
        else if (ads.isEmpty()) EmptyState("No ads", "Create an ad that customers can open for directions", Icons.Default.Campaign)
        else LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(ads, key = { it.id }) { ad ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(ad.imageUrl, ad.title, Modifier.size(64.dp), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
                            Text(ad.title, fontWeight = FontWeight.Bold); Text(ad.subtitle, style = MaterialTheme.typography.bodySmall)
                            Text("${ad.clicks} clicks • ${if (ad.isActive) "Live" else "Paused"}", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { editing = ad; showForm = true }) { Icon(Icons.Default.Edit, "Edit") }
                        IconButton(onClick = { vm.deleteAd(ad.id) }) { Icon(Icons.Default.DeleteOutline, "Delete", tint = Red) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdForm(ad: Advertisement?, vm: AdminViewModel, onCancel: () -> Unit, onSave: (Advertisement) -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(ad?.title ?: "") }; var subtitle by remember { mutableStateOf(ad?.subtitle ?: "") }
    var imageUrl by remember { mutableStateOf(ad?.imageUrl ?: "") }; var actionLabel by remember { mutableStateOf(ad?.actionLabel ?: "View location") }
    var location by remember { mutableStateOf(ad?.actionUrl?.removePrefix("geo:")?.substringBefore("?q=") ?: "") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { uriToFile(context, it)?.let { file -> vm.uploadAdImage(file) { imageUrl = it } } } }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(if (ad == null) "New Advertisement" else "Edit Advertisement", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Customers tap an ad to open its exact map location.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(14.dp)); GoTourTextField(title, { title = it }, "Business / offer title"); Spacer(Modifier.height(10.dp))
        GoTourTextField(subtitle, { subtitle = it }, "Short description"); Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = { launcher.launch("image/*") }, Modifier.fillMaxWidth()) { Icon(Icons.Default.Upload, null); Spacer(Modifier.width(8.dp)); Text(if (imageUrl.isBlank()) "Upload ad image" else "Replace ad image") }
        if (imageUrl.isNotBlank()) { Spacer(Modifier.height(8.dp)); AsyncImage(imageUrl, "Ad preview", Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop) }
        Spacer(Modifier.height(10.dp)); GoTourTextField(location, { location = it }, "Destination coordinates (lat,lng)")
        Text("Example: 24.5925,72.7078", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(10.dp)); GoTourTextField(actionLabel, { actionLabel = it }, "Action label")
        Spacer(Modifier.height(18.dp)); Row {
            OutlinedButton(onClick = onCancel, Modifier.weight(1f)) { Text("Cancel") }; Spacer(Modifier.width(8.dp))
            Button(onClick = { onSave((ad ?: Advertisement()).copy(title = title, subtitle = subtitle, imageUrl = imageUrl, actionLabel = actionLabel, actionUrl = "geo:$location?q=$location", endDate = ad?.endDate ?: java.util.Date(System.currentTimeMillis()+2592000000L).toString())) }, Modifier.weight(1f), enabled = title.isNotBlank() && imageUrl.isNotBlank() && location.contains(",")) { Text("Publish") }
        }
    }
}
