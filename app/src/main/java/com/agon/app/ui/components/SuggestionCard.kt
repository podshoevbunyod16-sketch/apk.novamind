package com.agon.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agon.app.ui.theme.BgCard
import com.agon.app.ui.theme.BorderSoft
import com.agon.app.ui.theme.TextAccent
import com.agon.app.ui.theme.TextMuted

@Composable
fun SuggestionCard(
    icon: String,
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(1.dp, BorderSoft),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = TextAccent,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
