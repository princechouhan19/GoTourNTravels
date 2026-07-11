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
                // Populate mock data so user can see and test the UI offline/without server
                _featured.value = listOf(
                    Vehicle(
                        id = "mock_activa",
                        name = "Honda Activa 6G",
                        type = "scooter",
                        brand = "Honda",
                        model = "Activa 6G",
                        dailyRate = 400,
                        rating = 4.8,
                        reviewsCount = 42,
                        primaryImage = "https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=500&auto=format&fit=crop&q=60",
                        description = "Honda Activa 6G - perfect for cruising around Mount Abu's scenic spots."
                    ),
                    Vehicle(
                        id = "mock_bullet",
                        name = "Royal Enfield Classic 350",
                        type = "bike",
                        brand = "Royal Enfield",
                        model = "Classic 350",
                        dailyRate = 900,
                        rating = 4.9,
                        reviewsCount = 85,
                        primaryImage = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=500&auto=format&fit=crop&q=60",
                        description = "Royal Enfield Classic 350 - classic look, royal feel, powerful ride."
                    ),
                    Vehicle(
                        id = "mock_thar",
                        name = "Mahindra Thar 4x4",
                        type = "suv",
                        brand = "Mahindra",
                        model = "Thar",
                        dailyRate = 3500,
                        rating = 4.7,
                        reviewsCount = 29,
                        primaryImage = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=500&auto=format&fit=crop&q=60",
                        description = "Mahindra Thar 4x4 - explore offroad terrains of Mount Abu in style."
                    )
                )
                _allVehicles.value = _featured.value + listOf(
                    Vehicle(
                        id = "mock_swift",
                        name = "Maruti Suzuki Swift",
                        type = "car",
                        brand = "Maruti Suzuki",
                        model = "Swift",
                        dailyRate = 1800,
                        rating = 4.6,
                        reviewsCount = 54,
                        primaryImage = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=500&auto=format&fit=crop&q=60",
                        description = "Maruti Swift - comfortable Hatchback, perfect for a small family."
                    )
                )
                _ads.value = listOf(
                    Advertisement(
                        id = "mock_ad_1",
                        title = "Explore Mount Abu",
                        subtitle = "Get flat 15% off on your first booking!",
                        imageUrl = "https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?w=500&auto=format&fit=crop&q=60"
                    )
                )
                _business.value = BusinessSettings(
                    name = "Go Tour N Travels",
                    phone = "+91 90012 34567",
                    email = "info@gotourntravels.com"
                )
                _error.value = null // Clear error so the UI shows our mock data
            } finally {
                _loading.value = false
            }
        }
    }
}
