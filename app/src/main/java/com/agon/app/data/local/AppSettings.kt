package com.agon.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nova_settings")

class AppSettings(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val USER_PROFILE = stringPreferencesKey("user_profile")
        val CHAT_HISTORY = stringPreferencesKey("chat_history")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { it[SERVER_URL] ?: "" }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[SERVER_URL] = url }
    }

    val userProfile: Flow<UserProfile?> = context.dataStore.data.map { prefs ->
        prefs[USER_PROFILE]?.let { json.decodeFromString(it) }
    }

    suspend fun saveUserProfile(profile: UserProfile?) {
        context.dataStore.edit { prefs ->
            if (profile == null) prefs.remove(USER_PROFILE)
            else prefs[USER_PROFILE] = json.encodeToString(profile)
        }
    }

    val chatHistory: Flow<List<ChatMessage>> = context.dataStore.data.map { prefs ->
        prefs[CHAT_HISTORY]?.let {
            try {
                json.decodeFromString<List<ChatMessage>>(it)
            } catch (_: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    suspend fun saveChatHistory(history: List<ChatMessage>) {
        context.dataStore.edit { prefs ->
            prefs[CHAT_HISTORY] = json.encodeToString(history)
        }
    }

    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[DARK_THEME] ?: true }

    suspend fun setDarkTheme(value: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = value }
    }
}
