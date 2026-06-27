package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.local.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = AppSettings(application)

    val serverUrl = settings.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val darkTheme = settings.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun saveServerUrl(url: String) {
        viewModelScope.launch { settings.setServerUrl(url) }
    }

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch { settings.setDarkTheme(value) }
    }
}
