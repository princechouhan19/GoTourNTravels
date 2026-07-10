package com.gotourntravels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotourntravels.ui.navigation.GoTourNavHost
import com.gotourntravels.ui.theme.GoTourNTravelsTheme
import com.gotourntravels.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color(0xFF8B1E3F).toArgb(), Color(0xFF6B1530).toArgb()),
            navigationBarStyle = SystemBarStyle.auto(Color(0xFF8B1E3F).toArgb(), Color(0xFF6B1530).toArgb())
        )

        setContent {
            val authVm: AuthViewModel = hiltViewModel()
            val darkMode by authVm.darkMode.collectAsStateWithLifecycle(initialValue = false)
            val startDestination by authVm.startDestination.collectAsStateWithLifecycle()

            GoTourNTravelsTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize().background(Color.Transparent)
                ) {
                    GoTourNavHost(startDestination = startDestination)
                }
            }
        }
    }
}
