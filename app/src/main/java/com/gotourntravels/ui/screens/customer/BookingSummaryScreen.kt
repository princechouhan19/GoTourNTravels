package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.BookingViewModel
import com.gotourntravels.viewmodel.PaymentsViewModel

@Composable
fun BookingSummaryScreen(navController: NavController, bookingId: String) {
    val bookingVm: BookingViewModel = hiltViewModel()
    val paymentsVm: PaymentsViewModel = hiltViewModel()
    val booking by bookingVm.detail.collectAsStateWithLifecycle()
    val loading by bookingVm.loading.collectAsStateWithLifecycle()
    val order by paymentsVm.order.collectAsStateWithLifecycle()
    val payment by paymentsVm.payment.collectAsStateWithLifecycle()
    val payLoading by paymentsVm.loading.collectAsStateWithLifecycle()
    val payError by paymentsVm.error.collectAsStateWithLifecycle()

    LaunchedEffect(bookingId) { bookingVm.loadDetail(bookingId) }

    val b = booking
    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Booking Summary", onBack = { navController.popBackStack() })
        if (loading && b == null) { LoadingBlock(); return@Column }
        if (b == null) return@Column
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            // Status card
            Surface(
                color = when (b.status) {
                    "confirmed", "active", "completed" -> Green.copy(alpha = 0.1f)
                    "cancelled", "expired" -> Red.copy(alpha = 0.1f)
                    else -> Amber.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            when (b.status) {
                                "confirmed", "active", "completed" -> Icons.Default.CheckCircle
                                "cancelled" -> Icons.Default.Cancel
                                else -> Icons.Default.Pending
                            },
                            contentDescription = null,
                            tint = when (b.status) {
                                "confirmed", "active", "completed" -> Green
                                "cancelled", "expired" -> Red
                                else -> Amber
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Booking ${b.status.replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        StatusChip(b.paymentStatus)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Booking #${b.bookingNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Vehicle info
            b.vehicle?.let { v ->
                Surface(color = CreamDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.name, fontWeight = FontWeight.Bold)
                            Text("${v.brand} ${v.model}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Reg: ${v.registrationNumber}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("₹${b.pricing.total}", color = Maroon, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Pricing breakdown
            SectionHeader("Price Breakdown")
            DetailRow("Base amount", "₹${b.pricing.baseAmount.toInt()}")
            DetailRow("Security deposit", "₹${b.pricing.securityDeposit.toInt()}")
            DetailRow("GST (${b.pricing.gstRate.toInt()}%)", "₹${b.pricing.gstAmount.toInt()}")
            if (b.pricing.discount > 0) DetailRow("Discount", "- ₹${b.pricing.discount.toInt()}")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row {
                Text("Total", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("₹${b.pricing.total.toInt()}", fontWeight = FontWeight.Bold, color = Maroon)
            }
            Spacer(Modifier.height(16.dp))

            // Trip details
            SectionHeader("Trip Details")
            DetailRow("Rental type", b.rentalType.replaceFirstChar { it.uppercase() })
            DetailRow("Duration", "${b.durationHours} hours")
            DetailRow("Start", b.startDate.take(16).replace("T", " "))
            DetailRow("End", b.endDate.take(16).replace("T", " "))
            DetailRow("Pickup", b.pickupLocation?.address ?: "Go Tour N Travels Office")
            if (b.withDriver) DetailRow("Driver", "Included")
            Spacer(Modifier.height(16.dp))

            // Payment section
            if (b.paymentStatus != "paid" && b.status != "cancelled") {
                SectionHeader("Complete Payment")
                payError?.let { Text(it, color = Red, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(8.dp)) }
                PrimaryButton(
                    text = if (order != null) "Pay ₹${b.pricing.total.toInt()} via Razorpay" else "Pay ₹${b.pricing.total.toInt()} Now",
                    isLoading = payLoading
                ) {
                    paymentsVm.createOrder(b.id, "booking")
                }
                Spacer(Modifier.height(8.dp))
                SecondaryButton("Cancel Booking", enabled = b.status in listOf("pending", "confirmed")) {
                    bookingVm.cancel(b.id, "Cancelled by customer") {
                        bookingVm.loadDetail(b.id)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("DEV MODE: Backend will accept any signature in mock mode. Real Razorpay test key required for live test payments.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (b.paymentStatus == "paid" && b.status == "active") {
                PrimaryButton("View Active Rental") {
                    navController.navigate(Dest.activeBooking(b.id))
                }
                Spacer(Modifier.height(12.dp))
            } else if (b.paymentStatus == "paid" && b.status == "completed") {
                SectionHeader("Rate your experience")
                ReviewForm { rating, comment ->
                    bookingVm.submitReview(b.id, rating, comment) {
                        bookingVm.loadDetail(b.id)
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ReviewForm(onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    Column {
        Row {
            (1..5).forEach { i ->
                IconButton(onClick = { rating = i }) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = if (i <= rating) Gold else InkMuted.copy(alpha = 0.3f))
                }
            }
        }
        OutlinedTextField(value = comment, onValueChange = { comment = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp), label = { Text("Share your experience") })
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Submit Review") { onSubmit(rating, comment) }
    }
}
