package com.gotourntravels.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gotourntravels.ui.components.PrimaryButton
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

private data class OnboardPage(val title: String, val body: String, val icon: ImageVector, val color: Color)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val vm: AuthViewModel = hiltViewModel()
    val pages = listOf(
        OnboardPage("Rent Two-Wheelers & Cars", "Activa, Jupiter, Royal Enfield, sedans & SUVs — pick your ride, Mount Abu awaits.", Icons.Default.TwoWheeler, Maroon),
        OnboardPage("Book with Driver", "Hire trained local drivers for sightseeing, airport transfers, and tour packages.", Icons.Default.DirectionsCar, Gold),
        OnboardPage("Safety First", "SOS support, live rental tracking, and nearby hospitals & police stations at your fingertips.", Icons.Default.HealthAndSafety, Green)
    )
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                vm.completeOnboarding()
                navController.navigate(Dest.Login.route) { popUpTo(0) }
            }) { Text("Skip", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
        }

        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { i ->
            val p = pages[i]
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(160.dp).clip(CircleShape).background(Brush.verticalGradient(listOf(p.color, p.color.copy(alpha = 0.7f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(p.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp))
                }
                Spacer(Modifier.height(32.dp))
                Text(p.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Maroon)
                Spacer(Modifier.height(12.dp))
                Text(p.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val active = pager.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (active) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (active) Gold else InkMuted.copy(alpha = 0.4f))
                )
            }
        }

        PrimaryButton(
            text = if (pager.currentPage == pages.size - 1) "Get Started" else "Next",
            modifier = Modifier.padding(16.dp)
        ) {
            if (pager.currentPage == pages.size - 1) {
                vm.completeOnboarding()
                navController.navigate(Dest.Login.route) { popUpTo(0) }
            } else {
                scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
