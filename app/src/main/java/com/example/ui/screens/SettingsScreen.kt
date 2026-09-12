package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.preferences.BatteryMode
import com.example.data.preferences.JarvisSettings
import com.example.data.preferences.OperationMode
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentSettings: JarvisSettings,
    onSaveSettings: (JarvisSettings) -> Unit,
    onTestVoice: () -> Unit,
    onBack: () -> Unit
) {
    var wakeWord by remember { mutableStateOf(currentSettings.wakeWord) }
    var operationMode by remember { mutableStateOf(currentSettings.operationMode) }
    var sessionTimeout by remember { mutableIntStateOf(currentSettings.sessionTimeoutSeconds) }
    var speechSpeed by remember { mutableFloatStateOf(currentSettings.speechSpeed) }
    var speechPitch by remember { mutableFloatStateOf(currentSettings.speechPitch) }
    var geminiKey by remember { mutableStateOf(currentSettings.customGeminiApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var aiFallback by remember { mutableStateOf(currentSettings.enableAiFallback) }
    var musicApp by remember { mutableStateOf(currentSettings.defaultMusicApp) }
    var musicPkg by remember { mutableStateOf(currentSettings.defaultMusicPackage) }
    var city by remember { mutableStateOf(currentSettings.weatherCity) }
    var unit by remember { mutableStateOf(currentSettings.weatherUnit) }
    var keepScreenOn by remember { mutableStateOf(currentSettings.keepScreenOn) }
    var startOnBoot by remember { mutableStateOf(currentSettings.startOnBoot) }
    var batteryMode by remember { mutableStateOf(currentSettings.batteryMode) }

    fun commitChanges() {
        onSaveSettings(
            currentSettings.copy(
                wakeWord = wakeWord,
                operationMode = operationMode,
                sessionTimeoutSeconds = sessionTimeout,
                speechSpeed = speechSpeed,
                speechPitch = speechPitch,
                customGeminiApiKey = geminiKey,
                enableAiFallback = aiFallback,
                defaultMusicApp = musicApp,
                defaultMusicPackage = musicPkg,
                weatherCity = city,
                weatherUnit = unit,
                keepScreenOn = keepScreenOn,
                startOnBoot = startOnBoot,
                batteryMode = batteryMode
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jarvis Settings",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            commitChanges()
                            onBack()
                        },
                        modifier = Modifier.testTag("settings_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = JarvisCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianDark
                )
            )
        },
        containerColor = ObsidianDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Voice & Wake Word Section
            item {
                SettingsSectionHeader(icon = Icons.Default.Mic, title = "Voice & Wake Word")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Wake Word Phrase",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Hey Jarvis", "Jarvis", "Computer").forEach { phrase ->
                                FilterChip(
                                    selected = wakeWord == phrase,
                                    onClick = {
                                        wakeWord = phrase
                                        commitChanges()
                                    },
                                    label = { Text(phrase) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JarvisCyan,
                                        selectedLabelColor = ObsidianDark,
                                        containerColor = ObsidianDark,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Operation Mode",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            OperationMode.values().forEach { mode ->
                                val label = when (mode) {
                                    OperationMode.PUSH_TO_TALK -> "Mode A • Push-to-Talk (Default)"
                                    OperationMode.VOICE_SESSION -> "Mode B • Voice Session (15s Window)"
                                    OperationMode.ALWAYS_LISTENING -> "Mode C • Always Listening (\"Hey Jarvis\")"
                                }
                                FilterChip(
                                    selected = operationMode == mode,
                                    onClick = {
                                        operationMode = mode
                                        commitChanges()
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JarvisCyan,
                                        selectedLabelColor = ObsidianDark,
                                        containerColor = ObsidianDark,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        AnimatedVisibility(visible = operationMode == OperationMode.ALWAYS_LISTENING) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AccentWarning.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, AccentWarning.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Warning",
                                        tint = AccentWarning,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Always Listening uses additional battery power and may increase device temperature. Recommended when connected to a charger.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        if (operationMode == OperationMode.VOICE_SESSION) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Session Followup Timeout: ${sessionTimeout}s",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Slider(
                                value = sessionTimeout.toFloat(),
                                onValueChange = {
                                    sessionTimeout = it.roundToInt()
                                    commitChanges()
                                },
                                valueRange = 5f..30f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = JarvisCyan,
                                    activeTrackColor = JarvisCyan
                                )
                            )
                        }
                    }
                }
            }

            // 2. Speech & TTS Section
            item {
                SettingsSectionHeader(icon = Icons.Default.GraphicEq, title = "Speech & TTS")
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = "Speech Speed: ${(speechSpeed * 10).roundToInt() / 10.0}x",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Button(
                                onClick = onTestVoice,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = JarvisCyan.copy(alpha = 0.2f),
                                    contentColor = JarvisCyan
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Test Voice", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Slider(
                            value = speechSpeed,
                            onValueChange = {
                                speechSpeed = it
                                commitChanges()
                            },
                            valueRange = 0.6f..1.6f,
                            colors = SliderDefaults.colors(
                                thumbColor = JarvisCyan,
                                activeTrackColor = JarvisCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Speech Pitch: ${(speechPitch * 10).roundToInt() / 10.0}x",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Slider(
                            value = speechPitch,
                            onValueChange = {
                                speechPitch = it
                                commitChanges()
                            },
                            valueRange = 0.7f..1.4f,
                            colors = SliderDefaults.colors(
                                thumbColor = JarvisCyan,
                                activeTrackColor = JarvisCyan
                            )
                        )
                    }
                }
            }

            // 3. Music & SimpMusic Integration
            item {
                SettingsSectionHeader(icon = Icons.Default.MusicNote, title = "Music & SimpMusic")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = musicApp,
                            onValueChange = {
                                musicApp = it
                                commitChanges()
                            },
                            label = { Text("Default Music Alias") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyan,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = musicPkg,
                            onValueChange = {
                                musicPkg = it
                                commitChanges()
                            },
                            label = { Text("SimpMusic Package Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyan,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "SimpMusic package defaults to com.maxrave.simpmusic",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // 4. AI Fallback & Gemini
            item {
                SettingsSectionHeader(icon = Icons.Default.Psychology, title = "General Knowledge & AI")
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable AI Fallback",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Answer general questions using Gemini 3.5 Flash",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = aiFallback,
                                onCheckedChange = {
                                    aiFallback = it
                                    commitChanges()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JarvisCyan,
                                    checkedTrackColor = JarvisCyan.copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = geminiKey,
                            onValueChange = {
                                geminiKey = it
                                commitChanges()
                            },
                            label = { Text("Custom Gemini API Key") },
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                Button(
                                    onClick = { isKeyVisible = !isKeyVisible },
                                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (isKeyVisible) "Hide" else "Show", style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyan,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Leave empty to use the system injected Gemini API key from AI Studio secrets.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // 5. Weather Settings
            item {
                SettingsSectionHeader(icon = Icons.Default.WbSunny, title = "Weather Configuration")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {
                                city = it
                                commitChanges()
                            },
                            label = { Text("Default City / Location") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyan,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Temperature Unit",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Celsius", "Fahrenheit").forEach { u ->
                                    FilterChip(
                                        selected = unit == u,
                                        onClick = {
                                            unit = u
                                            commitChanges()
                                        },
                                        label = { Text(u) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = JarvisCyan,
                                            selectedLabelColor = ObsidianDark,
                                            containerColor = ObsidianDark,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. System & Power
            item {
                SettingsSectionHeader(icon = Icons.Default.BatteryChargingFull, title = "System & Power Mode")
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keep Screen On (Desk Dock)", color = TextPrimary, style = MaterialTheme.typography.titleSmall)
                                Text("Prevents screen timeout when docked", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = keepScreenOn,
                                onCheckedChange = {
                                    keepScreenOn = it
                                    commitChanges()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JarvisCyan,
                                    checkedTrackColor = JarvisCyan.copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Start on Boot", color = TextPrimary, style = MaterialTheme.typography.titleSmall)
                                Text("Launches background service after restart", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = startOnBoot,
                                onCheckedChange = {
                                    startOnBoot = it
                                    commitChanges()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JarvisCyan,
                                    checkedTrackColor = JarvisCyan.copy(alpha = 0.3f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Battery Profile", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BatteryMode.values().forEach { mode ->
                                val name = when (mode) {
                                    BatteryMode.BALANCED -> "Balanced"
                                    BatteryMode.LOW_POWER -> "Low Power"
                                    BatteryMode.ALWAYS_ON -> "Always On"
                                }
                                FilterChip(
                                    selected = batteryMode == mode,
                                    onClick = {
                                        batteryMode = mode
                                        commitChanges()
                                    },
                                    label = { Text(name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JarvisCyan,
                                        selectedLabelColor = ObsidianDark,
                                        containerColor = ObsidianDark,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 7. Privacy Notice
            item {
                SettingsSectionHeader(icon = Icons.Default.Security, title = "Privacy Guarantee")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "• Local Commands (Time, Date, Apps, Media, Volume) execute 100% on device.\n" +
                                    "• Weather queries Open-Meteo with no personal telemetry.\n" +
                                    "• General Knowledge queries Gemini only when a command is not local.\n" +
                                    "• No audio recordings are ever stored or retained.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.3
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = JarvisCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = JarvisCyan
        )
    }
}
