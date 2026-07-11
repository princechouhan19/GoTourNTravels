package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.datastore.UserPrefs
import com.gotourntravels.models.User
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: GoTourRepository,
    private val prefs: UserPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Any>>(UiState.Idle)
    val state: StateFlow<UiState<Any>> = _state

    val user: StateFlow<User?> = prefs.user.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val darkMode: StateFlow<Boolean> = prefs.darkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isLoggedIn: StateFlow<Boolean> = prefs.token.map { !it.isNullOrBlank() }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val startDestination: StateFlow<String> = combine(prefs.onboardingDone, prefs.user) { onboarded, u ->
        when {
            !onboarded -> "onboarding"
            u == null -> "login"
            u.role == "admin" -> "admin-dashboard"
            else -> "customer-home"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "splash")

    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val r = repo.register(name, email, phone, password)
                _state.value = UiState.Success(r)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val r = repo.login(identifier, password)
                _state.value = UiState.Success(r)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun adminLogin(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val r = repo.adminLogin(email, password)
                _state.value = UiState.Success(r)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Admin login failed")
            }
        }
    }

    fun verifyOtp(code: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val r = repo.verifyOtp(code)
                _state.value = UiState.Success(r)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "OTP verification failed")
            }
        }
    }

    fun resendOtp() = viewModelScope.launch {
        try { repo.resendOtp() } catch (_: Exception) {}
    }

    fun forgotPassword(identifier: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val r = repo.forgotPassword(identifier)
                _state.value = UiState.Success(mapOf("userId" to (r["userId"] ?: ""), "otpDev" to (r["otpDev"] ?: "")))
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Failed to send OTP")
            }
        }
    }

    fun resetPassword(userId: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                repo.resetPassword(userId, code, newPassword)
                _state.value = UiState.Success("Password reset. You can now log in.")
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Reset failed")
            }
        }
    }

    fun bypassLogin(admin: Boolean) {
        viewModelScope.launch {
            val role = if (admin) "admin" else "customer"
            val dummyUser = User(
                id = "dummy_user_id",
                name = "Demo Guest",
                email = if (admin) "admin@gotourntravels.com" else "guest@gotourntravels.com",
                phone = "+919000000000",
                role = role,
                isVerified = true
            )
            prefs.saveAuth("dummy_token", dummyUser)
            _state.value = UiState.Success(dummyUser)
        }
    }

    fun logout() = viewModelScope.launch { repo.logout() }

    fun toggleDarkMode(value: Boolean) = viewModelScope.launch { prefs.setDarkMode(value) }

    fun completeOnboarding() = viewModelScope.launch { prefs.setOnboardingDone(true) }

    fun resetState() { _state.value = UiState.Idle }
}
