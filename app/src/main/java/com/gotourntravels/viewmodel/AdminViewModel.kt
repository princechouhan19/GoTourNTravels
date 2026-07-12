package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Advertisement
import com.gotourntravels.models.BusinessSettings
import com.gotourntravels.models.DashboardStats
import com.gotourntravels.models.Review
import com.gotourntravels.models.User
import com.gotourntravels.models.Vehicle
import com.gotourntravels.network.dto.BookingDayStat
import com.gotourntravels.network.dto.RevenueByType
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _stats = MutableStateFlow<DashboardStats?>(null)
    val stats: StateFlow<DashboardStats?> = _stats.asStateFlow()

    private val _bookingsChart = MutableStateFlow<List<BookingDayStat>>(emptyList())
    val bookingsChart: StateFlow<List<BookingDayStat>> = _bookingsChart.asStateFlow()

    private val _revenueByType = MutableStateFlow<List<RevenueByType>>(emptyList())
    val revenueByType: StateFlow<List<RevenueByType>> = _revenueByType.asStateFlow()

    private val _topVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val topVehicles: StateFlow<List<Vehicle>> = _topVehicles.asStateFlow()

    private val _customers = MutableStateFlow<List<User>>(emptyList())
    val customers: StateFlow<List<User>> = _customers.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _ads = MutableStateFlow<List<Advertisement>>(emptyList())
    val ads: StateFlow<List<Advertisement>> = _ads.asStateFlow()

    private val _business = MutableStateFlow<BusinessSettings?>(null)
    val business: StateFlow<BusinessSettings?> = _business.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _stats.value = repo.adminDashboard()
                _bookingsChart.value = repo.adminBookingAnalytics(14)
                _revenueByType.value = repo.adminRevenueByType()
                _topVehicles.value = repo.adminTopVehicles()
            } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun loadCustomers(search: String? = null) {
        viewModelScope.launch {
            try { _customers.value = repo.adminListUsers(search).items } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun toggleBlock(id: String, block: Boolean) {
        viewModelScope.launch { try { repo.adminToggleBlock(id, block); loadCustomers() } catch (e: Exception) { _error.value = e.message } }
    }

    fun loadReviews() {
        viewModelScope.launch { try { _reviews.value = repo.listReviews() } catch (e: Exception) { _error.value = e.message } }
    }

    fun updateReview(id: String, approved: Boolean, featured: Boolean, reply: String) {
        viewModelScope.launch {
            try { repo.updateReview(id, approved, featured, reply); loadReviews() } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun loadAds() {
        viewModelScope.launch { try { _ads.value = repo.listAllAds().items } catch (e: Exception) { _error.value = e.message } }
    }

    fun saveAd(ad: Advertisement, isEdit: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                if (isEdit) repo.updateAd(ad.id, ad) else repo.createAd(ad)
                loadAds(); onDone()
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteAd(id: String) {
        viewModelScope.launch { try { repo.deleteAd(id); loadAds() } catch (e: Exception) { _error.value = e.message } }
    }

    fun uploadAdImage(file: java.io.File, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try { onSuccess(repo.uploadImage(file).url) } catch (e: Exception) { _error.value = e.message ?: "Image upload failed" }
            finally { _loading.value = false }
        }
    }

    fun loadBusiness() {
        viewModelScope.launch { try { _business.value = repo.getBusiness() } catch (e: Exception) { _error.value = e.message } }
    }

    fun saveBusiness(b: BusinessSettings) {
        viewModelScope.launch {
            try { _business.value = repo.updateBusiness(b) } catch (e: Exception) { _error.value = e.message }
        }
    }
}
