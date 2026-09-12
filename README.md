# 🎙️ Jarvis Mini (Nova AI)

[![Android CI](https://github.com/AdityaChuhan-bot/Nova-ai/actions/workflows/build-apk.yml/badge.svg)](https://github.com/AdityaChuhan-bot/Nova-ai/actions/workflows/build-apk.yml)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-brightgreen?logo=android)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Design](https://img.shields.io/badge/Design-Material%203-00897B?logo=materialdesign)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> A lightweight, privacy-focused Android Voice Assistant and DIY Smart Speaker display designed for modern Android devices and repurposing low-resource hardware into intelligent ambient displays.

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Screenshots & UI Showcase](#-screenshots--ui-showcase)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration (.env)](#configuration-env)
  - [Building from Source](#building-from-source)
  - [Downloading Pre-Built APKs](#downloading-pre-built-apks)
- [DIY Smart Speaker Deployment](#-diy-smart-speaker-deployment)
- [CI/CD & Automation](#-cicd--automation)
- [Contributing](#-contributing)
- [Security](#-security)
- [License](#-license)

---

## 🌟 Overview

**Jarvis Mini** turns any Android phone or tablet into an always-ready, intelligent voice assistant and smart hub. Built entirely with Kotlin and Jetpack Compose, it features dynamic audio wave visualization, ambient smart display mode, live weather integration, and Google Gemini AI responses.

Whether docked on a desk stand as a productivity dashboard or wall-mounted as a smart home display, Jarvis Mini delivers immediate voice responses with minimal battery and memory overhead.

---

## ✨ Key Features

- **🎙️ Conversational Voice Engine**: Real-time voice recognition coupled with Gemini AI processing for natural, context-aware answers.
- **🌊 Interactive Sound Wave Visualizer**: Dynamic RMS-reactive audio canvas offering fluid, visually responsive feedback during speech input.
- **🕰️ Ambient Smart Clock Display**: Clean, distraction-free standby interface displaying dynamic digital time, calendar date, and connectivity status.
- **⛅ Live Weather Intelligence**: Quick-glance weather cards with temperature, humidity, precipitation, and conditions.
- **🎵 Media Quick Controls**: Integrated audio card for playback controls and quick media access.
- **💡 Low-Resource & Battery Friendly**: Engineered with strict single-activity MVVM architecture, optimized compose recompositions, and optional "Keep Screen On" power management.
- **🔒 Privacy First**: Direct, on-device Gemini API integration via secure Gradle Secrets—no third-party telemetry or middleware servers.

---

## 🏛️ System Architecture

Jarvis Mini follows modern Android Architecture guidelines with separation of concerns:

```
app/src/main/java/com/example/
├── MainActivity.kt         # Edge-to-edge entry point & window flags
├── data/                   # Data sources, preferences, and state models
├── device/                 # Hardware access (audio manager, screen keep-alive)
├── domain/                 # Business logic and use cases
├── network/                # Gemini AI & Weather network providers
├── receiver/               # System broadcast receivers (boot, power, network)
├── service/                # Background assistant voice services
├── ui/
│   ├── dialogs/            # WeatherQuickCard, MusicQuickCard dialogs
│   ├── screens/            # HomeScreen, SettingsScreen
│   ├── theme/              # Material 3 ColorScheme, Typography & Shapes
│   └── viewmodel/          # JarvisViewModel managing unidirectional state
└── voice/                  # SpeechRecognizer & Audio RMS level listeners
```

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Language** | Kotlin 2.1+ |
| **UI Toolkit** | Jetpack Compose (BOM 2025.02.00) |
| **Design System** | Material Design 3 (M3) Dynamic Theming |
| **Architecture** | MVVM + Unidirectional Data Flow (StateFlow) |
| **Concurrency** | Kotlin Coroutines & Asynchronous Flow |
| **AI Integration** | Google Gemini API (Firebase AI / REST) |
| **Secrets Management** | Secrets Gradle Plugin (`.env` file convention) |
| **CI / CD** | GitHub Actions with automated APK build & artifacts |
| **Compatibility** | Min SDK: 24 (Android 7.0) • Target SDK: 36 (Android 16) |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Java Development Kit 17 (Eclipse Temurin or OpenJDK recommended)
- **Android SDK**: Platform 36 & Build Tools 36.0.0
- **A Gemini API Key**: Obtain a free key from [Google AI Studio](https://aistudio.google.com/)

### Configuration (.env)

The project utilizes the **Secrets Gradle Plugin** to securely inject API keys at compile time.

1. Duplicate `.env.example` to `.env` in the repository root:
   ```bash
   cp .env.example .env
   ```

2. Open `.env` and configure your API credentials:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```

> ⚠️ **Note**: `.env` is automatically ignored by Git to ensure sensitive credentials are never committed.

### Building from Source

#### Command Line (CLI)

Build the debug APK:
```bash
# Linux / macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### Android Studio

1. Open Android Studio and select **Open**.
2. Navigate to and select the project directory.
3. Allow Gradle to sync dependencies.
4. Select `app` in the run configuration drop-down and click **Run (Shift + F10)**.

### Downloading Pre-Built APKs

Every commit and pull request to `main` is automatically compiled and verified by GitHub Actions.

1. Navigate to the **[Actions tab](https://github.com/AdityaChuhan-bot/Nova-ai/actions)**.
2. Select the latest successful workflow run.
3. Scroll to the **Artifacts** section at the bottom.
4. Download `jarvis-mini-debug-apk.zip`, extract it, and install `app-debug.apk` directly onto your Android device.

---

## 📱 DIY Smart Speaker Deployment

Repurposing an old phone or tablet into an ambient smart display:

1. **Mounting**: Use a desktop dock, tablet charging stand, or magnetic wall mount.
2. **Power**: Keep the device plugged into a standard 5V/2A or USB-C power supply.
3. **Screen Settings**: Enable **"Keep Screen On"** in Jarvis Mini's Settings screen.
4. **Dedicated Launcher (Optional)**: Set Jarvis Mini as the default assistive application in Android Settings (`Apps > Default apps > Digital assistant app`).

---

## 🤖 CI/CD & Automation

The repository includes a production-ready GitHub Actions workflow (`.github/workflows/build-apk.yml`):

- **Push & PR Triggers**: Automatically verifies code compilation on every push to `main`/`master`.
- **Headless SDK Setup**: Automated Android SDK 36 provisioning and license agreement.
- **Workflow Dispatch**: Supports manual triggers with selectable build targets (`debug`, `release`, or `both`).
- **Artifact Retention**: Automatically attaches downloadable APK packages to the workflow summary.

---

## 🤝 Contributing

Contributions are warmly welcomed! Please read our [Contributing Guide](CONTRIBUTING.md) and [Code of Conduct](SECURITY.md) before submitting pull requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 🔐 Security

If you discover a security vulnerability or credential leak, please review our [Security Policy](SECURITY.md) for reporting procedures.

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more details.
