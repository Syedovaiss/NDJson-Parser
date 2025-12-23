# 📄 NDJSON Parser

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Material Design 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white)

**A modern Android application for parsing, filtering, and converting NDJSON (Newline Delimited JSON) files with a beautiful Material Design 3 UI.**

[Features](#-features) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Usage](#-usage)

</div>

---

## ✨ Features

### 🔍 Core Functionality
- **📂 File Selection**: Easy file picker to select NDJSON files from your device
- **🔎 Smart Filtering**: Filter JSON objects by key and value with case-insensitive matching
- **🔄 Format Conversion**: 
  - Convert NDJSON to standard JSON array format
  - Convert NDJSON to CSV format with proper escaping
- **💾 File Download**: Download converted files directly to your device's Downloads folder
- **📊 Data Visualization**: Beautiful card-based UI to view parsed JSON data

### 🎨 User Experience
- **Modern Material Design 3**: Beautiful, intuitive interface with dynamic colors
- **Real-time Filtering**: Instant results as you type
- **Error Handling**: Comprehensive error messages with line-by-line parsing feedback
- **Loading States**: Visual feedback during file operations
- **Accessibility**: Full support for content descriptions and screen readers

---

## 🏗️ Architecture

This project follows **Clean Architecture** principles with **MVVM** pattern and **SOLID** principles:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   UI/Compose │  │  ViewModel   │  │  UI State    │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  Use Cases   │  │  Repository  │  │   Models     │   │
│  │              │  │  Interface   │  │              │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                         │
│  ┌───────────────┐  ┌──────────────┐                    │
│  │  Repository   │  │  Data Source │                    │
│  │ Implementation│  │              │                    │
│  └───────────────┘  └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

### 📦 Layer Responsibilities

#### **Presentation Layer**
- **UI Components**: Reusable Compose components
- **ViewModel**: Manages UI state and business logic
- **UI State**: Immutable state objects

#### **Domain Layer**
- **Use Cases**: Single responsibility business logic operations
  - `ParseNDJsonUseCase`: Parse NDJSON files
  - `ConvertToJsonUseCase`: Convert to JSON format
  - `ConvertToCsvUseCase`: Convert to CSV format
  - `FilterByKeyUseCase`: Filter by key/value
  - `DownloadFileUseCase`: Download converted files
- **Repository Interface**: Abstraction for data operations
- **Models**: Domain entities

#### **Data Layer**
- **Repository Implementation**: Concrete implementation of repository
- **File Operations**: Reading and writing files

### 🎯 SOLID Principles

- ✅ **Single Responsibility**: Each use case has one clear purpose
- ✅ **Open/Closed**: Extensible through interfaces
- ✅ **Liskov Substitution**: Repository implementations are interchangeable
- ✅ **Interface Segregation**: Focused, minimal interfaces
- ✅ **Dependency Inversion**: Depend on abstractions, not concretions

---

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Async Operations**: Kotlin Coroutines & Flow
- **JSON Parsing**: Kotlinx Serialization

### Libraries & Dependencies
```kotlin
// Core Android
- androidx.core:core-ktx
- androidx.lifecycle:lifecycle-runtime-ktx
- androidx.activity:activity-compose

// Compose
- androidx.compose.ui
- androidx.compose.material3
- androidx.compose.ui.tooling

// Architecture
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.lifecycle:lifecycle-runtime-compose

// Coroutines
- kotlinx-coroutines-android

// Serialization
- kotlinx-serialization-json
```

### Build Tools
- **Gradle**: 8.13.2
- **Android Gradle Plugin**: 8.13.2
- **Kotlin**: 2.0.21

---

## 🚀 Installation

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK (API 24+)
- Gradle 8.13.2+

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/NDJsonParser.git
   cd NDJsonParser
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" or press `Shift + F10`

---

## 📖 Usage

### 1. Select a File
- Tap the **"Select NDJSON File"** button
- Choose an NDJSON file from your device
- The app will automatically parse the file

### 2. View Data
- Parsed JSON objects are displayed in beautiful cards
- Each card shows:
  - Item number
  - Number of fields
  - Formatted JSON content

### 3. Filter Data
- Enter a **key** name (e.g., "name", "id", "email")
- Optionally enter a **value** to filter by
- Tap **"Apply Filter"** to see filtered results
- Tap **"Clear"** to remove the filter

### 4. Convert & Download
- Tap **"JSON"** to download as JSON array format
- Tap **"CSV"** to download as CSV format
- Files are saved to `Downloads/NDJsonParser/`

### Example NDJSON File
```json
{"name":"John Doe","age":30,"city":"New York"}
{"name":"Jane Smith","age":25,"city":"Los Angeles"}
{"name":"Bob Johnson","age":35,"city":"Chicago"}
```

---

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/ovais/ndjsonparser/
│   │   │   ├── data/
│   │   │   │   └── repository/
│   │   │   │       └── NDJsonRepositoryImpl.kt
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── FileFormat.kt
│   │   │   │   │   ├── JsonObject.kt
│   │   │   │   │   └── ParseResult.kt
│   │   │   │   ├── repository/
│   │   │   │   │   └── NDJsonRepository.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── ConvertToCsvUseCase.kt
│   │   │   │       ├── ConvertToJsonUseCase.kt
│   │   │   │       ├── DownloadFileUseCase.kt
│   │   │   │       ├── FilterByKeyUseCase.kt
│   │   │   │       └── ParseNDJsonUseCase.kt
│   │   │   ├── presentation/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── DataDisplay.kt
│   │   │   │   │   │   ├── DownloadButtons.kt
│   │   │   │   │   │   ├── FilePickerButton.kt
│   │   │   │   │   │   └── FilterSection.kt
│   │   │   │   │   ├── screen/
│   │   │   │   │   │   └── NDJsonParserScreen.kt
│   │   │   │   │   └── state/
│   │   │   │   │       └── NDJsonUiState.kt
│   │   │   │   └── viewmodel/
│   │   │   │       └── NDJsonViewModel.kt
│   │   │   └── MainActivity.kt
│   │   └── res/
│   │       ├── values/
│   │       │   └── strings.xml
│   │       └── ...
│   └── test/
└── build.gradle.kts
```
---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m "Add some amazing feature"
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Syed Ovais Akhtar**

- GitHub: [@syedovaiss](https://github.com/syedovaiss)
- LinkedIn: [Syed Ovais Akhtar](https://linkedin.com/in/syedovaisakhtar)

---

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI framework
- [Material Design 3](https://m3.material.io/) for the design system
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON parsing

---

<div align="center">

**Made with ❤️ using Kotlin and Jetpack Compose**

⭐ Star this repo if you find it helpful!

</div>

