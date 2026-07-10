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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun CustomerManagementScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val customers by vm.customers.collectAsStateWithLifecycle()
    var search by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { vm.loadCustomers() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Customer Management", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            GoTourTextField(search, { search = it; vm.loadCustomers(it) }, "Search by name, email, phone", leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, keyboardType = KeyboardType.Text)
            Spacer(Modifier.height(12.dp))
            if (customers.isEmpty()) EmptyState("No customers", "Search for customers above", Icons.Default.People)
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(customers) { c ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(c.name, fontWeight = FontWeight.Bold)
                                Text(c.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(c.phone, style = MaterialTheme.typography.bodySmall)
                            }
                            StatusChip(if (c.isBlocked) "blocked" else "active")
                            Switch(checked = !c.isBlocked, onCheckedChange = { vm.toggleBlock(c.id, it.not()) })
                        }
                    }
                }
            }
        }
    }
}
