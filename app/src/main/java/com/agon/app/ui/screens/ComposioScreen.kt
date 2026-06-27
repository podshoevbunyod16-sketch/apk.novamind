package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agon.app.ui.theme.BgBase
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgSurface
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposioScreen(onBack: () -> Unit) {
    val integrations = listOf(
        "github" to "🐙",
        "gmail" to "📧",
        "notion" to "📝",
        "slack" to "💬",
        "googlecalendar" to "📅",
        "googledrive" to "☁️",
        "trello" to "📋",
        "twitter" to "🐦",
        "discord" to "🎮",
        "jira" to "🔵",
        "linear" to "⚡",
        "youtube" to "▶️",
        "shopify" to "🛒",
        "hubspot" to "🔴",
        "airtable" to "🗃️",
        "dropbox" to "📦",
        "figma" to "🎨",
        "stripe" to "💳",
        "zoom" to "📹",
        "asana" to "🎯",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Composio MCP") },
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
                .padding(16.dp),
        ) {
            Text(
                text = "🦜 Интеграции Composio",
                style = MaterialTheme.typography.titleMedium,
                color = TextAccent,
            )
            Text(
                text = "Нажмите для подключения. После этого используйте команды /composio в чате.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(integrations) { (name, icon) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(icon, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
