package com.agon.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.MessageRole
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.AccentGlow
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.Border
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    val isUser = message.role == MessageRole.USER
    val configuration = LocalConfiguration.current
    val maxBubbleWidth = (configuration.screenWidthDp * 0.78).dp

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 3 },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        ) {
            if (!isUser) {
                Avatar(icon = "✦", isAi = true)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                modifier = Modifier.widthIn(max = maxBubbleWidth),
            ) {
                Text(
                    text = if (isUser) "Вы" else "NovaMind",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                )

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = if (isUser) 14.dp else 4.dp,
                                topEnd = if (isUser) 4.dp else 14.dp,
                                bottomStart = 14.dp,
                                bottomEnd = 14.dp,
                            )
                        )
                        .background(
                            if (isUser) {
                                Brush.linearGradient(listOf(Accent, Color(0xFF5B21B6)))
                            } else {
                                Brush.linearGradient(listOf(BgCard, BgCard))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isUser) AccentGlow else BorderSoft,
                            shape = RoundedCornerShape(
                                topStart = if (isUser) 14.dp else 4.dp,
                                topEnd = if (isUser) 4.dp else 14.dp,
                                bottomStart = 14.dp,
                                bottomEnd = 14.dp,
                            )
                        )
                        .padding(12.dp),
                ) {
                    Column {
                        if (isUser) {
                            Text(
                                text = message.content,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            FormattedMessage(message.content)
                        }

                        message.imageUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Generated image",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp)),
                            )
                        }
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(icon = "👤", isAi = false)
            }
        }
    }
}

@Composable
private fun Avatar(icon: String, isAi: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (isAi) {
                    Brush.linearGradient(listOf(Accent, Color(0xFF4F46E5)))
                } else {
                    Brush.linearGradient(listOf(BgCard, BgCard))
                }
            )
            .border(1.dp, if (isAi) Accent else Border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = if (isAi) TextPrimary else TextSecondary,
        )
    }
}
