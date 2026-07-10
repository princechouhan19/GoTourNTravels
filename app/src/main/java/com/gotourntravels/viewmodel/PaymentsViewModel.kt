package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Payment
import com.gotourntravels.network.dto.RazorpayOrder
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Payment>>(emptyList())
    val items: StateFlow<List<Payment>> = _items.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _order = MutableStateFlow<RazorpayOrder?>(null)
    val order: StateFlow<RazorpayOrder?> = _order.asStateFlow()

    private val _payment = MutableStateFlow<Payment?>(null)
    val payment: StateFlow<Payment?> = _payment.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try { _items.value = repo.listPayments().items } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun createOrder(bookingId: String, type: String = "booking") {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val r = repo.createOrder(bookingId, type)
                _order.value = r.order
                _payment.value = r.payment
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun verify(bookingId: String, orderId: String, paymentId: String, signature: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = repo.verifyPayment(bookingId, orderId, paymentId, signature)
                _payment.value = r.payment
                onDone(true)
            } catch (e: Exception) {
                _error.value = e.message
                onDone(false)
            } finally {
                _loading.value = false
            }
        }
    }
}
