package com.gotourntravels.ui.screens.customer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.ProfileViewModel
import com.gotourntravels.ui.screens.admin.uriToFile
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(navController: NavController) {
    val authVm: AuthViewModel = hiltViewModel()
    val vm: ProfileViewModel = hiltViewModel()
    val user by authVm.user.collectAsStateWithLifecycle()
    val dark by authVm.darkMode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> uri?.let { uriToFile(context, it)?.let(vm::uploadAvatar) } }

    LaunchedEffect(Unit) { vm.refresh() }

    val scroll = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(listOf(Maroon, MaroonDark)))
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Gold).clickable { photoLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.avatar?.isNotBlank() == true) AsyncImage(user!!.avatar, "Profile photo", Modifier.fillMaxSize())
                    else Text(user?.name?.take(2)?.uppercase() ?: "GT", color = Maroon, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(user?.name ?: "Guest", color = Color.White, fontWeight = FontWeight.Bold)
                Text(user?.email ?: "", color = GoldLight, fontSize = 12.sp)
                Text("Tap photo to change", color = Color.White.copy(alpha = .8f), fontSize = 10.sp)
                if (user?.isVerified == true) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Gold, modifier = Modifier.size(12.dp))
                        Text(" Verified", color = Gold, fontSize = 10.sp)
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scroll)) {
            // Account
            ProfileGroup("Account") {
                ProfileItem(Icons.Default.Person, "Edit Profile") { navController.navigate(Dest.EditProfile.route) }
                ProfileItem(Icons.Default.Lock, "Change Password") { navController.navigate(Dest.ChangePassword.route) }
                ProfileItem(Icons.Default.Home, "Saved Addresses") { navController.navigate(Dest.SavedAddresses.route) }
                ProfileItem(Icons.Default.ReceiptLong, "My Bookings") { navController.navigate(Dest.Bookings.route) }
                ProfileItem(Icons.Default.Payment, "Payments & Invoices") { navController.navigate(Dest.Payments.route) }
            }
            Spacer(Modifier.height(12.dp))
            // Safety
            ProfileGroup("Safety & Emergency") {
                ProfileItem(Icons.Default.Warning, "SOS Emergency", color = Red) { navController.navigate(Dest.Sos.route) }
                ProfileItem(Icons.Default.Map, "Tourist Map") { navController.navigate(Dest.TouristMap.route) }
                ProfileItem(Icons.Default.LocalHospital, "Nearby Hospitals") { navController.navigate(Dest.NearbyHospitals.route) }
            }
            Spacer(Modifier.height(12.dp))
            // Preferences
            ProfileGroup("Preferences") {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = Maroon)
                    Spacer(Modifier.width(16.dp))
                    Text("Dark mode", modifier = Modifier.weight(1f))
                    Switch(checked = dark, onCheckedChange = { authVm.toggleDarkMode(it) })
                }
                ProfileItem(Icons.Default.Notifications, "Notifications") { navController.navigate(Dest.Notifications.route) }
                ProfileItem(Icons.Default.Settings, "Settings") { navController.navigate(Dest.Settings.route) }
            }
            Spacer(Modifier.height(12.dp))
            ProfileGroup("Support") {
                ProfileItem(Icons.Default.Help, "Help & Support") { navController.navigate(Dest.HelpSupport.route) }
                ProfileItem(Icons.Default.Info, "About Go Tour N Travels") { navController.navigate(Dest.About.route) }
            }
            Spacer(Modifier.height(20.dp))
            // Admin entry if applicable
            if (user?.role == "admin") {
                Button(
                    onClick = { navController.navigate(Dest.AdminDashboard.route) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Maroon)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Open Admin Dashboard", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
            }

            // Logout
            OutlinedButton(
                onClick = { authVm.logout(); navController.navigate(Dest.Login.route) { popUpTo(0) } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                border = androidx.compose.foundation.BorderStroke(1.dp, Red)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProfileGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Maroon, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
            content()
        }
    }
}

@Composable
private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color = Maroon, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
