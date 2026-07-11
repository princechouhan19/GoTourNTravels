package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.datastore.UserPrefs
import com.gotourntravels.models.User
import com.gotourntravels.repository.GoTourRepository
import com.gotourntravels.network.dto.UpdateProfileRequest
import com.gotourntravels.network.dto.ChangePasswordRequest
import com.gotourntravels.models.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: GoTourRepository,
    private val prefs: UserPrefs
) : ViewModel() {

    val user: StateFlow<User?> = prefs.user.let { f ->
        MutableStateFlow<User?>(null).also { mut ->
            viewModelScope.launch { f.collect { mut.value = it } }
        }
    }.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success.asStateFlow()

    fun refresh() = viewModelScope.launch { try { repo.getProfile() } catch (_: Exception) {} }

    fun updateProfile(name: String, email: String, phone: String, avatar: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null; _success.value = null
            try {
                repo.updateProfile(UpdateProfileRequest(name = name, email = email, phone = phone, avatar = avatar))
                _success.value = "Profile updated"
            } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun changePassword(current: String, newPass: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null; _success.value = null
            try {
                repo.changePassword(current, newPass)
                _success.value = "Password updated"
            } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun updateAddresses(addresses: List<Address>) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null; _success.value = null
            try {
                repo.updateProfile(UpdateProfileRequest(addresses = addresses))
                _success.value = "Addresses updated"
            } catch (e: Exception) { _error.value = e.message } finally { _loading.value = false }
        }
    }

    fun logout() = viewModelScope.launch { repo.logout() }
    fun setDarkMode(value: Boolean) = viewModelScope.launch { prefs.setDarkMode(value) }
}
