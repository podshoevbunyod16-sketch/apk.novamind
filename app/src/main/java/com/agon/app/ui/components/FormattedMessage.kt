package com.agon.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.agon.app.ui.theme.AccentLight
import com.agon.app.ui.theme.TextPrimary
import com.agon.app.ui.theme.TextSecondary

@Composable
fun FormattedMessage(text: String) {
    val segments = splitCodeBlocks(text)

    Column {
        segments.forEach { segment ->
            when (segment) {
                is Segment.Code -> CodeBlock(code = segment.code)
                is Segment.Text -> Text(
                    text = formatInline(segment.text),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
    }
}

private sealed class Segment {
    data class Text(val text: String) : Segment()
    data class Code(val code: String) : Segment()
}

private fun splitCodeBlocks(text: String): List<Segment> {
    val result = mutableListOf<Segment>()
    val regex = "```([\\w\\n]*)\\n?([\\s\\S]*?)```".toRegex()
    var last = 0
    regex.findAll(text).forEach { match ->
        if (match.range.first > last) {
            result.add(Segment.Text(text.substring(last, match.range.first)))
        }
        result.add(Segment.Code(match.groupValues[2].trim()))
        last = match.range.last + 1
    }
    if (last < text.length) {
        result.add(Segment.Text(text.substring(last)))
    }
    if (result.isEmpty()) result.add(Segment.Text(text))
    return result
}

@Composable
private fun formatInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        var remaining = text
            .replace("\\*\\*(.+?)\\*\\*".toRegex()) { "\u0000B\u0000${it.groupValues[1]}\u0000B\u0000" }
            .replace("`([^`]+)`".toRegex()) { "\u0000C\u0000${it.groupValues[1]}\u0000C\u0000" }
            .replace("\\*(.+?)\\*".toRegex()) { "\u0000I\u0000${it.groupValues[1]}\u0000I\u0000" }
            .replace("\\\\n\\\\n".toRegex(), "\n\n")
            .replace("\\\\n".toRegex(), "\n")

        while (remaining.isNotEmpty()) {
            val markerIdx = remaining.indexOf('\u0000')
            if (markerIdx == -1) {
                append(remaining)
                break
            }
            if (markerIdx > 0) append(remaining.substring(0, markerIdx))

            val type = remaining.getOrNull(markerIdx + 1)
            val endIdx = remaining.indexOf("\u0000$type\u0000", markerIdx + 2)
            if (endIdx == -1 || type == null) {
                append(remaining)
                break
            }
            val content = remaining.substring(markerIdx + 3, endIdx)
            when (type) {
                'B' -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(content) }
                'I' -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(content) }
                'C' -> withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = androidx.compose.ui.graphics.Color(0x22000000),
                        color = AccentLight,
                    )
                ) { append(content) }
            }
            remaining = remaining.substring(endIdx + 3)
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFF0A0A14),
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.agon.app.ui.theme.Border),
    ) {
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(12.dp),
        )
    }
}
