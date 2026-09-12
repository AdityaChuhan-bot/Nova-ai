package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextSecondary

@Composable
fun QuickCommandChips(
    onCommandSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val commands = listOf(
        "What time is it?",
        "What's the weather?",
        "Open SimpMusic",
        "Pause music",
        "Today's date",
        "Increase volume",
        "Set volume to 70%",
        "Why is the sky blue?"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        commands.forEach { cmd ->
            OutlinedButton(
                onClick = { onCommandSelected(cmd) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ObsidianCard.copy(alpha = 0.7f),
                    contentColor = TextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Text(
                    text = cmd,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
