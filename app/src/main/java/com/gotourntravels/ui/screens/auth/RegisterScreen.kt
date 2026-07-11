package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.GoTourTextField
import com.gotourntravels.ui.components.GoTourTopBar
import com.gotourntravels.ui.components.PrimaryButton
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.Red
import com.gotourntravels.viewmodel.AuthViewModel
import com.gotourntravels.viewmodel.UiState

@Composable
fun RegisterScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (val s = state) {
            is UiState.Success -> {
                navController.navigate(Dest.CustomerHome.route) { popUpTo(Dest.Login.route) { inclusive = true } }
                vm.resetState()
            }
            is UiState.Error -> error = s.message
            else -> error = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Create Account", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            GoTourTextField(name, { name = it }, "Full Name", leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) })
            Spacer(Modifier.height(12.dp))
            GoTourTextField(email, { email = it }, "Email", leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(phone, { phone = it }, "Phone Number", leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }, keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(password, { password = it }, "Password", isPassword = true, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, keyboardType = KeyboardType.Password)
            Spacer(Modifier.height(20.dp))
            error?.let {
                Text(it, color = Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("Register", isLoading = state is UiState.Loading) {
                if (name.length >= 2 && email.contains("@") && phone.length >= 7 && password.length >= 6) {
                    vm.register(name, email, phone, password)
                } else {
                    error = "Please fill all fields correctly (password min 6 chars)"
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("By registering you agree to Go Tour N Travels' terms of service and privacy policy.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
