package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.ChatRepository
import com.agon.app.data.local.AppSettings
import com.agon.app.data.model.AdminSettingsRequest
import com.agon.app.data.model.AdminStatsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = AppSettings(application)
    private val repository = ChatRepository(application)

    val serverUrl = settings.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _stats = MutableStateFlow<AdminStatsResponse?>(null)
    val stats: StateFlow<AdminStatsResponse?> = _stats

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadStats() {
        viewModelScope.launch {
            _loading.value = true
            _stats.value = repository.adminStats(serverUrl.value)
            _loading.value = false
        }
    }

    fun saveSettings(settingsRequest: AdminSettingsRequest) {
        viewModelScope.launch {
            repository.adminSaveSettings(settingsRequest, serverUrl.value)
            loadStats()
        }
    }
}
