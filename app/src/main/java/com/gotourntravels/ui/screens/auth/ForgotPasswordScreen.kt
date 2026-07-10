package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.UiState

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    var stage by remember { mutableStateOf(1) }
    var identifier by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (val s = state) {
            is UiState.Success -> {
                val data = s.data
                if (data is Map<*, *>) {
                    userId = data["userId"] as? String ?: ""
                    if (userId.isNotBlank()) {
                        stage = 2
                        vm.resetState()
                    }
                } else if (data is String) {
                    navController.navigate(Dest.Login.route) { popUpTo(Dest.Login.route) { inclusive = true } }
                    vm.resetState()
                }
            }
            is UiState.Error -> error = s.message
            else -> error = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Forgot Password", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            when (stage) {
                1 -> {
                    Text("Enter your registered email or phone. We'll send a 6-digit OTP.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    GoTourTextField(identifier, { identifier = it }, "Email or Phone", leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) })
                }
                2 -> {
                    Text("Enter the OTP sent to your contact and a new password.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    GoTourTextField(code, { code = it }, "6-digit OTP", leadingIcon = { Icon(Icons.Default.Password, contentDescription = null) }, keyboardType = KeyboardType.NumberPassword)
                    Spacer(Modifier.height(12.dp))
                    GoTourTextField(newPass, { newPass = it }, "New Password", isPassword = true, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, keyboardType = KeyboardType.Password)
                }
            }
            Spacer(Modifier.height(20.dp))
            error?.let {
                Text(it, color = Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton(if (stage == 1) "Send OTP" else "Reset Password", isLoading = state is UiState.Loading) {
                if (stage == 1 && identifier.isNotBlank()) {
                    vm.forgotPassword(identifier)
                } else if (stage == 2 && code.length == 6 && newPass.length >= 6) {
                    vm.resetPassword(userId, code, newPass)
                }
            }
        }
    }
}
