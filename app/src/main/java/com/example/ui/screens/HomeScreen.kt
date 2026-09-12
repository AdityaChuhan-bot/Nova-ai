package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantState
import com.example.data.preferences.JarvisSettings
import com.example.data.preferences.OperationMode
import com.example.ui.components.JarvisOrb
import com.example.ui.components.QuickCommandChips
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisGlow
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    assistantState: AssistantState,
    spokenText: String,
    responseText: String,
    audioRmsLevel: Float,
    clockTime: String,
    clockDate: String,
    isOnline: Boolean,
    currentVolume: Int,
    settings: JarvisSettings,
    onMicTapped: () -> Unit,
    onStopSpeaking: () -> Unit,
    onCommandSubmitted: (String) -> Unit,
    onOpenMusicCard: () -> Unit,
    onOpenWeatherCard: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showKeyboardInput by remember { mutableStateOf(false) }
    var textInputQuery by remember { mutableStateOf("") }

    val statusDescription = when (assistantState) {
        AssistantState.IDLE -> "Ready"
        AssistantState.WAKE_WORD_LISTENING -> "Waiting for \"${settings.wakeWord}\""
        AssistantState.ACTIVATED -> "Activated"
        AssistantState.LISTENING -> "Listening..."
        AssistantState.PROCESSING -> "Processing command..."
        AssistantState.EXECUTING -> "Executing..."
        AssistantState.SPEAKING -> "Speaking..."
        AssistantState.ERROR -> "Couldn't understand"
    }

    val statusDotColor = when (assistantState) {
        AssistantState.IDLE -> JarvisCyanDim
        AssistantState.WAKE_WORD_LISTENING -> JarvisCyan
        AssistantState.ACTIVATED -> AccentSuccess
        AssistantState.LISTENING -> JarvisCyan
        AssistantState.PROCESSING, AssistantState.EXECUTING -> JarvisBlue
        AssistantState.SPEAKING -> JarvisCyan
        AssistantState.ERROR -> AccentError
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianDark,
        bottomBar = {
            // Bottom Dock
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
            ) {
                // Quick suggestion chips
                QuickCommandChips(
                    onCommandSelected = { cmd ->
                        onCommandSubmitted(cmd)
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Music Dock Button
                    FilledIconButton(
                        onClick = onOpenMusicCard,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("music_dock_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ObsidianCard,
                            contentColor = JarvisCyan
                        )
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = "SimpMusic & Playback")
                    }

                    // Weather Dock Button
                    FilledIconButton(
                        onClick = onOpenWeatherCard,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("weather_dock_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ObsidianCard,
                            contentColor = JarvisCyan
                        )
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = "Weather")
                    }

                    // Keyboard manual input toggle
                    FilledIconButton(
                        onClick = { showKeyboardInput = !showKeyboardInput },
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("keyboard_toggle_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (showKeyboardInput) JarvisCyan.copy(alpha = 0.2f) else ObsidianCard,
                            contentColor = if (showKeyboardInput) JarvisCyan else TextSecondary
                        )
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = "Type Command")
                    }

                    // Settings Dock Button
                    FilledIconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("settings_dock_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ObsidianCard,
                            contentColor = TextSecondary
                        )
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Smart Speaker Status & Clock Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Online/Offline & Volume badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianCard,
                        border = BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = if (isOnline) "Online" else "Offline",
                                tint = if (isOnline) AccentSuccess else AccentWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) "Online" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianCard,
                        border = BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Volume",
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$currentVolume%",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Mode Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianCard,
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Text(
                        text = when (settings.operationMode) {
                            OperationMode.PUSH_TO_TALK -> "Push-To-Talk"
                            OperationMode.VOICE_SESSION -> "Voice Session"
                            OperationMode.ALWAYS_LISTENING -> "Always Listening"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = JarvisCyan,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Big Clock Display (Desk Smart Speaker Look)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            ) {
                Text(
                    text = clockTime.ifEmpty { "12:00 PM" },
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = clockDate.ifEmpty { "Jarvis Smart Speaker" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            // Inline Keyboard Command Input (if toggled)
            AnimatedVisibility(visible = showKeyboardInput) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInputQuery,
                        onValueChange = { textInputQuery = it },
                        placeholder = { Text("Ask Jarvis or type command...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("text_command_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            if (textInputQuery.isNotBlank()) {
                                onCommandSubmitted(textInputQuery)
                                textInputQuery = ""
                                showKeyboardInput = false
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = JarvisCyan,
                            contentColor = ObsidianDark
                        ),
                        modifier = Modifier.size(52.dp).testTag("send_command_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Central Visualizer: Reactive Glowing Jarvis Acoustic Orb
            JarvisOrb(
                state = assistantState,
                audioRms = audioRmsLevel,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Status Indicator Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ObsidianCard,
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusDotColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusDescription,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Primary Microphone Touch Target (84dp, large & high tactile contrast)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                // Glow ring around mic
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(JarvisGlow, Color.Transparent)
                            )
                        )
                )

                Surface(
                    onClick = onMicTapped,
                    shape = CircleShape,
                    color = if (assistantState == AssistantState.LISTENING) JarvisCyan else ObsidianCard,
                    border = BorderStroke(
                        2.dp,
                        if (assistantState == AssistantState.LISTENING) JarvisCyan else ObsidianBorder
                    ),
                    modifier = Modifier
                        .size(84.dp)
                        .testTag("mic_button")
                        .shadow(8.dp, CircleShape)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = when (assistantState) {
                                AssistantState.LISTENING -> Icons.Default.Mic
                                AssistantState.SPEAKING -> Icons.Default.Stop
                                else -> Icons.Default.Mic
                            },
                            contentDescription = "Microphone",
                            tint = if (assistantState == AssistantState.LISTENING) ObsidianDark else JarvisCyan,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Transcription and Spoken Response Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Spoken / Transcribed Input Card
                AnimatedVisibility(visible = spokenText.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transcription_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard.copy(alpha = 0.8f)),
                        border = BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "You:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = JarvisCyan,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = spokenText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Assistant Response Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("response_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Jarvis:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = JarvisCyan
                                )
                                if (assistantState == AssistantState.SPEAKING) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• speaking",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = JarvisCyanDim
                                    )
                                }
                            }

                            if (assistantState == AssistantState.SPEAKING) {
                                Surface(
                                    onClick = onStopSpeaking,
                                    shape = RoundedCornerShape(8.dp),
                                    color = ObsidianDark,
                                    modifier = Modifier.clickable { onStopSpeaking() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop",
                                            tint = AccentError,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Stop", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = responseText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
