# News Feed Android App

A simple Android application that displays news articles using the NewsAPI. Built with Clean Architecture principles, following the [Now in Android](https://github.com/android/nowinandroid) reference architecture.

## Features

| List View                       | Grid View                       | Detail View                       |
|---------------------------------|---------------------------------|-----------------------------------|
| ![List View](docs/images/1.png) | ![Grid View](docs/images/2.png) | ![Detail View](docs/images/3.png) |


- 📰 Browse top news headlines
- 🔄 List/Grid view toggle
- 📱 Modern Material Design 3 UI
- ♾️ Infinite scrolling with pagination
- 🖼️ Image loading with caching
- 🔍 Detailed article view
- ⚡ Fast and responsive

## Architecture

This project follows **Clean Architecture** with **MVVM** pattern, organized into modular layers:

```
app/
├── core/
│   ├── data/          # Repository implementations, data sources
│   ├── domain/        # Use cases, business logic
│   ├── model/         # Domain models
│   ├── network/       # API service, DTOs
│   ├── ui/            # Shared UI components
│   └── designsystem/  # Theme, colors, typography
└── feature/
    ├── home/          # News list screen
    └── detail/        # Article detail screen
```

### Module Dependency Graph

```
app
 ├─> feature:home
 │    ├─> core:domain
 │    │    ├─> core:data
 │    │    │    ├─> core:network
 │    │    │    └─> core:model
 │    │    └─> core:model
 │    └─> core:ui
 └─> feature:detail
      ├─> core:model
      └─> core:ui
```

## Tech Stack

### Core
- **Kotlin** - Programming language
- **Coroutines & Flow** - Asynchronous programming
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Design system

### Architecture Components
- **ViewModel** - UI state management
- **Paging 3** - Infinite scrolling pagination
- **Navigation Compose** - Screen navigation

### Dependency Injection
- **Koin** - Lightweight DI framework

### Networking
- **Retrofit** - REST API client
- **Moshi** - JSON serialization
- **OkHttp** - HTTP client with interceptors

### Image Loading
- **Coil** - Image loading and caching

### Testing
- **JUnit 4** - Testing framework
- **Mockito & Mockito-Kotlin** - Mocking framework
- **Coroutines Test** - Testing coroutines
- **Arch Core Testing** - Testing LiveData/ViewModel

## Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- NewsAPI key (get it from [newsapi.org](https://newsapi.org))

### Configuration

1. Clone the repository
2. Add your NewsAPI key to `local.properties`:
```properties
API_KEY=your_api_key_here
```
3. Build and run

