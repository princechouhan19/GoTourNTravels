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
                _error.value = e.message ?: "Search failed"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadVehicle(id: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = repo.vehicleDetails(id)
                _vehicle.value = r.vehicle
                _reviews.value = r.reviews
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load vehicle"
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
