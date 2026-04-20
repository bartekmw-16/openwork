# Tymer Studio - Android App

This directory contains the Android companion app for Tymer Studio.

## Quick Start

### Prerequisites
- Android Studio (latest version recommended)
- JDK 17 or higher
- Android SDK with API 26-34

### Build Instructions

1. **Open in Android Studio**:
   ```bash
   cd apps/android
   # Open this directory in Android Studio
   ```

2. **Sync Gradle**:
   - Android Studio will automatically prompt to sync Gradle
   - Or manually: File > Sync Project with Gradle Files

3. **Build the App**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Device**:
   ```bash
   ./gradlew installDebug
   ```

   Or use Android Studio's Run button (Shift+F10)

### Development

#### Running on Emulator
1. Create an Android Virtual Device (AVD) in Android Studio
2. Select API 26+ for the system image
3. Click Run > Run 'app'

#### Running on Physical Device
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect via USB or wireless debugging
4. Click Run > Run 'app'

### Desktop Setup Requirements

For the Android app to connect, your Tymer Studio desktop must:

1. **Broadcast mDNS service**:
   - Service type: `_tymerstudio._tcp`
   - Port: Same as OpenWork server (default 3000)

2. **Accept SSH connections** (optional but recommended):
   - Run SSH server on port 22
   - Accept public keys from mobile clients

3. **Expose OpenWork API**:
   - Ensure server is accessible on local network
   - Allow connections from local network IPs

### Architecture

```
app/
├── network/
│   ├── DesktopDiscovery.kt  # mDNS device discovery
│   ├── SSHClient.kt          # SSH tunnel management
│   └── ApiService.kt         # OpenWork API client
├── ui/
│   ├── screens/              # UI screens
│   └── theme/                # Material 3 theming
├── viewmodels/               # MVVM ViewModels
└── data/models/              # Data models
```

### Features

- ✅ Local network device discovery (mDNS)
- ✅ SSH authentication and tunneling
- ✅ Chat synchronization with desktop
- ✅ Material 3 UI with Jetpack Compose
- ⏳ Real-time SSE event handling (in progress)
- ⏳ Offline mode with caching (planned)

### Troubleshooting

**App can't find desktop**:
- Ensure both devices are on the same WiFi network
- Check that desktop is broadcasting mDNS service
- Try refreshing the device list

**Connection fails**:
- Verify SSH is enabled on desktop (if using SSH)
- Check firewall settings on desktop
- Ensure OpenWork server is running

**Build errors**:
- Run `./gradlew clean build`
- Invalidate caches: File > Invalidate Caches / Restart
- Check JDK version is 17 or higher

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

### Release Build

1. Generate keystore (first time only):
   ```bash
   keytool -genkey -v -keystore tymer-release-key.jks \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias tymer-key
   ```

2. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

3. Sign APK (if not auto-signed):
   ```bash
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore tymer-release-key.jks \
     app/build/outputs/apk/release/app-release-unsigned.apk \
     tymer-key
   ```

4. Zipalign:
   ```bash
   zipalign -v 4 app-release-unsigned.apk tymer-studio.apk
   ```

### Contributing

See main repository CONTRIBUTING.md for guidelines.

### License

Same as main Tymer Studio project.
