package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.models.BusinessSettings
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.AdminViewModel

@Composable
fun BusinessProfileScreen(navController: NavController) {
    val vm: AdminViewModel = hiltViewModel()
    val business by vm.business.collectAsStateWithLifecycle()
    var form by remember { mutableStateOf<BusinessSettings?>(null) }
    LaunchedEffect(Unit) { vm.loadBusiness() }
    LaunchedEffect(business) { form = business }

    val b = form
    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Business Profile", onBack = { navController.popBackStack() })
        if (b == null) { LoadingBlock(); return@Column }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            GoTourTextField(b.name, { form = b.copy(name = it) }, "Business Name")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.tagline, { form = b.copy(tagline = it) }, "Tagline")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.phone, { form = b.copy(phone = it) }, "Phone", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.email, { form = b.copy(email = it) }, "Email", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.address.line1, { form = b.copy(address = b.address.copy(line1 = it)) }, "Address Line 1")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.address.city, { form = b.copy(address = b.address.copy(city = it)) }, "City")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.address.state, { form = b.copy(address = b.address.copy(state = it)) }, "State")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.address.pincode, { form = b.copy(address = b.address.copy(pincode = it)) }, "Pincode")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.gstNumber, { form = b.copy(gstNumber = it) }, "GST Number")
            Spacer(Modifier.height(12.dp))
            GoTourTextField(b.cancellationPolicy, { form = b.copy(cancellationPolicy = it) }, "Cancellation Policy")
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Save Changes") { vm.saveBusiness(b) }
            Spacer(Modifier.height(20.dp))
        }
    }
}
