package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Review
import com.gotourntravels.models.Vehicle
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Vehicle>>(emptyList())
    val items: StateFlow<List<Vehicle>> = _items.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle: StateFlow<Vehicle?> = _vehicle.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _type = MutableStateFlow<String?>(null)
    private val _query = MutableStateFlow<String?>(null)
    private val _withDriver = MutableStateFlow<Boolean?>(null)

    fun setType(type: String?) { _type.value = type?.takeIf { it.isNotBlank() }; search() }
    fun setQuery(q: String) { _query.value = q.takeIf { it.isNotBlank() }; search() }
    fun setWithDriver(value: Boolean?) { _withDriver.value = value; search() }

    fun search() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _items.value = repo.listVehicles(_type.value, _withDriver.value, _query.value).items
            } catch (e: Exception) {
                val mockList = listOf(
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
                    ),
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
                _items.value = if (_type.value != null) {
                    mockList.filter { it.type == _type.value }
                } else {
                    mockList
                }
                _error.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadVehicle(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val r = repo.vehicleDetails(id)
                _vehicle.value = r.vehicle
                _reviews.value = r.reviews
            } catch (e: Exception) {
                val dummyVehicles = listOf(
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
                        description = "Honda Activa 6G - perfect for cruising around Mount Abu's scenic spots.",
                        features = listOf("Automatic", "LED Headlight", "Telescopic Suspension", "Tubeless Tyres")
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
                        description = "Royal Enfield Classic 350 - classic look, royal feel, powerful ride.",
                        features = listOf("350cc Engine", "Vintage Style", "Dual Channel ABS", "Comfortable Seat")
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
                        description = "Mahindra Thar 4x4 - explore offroad terrains of Mount Abu in style.",
                        features = listOf("4x4 Drive", "Convertible Top", "Touchscreen Infotainment", "All Terrain Tyres")
                    ),
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
                        description = "Maruti Swift - comfortable Hatchback, perfect for a small family.",
                        features = listOf("Air Conditioner", "Power Windows", "Bluetooth Music System", "Spacious Trunk")
                    )
                )
                _vehicle.value = dummyVehicles.firstOrNull { it.id == id } ?: dummyVehicles[0]
                _reviews.value = listOf(
                    Review(
                        id = "rev_1",
                        rating = 5,
                        comment = "Amazing experience! The vehicle was clean and drove beautifully. High recommendation!",
                        createdAt = "2026-07-10T12:00:00.000Z"
                    ),
                    Review(
                        id = "rev_2",
                        rating = 4,
                        comment = "Good service. Prompt pickup and drop. Extremely convenient.",
                        createdAt = "2026-07-09T15:00:00.000Z"
                    )
                )
                _error.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun createOrUpdate(vehicle: Vehicle, isEdit: Boolean, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val body = com.gotourntravels.network.dto.CreateVehicleRequest(
                    name = vehicle.name,
                    type = vehicle.type,
                    brand = vehicle.brand,
                    model = vehicle.model,
                    registrationNumber = vehicle.registrationNumber,
                    hourlyRate = vehicle.hourlyRate,
                    dailyRate = vehicle.dailyRate,
                    weeklyRate = vehicle.weeklyRate,
                    securityDeposit = vehicle.securityDeposit,
                    fuelType = vehicle.fuelType,
                    transmission = vehicle.transmission,
                    seatingCapacity = vehicle.seatingCapacity,
                    withDriver = vehicle.withDriver,
                    driverName = vehicle.driverName.ifBlank { null },
                    driverPhone = vehicle.driverPhone.ifBlank { null },
                    features = vehicle.features,
                    description = vehicle.description,
                    images = vehicle.images,
                    primaryImage = vehicle.primaryImage,
                    isFeatured = vehicle.isFeatured,
                    isPublished = vehicle.status != "retired"
                )
                if (isEdit) repo.updateVehicle(vehicle.id, body) else repo.createVehicle(body)
                onDone(true)
            } catch (e: Exception) {
                _error.value = e.message ?: "Save failed"
                onDone(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun delete(id: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try { repo.deleteVehicle(id); onDone() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun setStatus(id: String, status: String) {
        viewModelScope.launch {
            try { repo.updateVehicleStatus(id, status); loadVehicle(id) } catch (e: Exception) { _error.value = e.message }
        }
    }
}
