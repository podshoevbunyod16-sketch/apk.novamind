package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.InputMode
import com.agon.app.data.model.UserProfile
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.AccentDim
import com.agon.app.ui.theme.AccentLight
import com.agon.app.ui.theme.BgHover
import com.agon.app.ui.theme.BgPanel
import com.agon.app.ui.theme.Border
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.Error
import com.agon.app.ui.theme.ErrorDim
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextMuted
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@Composable
fun DrawerContent(
    user: UserProfile?,
    onNewChat: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onModeSelected: (InputMode) -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(BgPanel)
            .padding(horizontal = 14.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Accent, Color(0xFF4F46E5)))),
                contentAlignment = Alignment.Center,
            ) {
                Text("🧠", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Khirad", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("Assistant", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onNewChat,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentDim),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = TextAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Новый диалог", color = TextAccent)
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionLabel("Основные")
        NavRow(icon = Icons.Default.ChatBubble, label = "Чат", selected = true) {
            onNavigate("chat")
        }
        NavRow(icon = Icons.Default.Search, label = "Веб-поиск", badge = "Beta") {
            onModeSelected(InputMode("/search ", "Введите запрос для поиска..."))
        }
        NavRow(icon = Icons.Default.Image, label = "Генерация изображений") {
            onModeSelected(InputMode("/image ", "Опишите изображение для генерации..."))
        }
        NavRow(icon = Icons.Default.Code, label = "Помощник кода") {
            onModeSelected(InputMode("/code ", "Опишите, какой код создать..."))
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionLabel("Сервисы")
        NavRow(icon = null, emoji = "🌤", label = "Погода") {
            onModeSelected(InputMode("/services weather ", "Введите город для погоды..."))
        }
        NavRow(icon = null, emoji = "💱", label = "Курс валют") {
            onModeSelected(InputMode("/services currency ", "Введите валюты (USD RUB)..."))
        }
        NavRow(icon = null, emoji = "📚", label = "Википедия") {
            onModeSelected(InputMode("/services wiki ", "Введите запрос для Википедии..."))
        }
        NavRow(icon = Icons.Default.Link, label = "Composio MCP") {
            onNavigate("composio")
        }

        if (user?.isAdmin == true) {
            Spacer(modifier = Modifier.height(14.dp))
            SectionLabel("Админ")
            NavRow(icon = Icons.Default.AdminPanelSettings, label = "Панель управления") {
                onAdminClick()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(12.dp))
        UserCard(user = user, onLogout = onLogout)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun NavRow(
    icon: ImageVector?,
    label: String,
    selected: Boolean = false,
    badge: String? = null,
    emoji: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) AccentDim else Color.Transparent)
            .border(1.dp, if (selected) Border else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            emoji != null -> Text(emoji, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(24.dp))
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (selected) TextAccent else TextSecondary,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) TextAccent else TextSecondary,
            modifier = Modifier.weight(1f),
        )
        badge?.let {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(Accent)
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            ) {
                Text(it, style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}

@Composable
private fun UserCard(user: UserProfile?, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BgHover)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Accent, Color(0xFF4F46E5)))),
            contentAlignment = Alignment.Center,
        ) {
            Text("👤", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user?.nick ?: "Пользователь",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text("Базовый план", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ErrorDim)
                .border(1.dp, Error.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .clickable(onClick = onLogout)
                .padding(horizontal = 8.dp, vertical = 5.dp),
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Error, modifier = Modifier.size(14.dp))
        }
    }
}
