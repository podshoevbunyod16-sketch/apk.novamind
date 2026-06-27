package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.model.AdminSettingsRequest
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.BgBase
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgSurface
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary
import com.agon.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = viewModel(),
) {
    val stats by viewModel.stats.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()
    var prompt by remember(stats) { mutableStateOf(stats?.system_prompt ?: "") }

    LaunchedEffect(Unit) { viewModel.loadStats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель управления") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadStats() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSurface),
            )
        },
        containerColor = BgBase,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(color = Accent, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            stats?.let { s ->
                AdminCard(title = "Модель") {
                    Text("Провайдер: ${s.current_provider}", color = TextPrimary)
                    Text("Модель: ${s.current_model}", color = TextPrimary)
                    Text("Сообщений: ${s.history_messages}", color = TextPrimary)
                }

                AdminCard(title = "Плагины и команды") {
                    Text("Плагины: ${s.plugins_loaded.joinToString()}", color = TextPrimary)
                    Text("Кастомные команды: ${s.custom_commands.joinToString()}", color = TextPrimary)
                }

                AdminCard(title = "System Prompt") {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        maxLines = 10,
                    )
                    TextButton(
                        onClick = {
                            viewModel.saveSettings(AdminSettingsRequest(system_prompt = prompt))
                        },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Сохранить", color = TextAccent)
                    }
                }
            } ?: run {
                Text(
                    text = "Не удалось загрузить статистику. Проверьте URL сервера в настройках.",
                    color = TextSecondary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Server URL: $serverUrl", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun AdminCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BgCard),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextAccent)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
