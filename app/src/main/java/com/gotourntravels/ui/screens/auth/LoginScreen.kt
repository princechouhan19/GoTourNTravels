package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.GoTourTextField
import com.gotourntravels.ui.components.PrimaryButton
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.network.dto.AuthResponse
import com.gotourntravels.models.User
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.UiState

@Composable
fun LoginScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            val result = (state as UiState.Success).data
            val u = when (result) {
                is AuthResponse -> result.user
                is User -> result
                else -> null
            }
            val dest = if (u?.role == "admin") Dest.AdminDashboard.route else Dest.CustomerHome.route
            navController.navigate(dest) { popUpTo(0) }
            vm.resetState()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Brush.verticalGradient(listOf(Maroon, MaroonDark)))
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GT", color = Maroon, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text("Welcome Back", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Go Tour N Travels", color = GoldLight, fontSize = 12.sp)
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CreamDark),
            ) {
                FilterChipItem("Customer", !isAdmin) { isAdmin = false }
                FilterChipItem("Business Admin", isAdmin) { isAdmin = true }
            }
            Spacer(Modifier.height(16.dp))
            GoTourTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = if (isAdmin) "Admin Email" else "Email or Phone",
                leadingIcon = { Icon(if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person, contentDescription = null) },
                keyboardType = if (isAdmin) KeyboardType.Email else KeyboardType.Text
            )
            Spacer(Modifier.height(12.dp))
            GoTourTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardType = KeyboardType.Password
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate(Dest.ForgotPassword.route) }) {
                Text("Forgot password?", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            if (state is UiState.Error) {
                Text((state as UiState.Error).message, color = Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton(
                text = if (isAdmin) "Login as Admin" else "Login",
                isLoading = state is UiState.Loading
            ) {
                if (identifier.isNotBlank() && password.isNotBlank()) {
                    if (isAdmin) vm.adminLogin(identifier, password) else vm.login(identifier, password)
                }
            }
            Spacer(Modifier.height(10.dp))
            if (!isAdmin) {
                OutlinedButton(
                    onClick = { vm.bypassLogin(false) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Maroon)
                ) {
                    Text("Explore App (Demo Mode)", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }
            if (!isAdmin) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { navController.navigate(Dest.Register.route) }) {
                        Text("Register", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            // Dev hint — admin login
            if (isAdmin) {
                Spacer(Modifier.height(16.dp))
                Surface(color = CreamDark, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Demo Admin Login", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Maroon)
                        Text("Email: admin@gotourntravels.com", fontSize = 11.sp)
                        Text("Password: Admin@123", fontSize = 11.sp)
                        Text("(Run backend seed: npm run seed)", fontSize = 10.sp, color = InkMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.weight(1f).padding(4.dp).clip(RoundedCornerShape(8.dp)),
        color = if (selected) Maroon else Color.Transparent,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(label, color = if (selected) Color.White else InkMuted, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}
