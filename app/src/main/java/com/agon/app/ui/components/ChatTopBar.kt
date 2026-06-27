package com.agon.app.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OnlinePrediction
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.ModelOption
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.AccentDim
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BgSurface
import com.agon.app.ui.theme.Border
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.Success
import com.agon.app.ui.theme.SuccessDim
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    selectedModel: ModelOption,
    models: List<ModelOption>,
    dropdownOpen: Boolean,
    onToggleDropdown: () -> Unit,
    onCloseDropdown: () -> Unit,
    onSelectModel: (ModelOption) -> Unit,
    onMenuClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AI Ассистент",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BgCard)
                            .border(1.dp, Border, RoundedCornerShape(99.dp))
                            .clickable { onToggleDropdown() }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(selectedModel.color),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedModel.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = TextAccent,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownOpen,
                        onDismissRequest = onCloseDropdown,
                        modifier = Modifier.background(com.agon.app.ui.theme.BgPanel),
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(model.color),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(model.name, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                                            Text(model.desc, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                },
                                onClick = { onSelectModel(model) },
                            )
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextSecondary,
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(SuccessDim)
                    .border(1.dp, Success.copy(alpha = 0.25f), RoundedCornerShape(99.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Success),
                )
                Text("Online", style = MaterialTheme.typography.labelSmall, color = Success)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear chat",
                    tint = TextSecondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSurface),
    )
}
