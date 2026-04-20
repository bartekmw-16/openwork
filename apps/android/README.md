# Tymer Studio Android App

## Overview
Kotlin-based Android companion app for Tymer Studio desktop application, enabling mobile access to your AI workspace.

## Features
- **Local Network Connection**: Automatically discover and connect to Tymer Studio desktop instances on the same network
- **SSH Authentication**: Secure communication using SSH keys
- **Chat Synchronization**: Real-time chat sync with desktop sessions
- **Mobile-Optimized UI**: Touch-friendly interface for on-the-go productivity

## Architecture

### Technology Stack
- **Language**: Kotlin
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **UI Framework**: Jetpack Compose
- **Networking**: OkHttp + Retrofit
- **SSH**: JSch or Apache MINA SSHD
- **Local Discovery**: mDNS/Bonjour (NSD API)

### Project Structure
```
apps/android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tymerstudio/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── ChatScreen.kt
│   │   │   │   │   │   ├── ConnectionScreen.kt
│   │   │   │   │   │   └── SettingsScreen.kt
│   │   │   │   │   └── components/
│   │   │   │   ├── network/
│   │   │   │   │   ├── DesktopDiscovery.kt
│   │   │   │   │   ├── SSHClient.kt
│   │   │   │   │   └── ApiService.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── models/
│   │   │   │   │   └── repository/
│   │   │   │   └── viewmodels/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Connection Flow

### 1. Desktop Discovery
```kotlin
// Use Network Service Discovery (NSD) API
val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
val serviceType = "_tymerstudio._tcp"

// Desktop app broadcasts on local network
// Android app discovers and lists available instances
```

### 2. SSH Authentication
```kotlin
// Generate or import SSH key pair
// Exchange public key with desktop
// Establish secure tunnel for all communication
```

### 3. API Communication
```kotlin
// Connect to desktop's OpenWork server via SSH tunnel
// Use same REST API as web client
// Subscribe to SSE events for real-time updates
```

## Build Configuration

### build.gradle.kts (app level)
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.tymerstudio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tymerstudio.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.11.202"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // SSH
    implementation("com.jcraft:jsch:0.1.55")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

## Implementation Steps

### Phase 1: Project Setup
1. Create Android project structure
2. Configure Gradle build files
3. Set up navigation and basic UI

### Phase 2: Network Discovery
1. Implement mDNS service discovery
2. Display list of available desktop instances
3. Handle connection selection

### Phase 3: SSH Connection
1. Generate/import SSH key pairs
2. Implement SSH client
3. Establish secure tunnel

### Phase 4: API Integration
1. Connect to OpenWork server API
2. Implement chat message sync
3. Handle real-time SSE events

### Phase 5: UI Implementation
1. Chat screen with message list
2. Connection management screen
3. Settings for SSH keys and preferences

## Desktop Requirements

The Tymer Studio desktop app needs to:

1. **Broadcast mDNS Service**:
```typescript
// Advertise on local network
const serviceName = "Tymer Studio Desktop";
const serviceType = "_tymerstudio._tcp";
const port = 3000; // OpenWork server port
```

2. **Accept SSH Connections**:
```typescript
// Enable SSH server mode
// Accept public keys from mobile clients
// Provide tunneled access to OpenWork server
```

3. **Expose Server API**:
```typescript
// Ensure OpenWork server API is accessible
// Return workspace/session data
// Stream SSE events for real-time updates
```

## Security Considerations

- SSH key pairs for authentication (no passwords)
- Certificate pinning for API calls
- Encrypted storage for credentials
- Network security config restricting cleartext traffic
- Permissions: INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE

## Testing Plan

- [ ] mDNS discovery finds desktop instances
- [ ] SSH authentication succeeds
- [ ] API calls return correct data
- [ ] Chat messages sync in real-time
- [ ] App handles network disconnections gracefully
- [ ] Works on different Android versions (26-34)

## Future Enhancements

- Push notifications for new messages
- Offline mode with local caching
- Voice input for prompts
- File sharing between mobile and desktop
- Multi-device session continuity
