package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val dest by vm.startDestination.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(800)
        navController.navigate(dest) {
            popUpTo(Dest.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Maroon, MaroonDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Gold),
                contentAlignment = Alignment.Center
            ) {
                Text("GT", color = Maroon, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text("Go Tour N Travels", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Text("Mount Abu • Rajasthan", color = GoldLight, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}
