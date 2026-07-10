package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Advertisement
import com.gotourntravels.models.BusinessSettings
import com.gotourntravels.models.Vehicle
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _featured = MutableStateFlow<List<Vehicle>>(emptyList())
    val featured: StateFlow<List<Vehicle>> = _featured.asStateFlow()

    private val _allVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val allVehicles: StateFlow<List<Vehicle>> = _allVehicles.asStateFlow()

    private val _ads = MutableStateFlow<List<Advertisement>>(emptyList())
    val ads: StateFlow<List<Advertisement>> = _ads.asStateFlow()

    private val _business = MutableStateFlow<BusinessSettings?>(null)
    val business: StateFlow<BusinessSettings?> = _business.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _featured.value = repo.featuredVehicles()
                _allVehicles.value = repo.listVehicles().items
                _ads.value = repo.listAds("home_banner")
                _business.value = repo.getBusiness()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load home"
            } finally {
                _loading.value = false
            }
        }
    }
}
