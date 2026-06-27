package com.agon.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.ChatRepository
import com.agon.app.data.local.AppSettings
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.InputMode
import com.agon.app.data.model.MessageRole
import com.agon.app.data.model.ModelOption
import com.agon.app.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = AppSettings(application)
    private val repository = ChatRepository(application)

    val serverUrl = settings.serverUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userProfile = settings.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    private val _webSearch = MutableStateFlow(false)
    val webSearch: StateFlow<Boolean> = _webSearch

    private val _reasoning = MutableStateFlow(false)
    val reasoning: StateFlow<Boolean> = _reasoning

    private val _autoSearch = MutableStateFlow(false)
    val autoSearch: StateFlow<Boolean> = _autoSearch

    private val _inputMode = MutableStateFlow<InputMode?>(null)
    val inputMode: StateFlow<InputMode?> = _inputMode

    private val _drawerOpen = MutableStateFlow(false)
    val drawerOpen: StateFlow<Boolean> = _drawerOpen

    private val _modelDropdownOpen = MutableStateFlow(false)
    val modelDropdownOpen: StateFlow<Boolean> = _modelDropdownOpen

    private val _selectedModel = MutableStateFlow<ModelOption>(defaultModels[0])
    val selectedModel: StateFlow<ModelOption> = _selectedModel

    private val _showLoginEvent = MutableStateFlow(false)
    val showLoginEvent: StateFlow<Boolean> = _showLoginEvent

    val canSend = combine(input, _isTyping) { text, typing ->
        text.trim().isNotEmpty() && !typing
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            settings.chatHistory.collect { _messages.value = it }
        }
        viewModelScope.launch {
            settings.userProfile.collect { if (it == null) _showLoginEvent.value = true }
        }
    }

    fun onInputChange(text: String) {
        _input.value = text
    }

    fun setInputMode(mode: InputMode?) {
        _inputMode.value = mode
    }

    fun toggleWebSearch() {
        _webSearch.value = !_webSearch.value
    }

    fun toggleReasoning() {
        _reasoning.value = !_reasoning.value
    }

    fun toggleAutoSearch() {
        _autoSearch.value = !_autoSearch.value
    }

    fun toggleDrawer() {
        _drawerOpen.value = !_drawerOpen.value
    }

    fun closeDrawer() {
        _drawerOpen.value = false
    }

    fun toggleModelDropdown() {
        _modelDropdownOpen.value = !_modelDropdownOpen.value
    }

    fun closeModelDropdown() {
        _modelDropdownOpen.value = false
    }

    fun selectModel(model: ModelOption) {
        _selectedModel.value = model
        _modelDropdownOpen.value = false
        viewModelScope.launch {
            repository.switchModel(model.id, serverUrl.value)
        }
    }

    fun send(text: String? = null) {
        val msg = (text ?: _input.value).trim()
        if (msg.isEmpty() || _isTyping.value) return

        val final = if (_inputMode.value != null && !msg.startsWith("/")) {
            _inputMode.value!!.prefix + msg
        } else msg

        _inputMode.value = null
        _input.value = ""

        viewModelScope.launch {
            addMessage(ChatMessage(MessageRole.USER, final))
            _isTyping.value = true
            delay(400)

            val response = repository.sendMessage(
                final,
                reasoning = _reasoning.value,
                webSearch = _webSearch.value,
                autoSearch = _autoSearch.value,
                serverUrl = serverUrl.value,
            )

            if (final.startsWith("/clear", ignoreCase = true)) {
                _messages.value = emptyList()
                persistHistory()
            } else {
                addMessage(response)
            }
            _isTyping.value = false
        }
    }

    fun sendSuggestion(text: String) {
        send(text)
    }

    fun newChat() {
        _messages.value = emptyList()
        persistHistory()
        closeDrawer()
    }

    fun clearChat() {
        newChat()
    }

    fun attachImage(uri: Uri, description: String) {
        viewModelScope.launch {
            addMessage(ChatMessage(MessageRole.USER, "📷 ${uri.lastPathSegment ?: "image"}\n💬 $description"))
            _isTyping.value = true
            val response = repository.uploadImage(uri, description, serverUrl.value)
            addMessage(response)
            _isTyping.value = false
        }
    }

    fun attachDocument(uri: Uri, description: String) {
        viewModelScope.launch {
            addMessage(ChatMessage(MessageRole.USER, "📁 ${uri.lastPathSegment ?: "file"}\n💬 $description"))
            _isTyping.value = true
            val response = repository.uploadDocument(uri, description, serverUrl.value)
            addMessage(response)
            _isTyping.value = false
        }
    }

    fun login(profile: UserProfile) {
        viewModelScope.launch {
            settings.saveUserProfile(profile)
            _showLoginEvent.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            settings.saveUserProfile(null)
            _messages.value = emptyList()
            _showLoginEvent.value = true
        }
    }

    fun setServerUrl(url: String) {
        viewModelScope.launch { settings.setServerUrl(url) }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.value += message
        persistHistory()
    }

    private fun persistHistory() {
        viewModelScope.launch {
            settings.saveChatHistory(_messages.value.takeLast(200))
        }
    }

    companion object {
        val defaultModels = listOf(
            ModelOption(
                id = "deepseek-r1-distill-llama-70b",
                name = "Deepseek R1",
                desc = "Самая мощная модель",
                color = Color(0xFFA78BFA),
            ),
            ModelOption(
                id = "openai/gpt-oss-120b",
                name = "GPT OSS",
                desc = "Баланс скорости и качества",
                color = Color(0xFF60A5FA),
            ),
            ModelOption(
                id = "zai-glm-4.7",
                name = "GLM 4.7",
                desc = "Максимальная скорость",
                color = Color(0xFF34D399),
            ),
        )
    }
}
