# 📱 Vonage Video Android App

<div align="center">
  
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.06.01-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![API Level](https://img.shields.io/badge/API-24%2B-orange.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](LICENSE)

*A modern Android reference application showcasing Vonage Video API integration with Jetpack Compose*

</div>

---

## ✨ Features

- 🎥 **Video Calling** - Integrated Vonage Video API for real-time video communication
- 🎨 **Modern UI** - Built with Jetpack Compose and Material Design 3
- 🌙 **Dark/Light Theme** - Adaptive theming with dynamic colors (Android 12+)
- 🏗️ **Clean Architecture** - Modular design with separate compose library
- 🧪 **Testing Ready** - Comprehensive test setup with JUnit and Espresso
- 📱 **Edge-to-Edge** - Modern Android UI with edge-to-edge display support

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 2.2.0
- **UI Framework**: Jetpack Compose (BOM 2025.06.01)
- **Architecture**: Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36

### Key Dependencies
- `androidx.activity:activity-compose` - Compose integration
- `androidx.compose.material3:material3` - Material Design 3 components
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle-aware components
- `androidx.core:core-ktx` - Android KTX extensions

### Development Tools
- **Code Quality**: Detekt static analysis
- **Testing**: JUnit, Espresso, Compose UI testing
- **Build Tools**: AGP 8.11.0

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 11 or higher
- Android SDK 36
- Gradle 8.11.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/vonage-video-android-app.git
   cd vonage-video-android-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   ```bash
   ./gradlew installDebug
   ```

### Configuration

To integrate your Vonage Video API credentials:

1. Create a `local.properties` file in the root directory
2. Add your Vonage API credentials:
   ```properties
   vonage.api.key=your_api_key_here
   vonage.session.id=your_session_id_here
   vonage.token=your_token_here
   ```

## 📁 Project Structure

```
vonage-video-android-app/
├── app/                          # Main application module
│   ├── src/main/java/com/vonage/android/
│   │   └── MainActivity.kt       # Main entry point
│   └── build.gradle.kts         # App-level build configuration
├── compose/                      # Reusable Compose components
│   ├── src/main/java/com/vonage/android/compose/
│   │   ├── components/          # Custom Compose components
│   │   └── theme/              # Material Design 3 theming
│   └── build.gradle.kts        # Compose module configuration
├── kotlin/                      # Shared Kotlin utilities
├── build-tools/                 # Code quality tools (Detekt)
├── gradle/                      # Gradle version catalog
└── scripts/                     # Build and deployment scripts
```

## 🎨 UI Components

The app features a modular Compose design system:

- **Theme System**: Dynamic Material Design 3 theming
- **Color Schemes**: Light/Dark mode with Android 12+ dynamic colors
- **Typography**: Custom typography scales
- **Components**: Reusable UI components in the `compose` module

## 🧪 Testing

Run the test suite:

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# Code quality checks
./gradlew detekt
```

## 📱 Supported Devices

- **Minimum**: Android 7.0 (API level 24)
- **Target**: Android 14+ (API level 36)
- **Architecture**: ARM64, x86_64
- **Screen sizes**: Phone, Tablet, Foldable

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- [Vonage Video API Documentation](https://tokbox.com/developer/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Android Development](https://developer.android.com/)

## 📞 Support

For support and questions:

- 📧 Email: [support@vonage.com](mailto:support@vonage.com)
- 📖 Documentation: [Vonage Developer Portal](https://developer.vonage.com/)
- 💬 Community: [Vonage Developer Community](https://developer.vonage.com/community)

---

<div align="center">
  <strong>Made with ❤️ using Vonage Video API and Jetpack Compose</strong>
</div>
