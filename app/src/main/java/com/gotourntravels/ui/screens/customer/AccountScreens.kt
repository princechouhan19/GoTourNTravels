package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.models.Address
import com.gotourntravels.ui.components.GoTourTextField
import com.gotourntravels.ui.components.GoTourTopBar
import com.gotourntravels.ui.components.PrimaryButton
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(navController: NavController) {
    val auth: AuthViewModel = hiltViewModel()
    val vm: ProfileViewModel = hiltViewModel()
    val user by auth.user.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val success by vm.success.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var name by remember(user) { mutableStateOf(user?.name.orEmpty()) }
    var email by remember(user) { mutableStateOf(user?.email.orEmpty()) }
    var phone by remember(user) { mutableStateOf(user?.phone.orEmpty()) }

    LaunchedEffect(success) { if (success != null) navController.popBackStack() }
    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Edit Profile", onBack = { navController.popBackStack() })
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Personal details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Keep your booking contact information up to date.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            GoTourTextField(name, { name = it }, "Full name")
            GoTourTextField(email, { email = it }, "Email address", keyboardType = KeyboardType.Email)
            GoTourTextField(phone, { phone = it }, "Phone number", keyboardType = KeyboardType.Phone)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(4.dp))
            PrimaryButton("Save changes", isLoading = loading) { vm.updateProfile(name, email, phone) }
        }
    }
}

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val vm: ProfileViewModel = hiltViewModel()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val success by vm.success.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    LaunchedEffect(success) { if (success != null) navController.popBackStack() }

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Change Password", onBack = { navController.popBackStack() })
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Security", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Use at least 8 characters and do not reuse a previous password.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            GoTourTextField(current, { current = it }, "Current password", isPassword = true)
            GoTourTextField(next, { next = it }, "New password", isPassword = true)
            GoTourTextField(confirm, { confirm = it }, "Confirm new password", isPassword = true)
            val validation = if (next.isNotEmpty() && next != confirm) "Passwords do not match" else error
            validation?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            PrimaryButton("Update password", isLoading = loading, enabled = current.isNotBlank() && next.length >= 8 && next == confirm) {
                vm.changePassword(current, next)
            }
        }
    }
}

@Composable
fun SavedAddressesScreen(navController: NavController) {
    val auth: AuthViewModel = hiltViewModel()
    val vm: ProfileViewModel = hiltViewModel()
    val user by auth.user.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    var adding by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("") }
    var line1 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Mount Abu") }
    val addresses = user?.addresses.orEmpty()

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar("Saved Addresses", onBack = { navController.popBackStack() }, actions = {
            IconButton(onClick = { adding = true }) { Icon(Icons.Default.Add, "Add address", tint = MaterialTheme.colorScheme.primary) }
        })
        if (addresses.isEmpty()) {
            Column(Modifier.padding(24.dp)) {
                Text("No saved addresses", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Add a pickup address to make future bookings faster.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(addresses) { address ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(address.label.ifBlank { "Address" }, fontWeight = FontWeight.SemiBold)
                                Text(address.line1, style = MaterialTheme.typography.bodyMedium)
                                Text("${address.city}, ${address.state} ${address.pincode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { vm.updateAddresses(addresses - address) }) { Icon(Icons.Default.DeleteOutline, "Remove address") }
                        }
                    }
                }
            }
        }
    }
    if (adding) AlertDialog(
        onDismissRequest = { adding = false },
        title = { Text("Add address") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GoTourTextField(label, { label = it }, "Label (Home, Hotel)")
            GoTourTextField(line1, { line1 = it }, "Address line")
            GoTourTextField(city, { city = it }, "City")
        } },
        confirmButton = { TextButton(enabled = label.isNotBlank() && line1.isNotBlank() && !loading, onClick = {
            vm.updateAddresses(addresses + Address(label = label, line1 = line1, city = city, state = "Rajasthan")); adding = false
        }) { Text("Save") } },
        dismissButton = { TextButton(onClick = { adding = false }) { Text("Cancel") } }
    )
}
