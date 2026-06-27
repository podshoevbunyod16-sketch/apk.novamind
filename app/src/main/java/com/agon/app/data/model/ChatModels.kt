package com.agon.app.data.model

import kotlinx.serialization.Serializable

enum class MessageRole { USER, AI }

@Serializable
data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

@Serializable
data class UserProfile(
    val nick: String,
    val code: String = "",
    val isAdmin: Boolean = false,
    val avatarUrl: String = "",
    val googleLogin: Boolean = false,
    val email: String = "",
)

data class NavDrawerItem(
    val id: String,
    val title: String,
    val icon: String, // emoji or single char
    val badge: String? = null,
    val mode: InputMode? = null,
)

data class InputMode(
    val prefix: String,
    val placeholder: String,
)

data class ModelOption(
    val id: String,
    val name: String,
    val desc: String,
    val color: androidx.compose.ui.graphics.Color,
)

@Serializable
data class SendRequest(val message: String, val reasoning: Boolean = false)

@Serializable
data class SendResponse(val reply: String? = null, val error: String? = null)

@Serializable
data class CommandRequest(val command: String)

@Serializable
data class CommandResponse(
    val result: String? = null,
    val reply: String? = null,
    val error: String? = null,
)

@Serializable
data class WebSearchRequest(val query: String)

@Serializable
data class WebSearchResponse(val reply: String? = null, val error: String? = null)

@Serializable
data class AutoSearchRequest(val message: String)

@Serializable
data class AutoSearchResponse(
    val needs_search: Boolean = false,
    val search_query: String? = null,
    val reply: String? = null,
    val error: String? = null,
)

@Serializable
data class ModelsListResponse(
    val models: List<ProviderModels> = emptyList(),
    val current: String = "",
)

@Serializable
data class ProviderModels(
    val provider: String,
    val list: List<ModelInfo> = emptyList(),
)

@Serializable
data class ModelInfo(val id: String, val name: String)

@Serializable
data class SwitchResponse(val success: Boolean = false, val provider: String? = null, val error: String? = null)

@Serializable
data class AdminLoginRequest(val username: String, val code: String)

@Serializable
data class AdminLoginResponse(val success: Boolean = false, val username: String? = null, val error: String? = null)

@Serializable
data class AdminStatsResponse(
    val current_provider: String = "",
    val current_model: String = "",
    val history_messages: Int = 0,
    val plugins_loaded: List<String> = emptyList(),
    val custom_commands: List<String> = emptyList(),
    val system_prompt: String = "",
    val voice_enabled: Boolean = false,
)

@Serializable
data class AdminSettingsRequest(
    val system_prompt: String? = null,
    val provider: String? = null,
    val model: String? = null,
)
