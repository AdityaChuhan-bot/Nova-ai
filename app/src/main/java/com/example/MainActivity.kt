package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.dialogs.MusicQuickCard
import com.example.ui.dialogs.WeatherQuickCard
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val settings by viewModel.settings.collectAsState()
                val assistantState by viewModel.assistantState.collectAsState()
                val spokenText by viewModel.spokenText.collectAsState()
                val responseText by viewModel.responseText.collectAsState()
                val audioRmsLevel by viewModel.audioRmsLevel.collectAsState()
                val clockTime by viewModel.currentTimeString.collectAsState()
                val clockDate by viewModel.currentDateString.collectAsState()
                val isOnline by viewModel.isOnline.collectAsState()
                val currentVolume by viewModel.currentVolume.collectAsState()

                // Dynamic Keep Screen On based on settings
                LaunchedEffect(settings.keepScreenOn) {
                    if (settings.keepScreenOn) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                var showSettings by remember { mutableStateOf(false) }
                var showMusicCard by remember { mutableStateOf(false) }
                var showWeatherCard by remember { mutableStateOf(false) }

                // Audio recording permission launcher
                var hasAudioPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    hasAudioPermission = isGranted
                    if (isGranted) {
                        viewModel.onMicTapped()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (showSettings) {
                        SettingsScreen(
                            currentSettings = settings,
                            onSaveSettings = { updated ->
                                viewModel.updateSettings(updated)
                            },
                            onTestVoice = {
                                viewModel.executeTextCommand("Tell me the time")
                            },
                            onBack = { showSettings = false }
                        )
                    } else {
                        HomeScreen(
                            assistantState = assistantState,
                            spokenText = spokenText,
                            responseText = responseText,
                            audioRmsLevel = audioRmsLevel,
                            clockTime = clockTime,
                            clockDate = clockDate,
                            isOnline = isOnline,
                            currentVolume = currentVolume,
                            settings = settings,
                            onMicTapped = {
                                if (hasAudioPermission) {
                                    viewModel.onMicTapped()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onStopSpeaking = { viewModel.onStopSpeakingTapped() },
                            onCommandSubmitted = { cmd -> viewModel.executeTextCommand(cmd) },
                            onOpenMusicCard = { showMusicCard = true },
                            onOpenWeatherCard = { showWeatherCard = true },
                            onOpenSettings = { showSettings = true }
                        )
                    }

                    // Music & Media Sheet Dialog
                    if (showMusicCard) {
                        val isSimpInstalled = remember(settings.defaultMusicPackage) {
                            viewModel.appController.isAppInstalled(settings.defaultMusicPackage)
                        }

                        MusicQuickCard(
                            currentVolume = currentVolume,
                            isSimpMusicInstalled = isSimpInstalled,
                            onLaunchSimpMusic = {
                                viewModel.launchApp(settings.defaultMusicPackage)
                                showMusicCard = false
                            },
                            onPlayPause = { viewModel.togglePlayPause() },
                            onNext = { viewModel.nextTrack() },
                            onPrevious = { viewModel.previousTrack() },
                            onStop = { viewModel.mediaController.stop() },
                            onVolumeChanged = { percent -> viewModel.setVolume(percent) },
                            onMute = { viewModel.volumeController.mute(); viewModel.refreshVolume() },
                            onDismiss = { showMusicCard = false }
                        )
                    }

                    // Weather Sheet Dialog
                    if (showWeatherCard) {
                        WeatherQuickCard(
                            city = settings.weatherCity,
                            unit = settings.weatherUnit,
                            lastWeatherResponse = responseText,
                            onRefreshWeather = {
                                viewModel.executeTextCommand("What is the weather in ${settings.weatherCity}")
                            },
                            onForecastRequested = {
                                viewModel.executeTextCommand("What is the weather forecast for ${settings.weatherCity}")
                            },
                            onDismiss = { showWeatherCard = false }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshVolume()
        viewModel.refreshInstalledApps()
    }
}

/**
 * Kept for greeting component tests and modular previews.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
