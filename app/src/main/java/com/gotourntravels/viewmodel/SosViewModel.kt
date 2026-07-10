package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.datastore.UserPrefs
import com.gotourntravels.models.Location
import com.gotourntravels.models.SOSRequest
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SosViewModel @Inject constructor(
    private val repo: GoTourRepository,
    private val prefs: UserPrefs
) : ViewModel() {

    private val _items = MutableStateFlow<List<SOSRequest>>(emptyList())
    val items: StateFlow<List<SOSRequest>> = _items.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _created = MutableStateFlow<SOSRequest?>(null)
    val created: StateFlow<SOSRequest?> = _created.asStateFlow()

    fun loadMine() {
        viewModelScope.launch {
            try { _items.value = repo.mySos() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            try { _items.value = repo.listAllSos() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun raise(type: String, description: String, lat: Double, lng: Double, address: String, bookingId: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val phone = prefs.user.first()?.phone ?: ""
                _created.value = repo.createSos(Location(address = address, lat = lat, lng = lng), type, description, phone, bookingId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to raise SOS"
            } finally {
                _loading.value = false
            }
        }
    }

    fun acknowledge(id: String) = viewModelScope.launch {
        try { repo.acknowledgeSos(id); loadAll() } catch (e: Exception) { _error.value = e.message }
    }

    fun resolve(id: String, notes: String) = viewModelScope.launch {
        try { repo.resolveSos(id, notes); loadAll() } catch (e: Exception) { _error.value = e.message }
    }
}
