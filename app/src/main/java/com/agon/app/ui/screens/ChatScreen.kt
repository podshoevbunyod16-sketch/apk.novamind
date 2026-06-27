package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.model.InputMode
import com.agon.app.ui.components.ChatInput
import com.agon.app.ui.components.ChatTopBar
import com.agon.app.ui.components.DrawerContent
import com.agon.app.ui.components.MessageBubble
import com.agon.app.ui.components.SuggestionCard
import com.agon.app.ui.components.TypingIndicator
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.AccentDim
import com.agon.app.ui.theme.AccentGlow
import com.agon.app.ui.theme.BgBase
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgHover
import com.agon.app.ui.theme.BgSurface
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextMuted
import com.agon.app.ui.theme.TextSecondary
import com.agon.app.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    onNavigate: (String) -> Unit,
    viewModel: ChatViewModel = viewModel(),
) {
    val messages by viewModel.messages.collectAsState()
    val input by viewModel.input.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val canSend by viewModel.canSend.collectAsState()
    val webSearch by viewModel.webSearch.collectAsState()
    val reasoning by viewModel.reasoning.collectAsState()
    val autoSearch by viewModel.autoSearch.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val dropdownOpen by viewModel.modelDropdownOpen.collectAsState()
    val user by viewModel.userProfile.collectAsState()
    val drawerOpen by viewModel.drawerOpen.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val drawerState = rememberDrawerState(
        initialValue = if (drawerOpen) DrawerValue.Open else DrawerValue.Closed,
        confirmStateChange = {
            viewModel.closeDrawer()
            true
        }
    )

    LaunchedEffect(drawerOpen) {
        if (drawerOpen) drawerState.open() else drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = com.agon.app.ui.theme.BgPanel) {
                DrawerContent(
                    user = user,
                    onNewChat = { viewModel.newChat() },
                    onNavigate = { route ->
                        viewModel.closeDrawer()
                        onNavigate(route)
                    },
                    onLogout = { viewModel.logout() },
                    onModeSelected = { mode ->
                        viewModel.setInputMode(mode)
                        viewModel.closeDrawer()
                    },
                    onAdminClick = {
                        viewModel.closeDrawer()
                        onNavigate("admin")
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    ChatTopBar(
                        selectedModel = selectedModel,
                        models = ChatViewModel.defaultModels,
                        dropdownOpen = dropdownOpen,
                        onToggleDropdown = { viewModel.toggleModelDropdown() },
                        onCloseDropdown = { viewModel.closeModelDropdown() },
                        onSelectModel = { viewModel.selectModel(it) },
                        onMenuClick = { viewModel.toggleDrawer() },
                        onClearClick = { viewModel.clearChat() },
                    )
                },
                containerColor = BgBase,
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(BgBase),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (messages.isEmpty()) {
                            WelcomeScreen(onSuggestion = { viewModel.sendSuggestion(it) })
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                itemsIndexed(messages, key = { index, item -> "$index-${item.timestamp}" }) { _, message ->
                                    MessageBubble(message = message)
                                }
                                if (isTyping) {
                                    item { TypingIndicator() }
                                }
                            }
                        }
                    }

                    ChatInput(
                        value = input,
                        onValueChange = { viewModel.onInputChange(it) },
                        onSend = { viewModel.send() },
                        canSend = canSend,
                        webSearch = webSearch,
                        onToggleWebSearch = { viewModel.toggleWebSearch() },
                        reasoning = reasoning,
                        onToggleReasoning = { viewModel.toggleReasoning() },
                        autoSearch = autoSearch,
                        onToggleAutoSearch = { viewModel.toggleAutoSearch() },
                        onAttachImage = { uri, desc -> viewModel.attachImage(uri, desc) },
                        onAttachDocument = { uri, desc -> viewModel.attachDocument(uri, desc) },
                        isRecording = false,
                        onToggleVoice = { /* voice input would go here */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgSurface)
                            .padding(12.dp),
                    )
                }
            }
        },
    )
}

@Composable
private fun WelcomeScreen(onSuggestion: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Accent, Color(0xFF4F46E5)))),
            contentAlignment = Alignment.Center,
        ) {
            Text("🧠", style = MaterialTheme.typography.displayMedium)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Привет! Я  ",
            style = MaterialTheme.typography.headlineMedium,
            color = com.agon.app.ui.theme.TextPrimary,
        )
        Text(
            text = "Khirad",
            style = MaterialTheme.typography.headlineMedium,
            color = Accent,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ваш интеллектуальный AI-ассистент. \u0417адайте любой вопрос или используйте инструменты из боковой панели.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionCard(
                    icon = "🌤",
                    title = "Погода",
                    desc = "Узнайте погоду в любом городе",
                    onClick = { onSuggestion("/services weather ") },
                    modifier = Modifier.weight(1f),
                )
                SuggestionCard(
                    icon = "💻",
                    title = "Напиши код",
                    desc = "Сгенерировать код на любом языке",
                    onClick = { onSuggestion("/code ") },
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionCard(
                    icon = "💱",
                    title = "Курс валют",
                    desc = "Актуальные курсы валют",
                    onClick = { onSuggestion("/services currency ") },
                    modifier = Modifier.weight(1f),
                )
                SuggestionCard(
                    icon = "🔍",
                    title = "Поиск в интернете",
                    desc = "Найти информацию через поиск",
                    onClick = { onSuggestion("/search ") },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
