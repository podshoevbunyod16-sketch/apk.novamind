package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.BgBase
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgSurface
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary
import com.agon.app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val serverUrl by viewModel.serverUrl.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    var urlField by remember(serverUrl) { mutableStateOf(serverUrl) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Сервер NovaMind",
                style = MaterialTheme.typography.titleMedium,
                color = TextAccent,
            )
            OutlinedTextField(
                value = urlField,
                onValueChange = { urlField = it },
                label = { Text("URL сервера") },
                placeholder = { Text("http://10.0.2.2:5000") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Text(
                text = "Оставьте пустым для автономного режима. Для эмулятора Android используйте 10.0.2.2:5000.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            Button(
                onClick = { viewModel.saveServerUrl(urlField) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
            ) {
                Text("Сохранить")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Тёмная тема", color = TextPrimary)
                    Text("Использовать тёмный интерфейс", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Switch(checked = darkTheme, onCheckedChange = { viewModel.setDarkTheme(it) })
            }
        }
    }
}
