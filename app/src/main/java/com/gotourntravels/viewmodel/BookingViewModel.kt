package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Booking
import com.gotourntravels.repository.GoTourRepository
import com.gotourntravels.network.dto.CreateBookingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Booking>>(emptyList())
    val items: StateFlow<List<Booking>> = _items.asStateFlow()

    private val _active = MutableStateFlow<List<Booking>>(emptyList())
    val active: StateFlow<List<Booking>> = _active.asStateFlow()

    private val _detail = MutableStateFlow<Booking?>(null)
    val detail: StateFlow<Booking?> = _detail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _created = MutableStateFlow<Booking?>(null)
    val created: StateFlow<Booking?> = _created.asStateFlow()

    fun load(status: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try { _items.value = repo.listBookings(status).items } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun loadActive() {
        viewModelScope.launch {
            try { _active.value = repo.activeBookings() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun loadDetail(id: String) {
        viewModelScope.launch {
            _loading.value = true
            try { _detail.value = repo.bookingDetails(id) } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun create(
        vehicleId: String,
        rentalType: String,
        startDateIso: String,
        endDateIso: String,
        withDriver: Boolean,
        notes: String,
        pickupLat: Double? = null,
        pickupLng: Double? = null,
        pickupAddress: String = ""
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val body = CreateBookingRequest(
                    vehicle = vehicleId,
                    rentalType = rentalType,
                    startDate = startDateIso,
                    endDate = endDateIso,
                    withDriver = withDriver,
                    customerNotes = notes,
                    pickupLocation = if (pickupLat != null && pickupLng != null)
                        com.gotourntravels.models.Location(address = pickupAddress, lat = pickupLat, lng = pickupLng) else null
                )
                _created.value = repo.createBooking(body)
            } catch (e: Exception) {
                _error.value = e.message ?: "Booking failed"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancel(id: String, reason: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try { repo.cancelBooking(id, reason); onDone() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun submitReview(id: String, rating: Int, comment: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try { repo.submitReview(id, rating, comment); onDone() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun updateLocation(id: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            try { repo.updateBookingLocation(id, lat, lng) } catch (_: Exception) {}
        }
    }

    // Admin actions
    fun activate(id: String) {
        viewModelScope.launch { try { repo.activateBooking(id); loadDetail(id) } catch (e: Exception) { _error.value = e.message } }
    }

    fun complete(id: String, endOdo: Int, endFuel: Int) {
        viewModelScope.launch { try { repo.completeBooking(id, endOdo, endFuel); loadDetail(id) } catch (e: Exception) { _error.value = e.message } }
    }

    fun resetCreated() { _created.value = null }
}
