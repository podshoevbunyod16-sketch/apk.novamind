package com.agon.app.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.AccentDim
import com.agon.app.ui.theme.AccentLight
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgHover
import com.agon.app.ui.theme.BgInput
import com.agon.app.ui.theme.Border
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.Error
import com.agon.app.ui.theme.ErrorDim
import com.agon.app.ui.theme.Success
import com.agon.app.ui.theme.SuccessDim
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextMuted
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    canSend: Boolean,
    webSearch: Boolean,
    onToggleWebSearch: () -> Unit,
    reasoning: Boolean,
    onToggleReasoning: () -> Unit,
    autoSearch: Boolean,
    onToggleAutoSearch: () -> Unit,
    onAttachImage: (android.net.Uri, String) -> Unit,
    onAttachDocument: (android.net.Uri, String) -> Unit,
    isRecording: Boolean,
    onToggleVoice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var attachOpen by remember { mutableStateOf(false) }
    var pendingImageDescription by remember { mutableStateOf<String?>(null) }
    var pendingFileDescription by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { pendingImageDescription = it.toString() }
    }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { pendingFileDescription = it.toString() }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolbarChip(
                icon = Icons.Default.Search,
                label = "Поиск в интернете",
                active = webSearch,
                onClick = onToggleWebSearch,
            )
            ToolbarChip(
                icon = Icons.Default.SettingsSuggest,
                label = "Рассуждение",
                active = reasoning,
                onClick = onToggleReasoning,
            )
            Box {
                ToolbarChip(
                    icon = Icons.Default.AttachFile,
                    label = "Прикрепить",
                    active = false,
                    onClick = { attachOpen = !attachOpen },
                )
                if (attachOpen) {
                    Column(
                        modifier = Modifier
                            .padding(top = 34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgCard)
                            .border(1.dp, Border, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                    ) {
                        AttachOption(icon = Icons.Default.Image, label = "Изображение") {
                            attachOpen = false
                            imagePicker.launch("image/*")
                        }
                        AttachOption(icon = Icons.Default.AttachFile, label = "Файл") {
                            attachOpen = false
                            filePicker.launch("*/*")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            ToolbarChip(
                icon = Icons.Default.ZoomIn,
                label = "Авто поиск",
                active = autoSearch,
                onClick = onToggleAutoSearch,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BgInput)
                .border(1.5.dp, if (value.isNotEmpty()) Accent else Border, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                maxLines = 6,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Напишите сообщение...",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onToggleVoice,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) ErrorDim else BgCard)
                    .border(1.dp, if (isRecording) Error else Border, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = if (isRecording) Error else TextSecondary,
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (canSend) Accent else BgCard),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (canSend) Color.White else TextMuted,
                )
            }
        }

        Text(
            text = "Khirad может ошибаться. Проверяйте важную информацию.",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
        )
    }

    pendingImageDescription?.let { uriString ->
        val uri = android.net.Uri.parse(uriString)
        DescriptionDialog(
            title = "Что сделать с изображением?",
            onConfirm = { desc ->
                onAttachImage(uri, desc)
                pendingImageDescription = null
            },
            onDismiss = { pendingImageDescription = null },
        )
    }

    pendingFileDescription?.let { uriString ->
        val uri = android.net.Uri.parse(uriString)
        DescriptionDialog(
            title = "Что сделать с файлом?",
            onConfirm = { desc ->
                onAttachDocument(uri, desc)
                pendingFileDescription = null
            },
            onDismiss = { pendingFileDescription = null },
        )
    }
}

@Composable
private fun ToolbarChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (active) AccentDim else BgCard)
            .border(1.dp, if (active) Accent else BorderSoft, RoundedCornerShape(99.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (active) AccentLight else TextMuted,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) TextAccent else TextMuted,
        )
    }
}

@Composable
private fun AttachOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}
