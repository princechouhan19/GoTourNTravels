package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.GoTourTopBar
import com.gotourntravels.ui.components.PrimaryButton
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.UiState
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val user by vm.user.collectAsStateWithLifecycle()

    val code = remember { List(6) { mutableStateOf("") } }
    val error = remember { mutableStateOf<String?>(null) }
    val resend = remember { mutableStateOf(30) }

    LaunchedEffect(Unit) {
        while (resend.value > 0) {
            delay(1000); resend.value -= 1
        }
    }

    LaunchedEffect(state) {
        when (val s = state) {
            is UiState.Success -> {
                val u = user
                val dest = if (u?.role == "admin") Dest.AdminDashboard.route else Dest.CustomerHome.route
                navController.navigate(dest) { popUpTo(0) }
                vm.resetState()
            }
            is UiState.Error -> error.value = s.message
            else -> error.value = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Verify OTP", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))
            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(72.dp), tint = Gold)
            Spacer(Modifier.height(16.dp))
            Text("Enter the 6-digit code", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Sent to ${user?.phone.orEmpty()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                code.forEachIndexed { i, s ->
                    OutlinedTextField(
                        value = s.value,
                        onValueChange = { v ->
                            if (v.length <= 1 && v.all { it.isDigit() }) {
                                s.value = v
                                if (v.isNotBlank() && i < 5) { /* focus next */ }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Maroon)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            error.value?.let {
                Text(it, color = Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
            }
            PrimaryButton("Verify & Continue", isLoading = state is UiState.Loading) {
                val otp = code.joinToString("") { it.value }
                if (otp.length == 6) vm.verifyOtp(otp) else error.value = "Enter all 6 digits"
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = {
                if (resend.value == 0) {
                    vm.resendOtp()
                    resend.value = 30
                }
            }) {
                Text(if (resend.value > 0) "Resend OTP in ${resend.value}s" else "Resend OTP", color = if (resend.value > 0) InkMuted else Maroon)
            }
            Spacer(Modifier.height(8.dp))
            // Dev hint
            Text("DEV: Check backend console for OTP", fontSize = 10.sp, color = InkMuted)
        }
    }
}
