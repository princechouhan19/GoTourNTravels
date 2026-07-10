package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Notification
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Notification>>(emptyList())
    val items: StateFlow<List<Notification>> = _items.asStateFlow()
    private val _unread = MutableStateFlow(0)
    val unread: StateFlow<Int> = _unread.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun load(unread: Boolean? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = repo.listNotifications(unread)
                _items.value = r.items
                _unread.value = r.unreadCount
            } catch (_: Exception) {} finally { _loading.value = false }
        }
    }

    fun markAllRead() = viewModelScope.launch {
        try { repo.markAllRead(); load() } catch (_: Exception) {}
    }

    fun markRead(id: String) = viewModelScope.launch {
        try { repo.markRead(id); load() } catch (_: Exception) {}
    }

    fun delete(id: String) = viewModelScope.launch {
        try { repo.deleteNotification(id); load() } catch (_: Exception) {}
    }
}
