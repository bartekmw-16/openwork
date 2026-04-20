# Manual Logo Replacement Instructions for Tymer Studio

## Download the Logo

Since automated download failed, please manually download the logo:

**Logo URL**: https://lh3.googleusercontent.com/sitesv/AA5AbUDNt9NzpvOqTJXFUu_cW0AH46zq2jJ1GoP_EGH2kNhgrTxUT1jjWsj4IFqngTp01QKOzAC_RQdx9crANsnGF2ag0RoymbwHFSKC6hCJt6NEsK-Eok_muBTpH3sqODA-tyYNCDSpRnMK4g9JI7GnaAoRANm4nye9BJnFADb8SASao2yoK9b828zkT5Z7U4LCybYZnEr3aptElTT0Fng_ZGq5MeHmrCQVfPao=w1280

1. Open the URL in your browser
2. Right-click and "Save Image As..."
3. Save as `tymer-logo.png`

## Step 1: Generate Icon Assets

### Option A: Using Online Tools (Easiest)

1. **Visit Tauri Icon Generator**:
   - Go to: https://tauri.app/v1/guides/features/icons/
   - Or use: https://cloudconvert.com/

2. **Upload your logo** (`tymer-logo.png`)

3. **Generate icons for macOS**:
   - Select output format: `.icns`
   - Download and save as `icon.icns`

4. **Generate icons for Windows**:
   - Select output format: `.ico`
   - Download and save as `icon.ico`

5. **Generate PNG variants**:
   - Create these sizes: 32x32, 128x128, 256x256, 512x512
   - Use transparent background

### Option B: Using macOS Tools

If you're on a Mac:

```bash
# 1. Resize logo to 512x512 (use Preview or another tool)
# 2. Create iconset directory
mkdir tymer-icon.iconset

# 3. Create different sizes
sips -z 16 16     tymer-logo.png --out tymer-icon.iconset/icon_16x16.png
sips -z 32 32     tymer-logo.png --out tymer-icon.iconset/icon_16x16@2x.png
sips -z 32 32     tymer-logo.png --out tymer-icon.iconset/icon_32x32.png
sips -z 64 64     tymer-logo.png --out tymer-icon.iconset/icon_32x32@2x.png
sips -z 128 128   tymer-logo.png --out tymer-icon.iconset/icon_128x128.png
sips -z 256 256   tymer-logo.png --out tymer-icon.iconset/icon_128x128@2x.png
sips -z 256 256   tymer-logo.png --out tymer-icon.iconset/icon_256x256.png
sips -z 512 512   tymer-logo.png --out tymer-icon.iconset/icon_256x256@2x.png
sips -z 512 512   tymer-logo.png --out tymer-icon.iconset/icon_512x512.png
sips -z 1024 1024 tymer-logo.png --out tymer-icon.iconset/icon_512x512@2x.png

# 4. Generate .icns file
iconutil -c icns tymer-icon.iconset -o icon.icns
```

### Option C: Using ImageMagick (Cross-platform)

```bash
# Install ImageMagick first
# macOS: brew install imagemagick
# Linux: sudo apt-get install imagemagick
# Windows: Download from imagemagick.org

# Generate PNG sizes
convert tymer-logo.png -resize 32x32 icon-32.png
convert tymer-logo.png -resize 128x128 icon-128.png
convert tymer-logo.png -resize 256x256 icon-256.png
convert tymer-logo.png -resize 512x512 icon-512.png

# Generate .ico for Windows (requires multiple sizes)
convert icon-32.png icon-128.png icon-256.png icon.ico
```

## Step 2: Replace Icons in the Project

Navigate to your Tymer Studio project:

```bash
cd /path/to/openwork/apps/desktop/src-tauri/icons/
```

### Replace Production Icons

```bash
# macOS icon
cp /path/to/generated/icon.icns ./icon.icns

# Windows icon
cp /path/to/generated/icon.ico ./icon.ico

# Base PNG icon (512x512)
cp /path/to/generated/icon-512.png ./icon.png

# Smaller PNG variants
cp /path/to/generated/icon-32.png ./32x32.png
cp /path/to/generated/icon-128.png ./128x128.png
cp /path/to/generated/icon-256.png ./128x128@2x.png
```

### Replace Development Icons (Optional)

For development mode with a visual indicator:

```bash
cd dev/

# Copy same icons or create modified versions
cp /path/to/generated/icon-512.png ./icon.png
cp /path/to/generated/icon-32.png ./32x32.png
cp /path/to/generated/icon-128.png ./128x128.png
cp /path/to/generated/icon-256.png ./128x128@2x.png

# Optionally add a "DEV" badge to these icons using image editing software
```

## Step 3: Build and Test

### Build the macOS App

```bash
cd /path/to/openwork

# Install dependencies (if not done)
pnpm install

# Build the desktop app
pnpm --filter @tymerstudio/desktop build
```

This will create:
- macOS: `apps/desktop/src-tauri/target/release/bundle/macos/Tymer Studio.app`
- DMG installer: `apps/desktop/src-tauri/target/release/bundle/dmg/`

### Install on Your Mac

1. **Locate the built app**:
   ```bash
   open apps/desktop/src-tauri/target/release/bundle/macos/
   ```

2. **Drag to Applications folder**:
   - Drag `Tymer Studio.app` to `/Applications/`

3. **First launch**:
   - Right-click the app and select "Open"
   - Click "Open" in the security dialog
   - (This is needed for unsigned apps on macOS)

### Verify Icon Appearance

Check that the new Tymer Studio logo appears in:
- ✅ Dock when app is running
- ✅ Applications folder
- ✅ App Switcher (Cmd+Tab)
- ✅ Window title bar
- ✅ System Preferences > Security & Privacy

## Step 4: Create Release Build

For distribution to others:

```bash
# Create optimized release build
pnpm --filter @tymerstudio/desktop build

# The DMG will be at:
# apps/desktop/src-tauri/target/release/bundle/dmg/Tymer Studio_0.11.202_aarch64.dmg
# (or x64 depending on your Mac architecture)
```

## Troubleshooting

### Icon Not Updating
- Clear icon cache: `sudo find /private/var/folders/ -name com.apple.iconservices -exec rm -rf {} \;`
- Restart Finder: `killall Finder`
- Rebuild with: `pnpm --filter @tymerstudio/desktop build --force`

### Build Errors
- Clear build cache: `rm -rf apps/desktop/src-tauri/target/`
- Reinstall dependencies: `pnpm install --force`

### Permission Issues
- On first run, go to System Preferences > Security & Privacy
- Click "Open Anyway" for Tymer Studio

## Android App Icons

For the Android app, you'll need different icon sizes:

1. **Generate Android icons**:
   - Use Android Studio's Image Asset Studio
   - Or use online tools like https://romannurik.github.io/AndroidAssetStudio/

2. **Required sizes**:
   - mdpi: 48x48
   - hdpi: 72x72
   - xhdpi: 96x96
   - xxhdpi: 144x144
   - xxxhdpi: 192x192

3. **Place in Android project**:
   ```bash
   apps/android/app/src/main/res/
   ├── mipmap-mdpi/ic_launcher.png
   ├── mipmap-hdpi/ic_launcher.png
   ├── mipmap-xhdpi/ic_launcher.png
   ├── mipmap-xxhdpi/ic_launcher.png
   └── mipmap-xxxhdpi/ic_launcher.png
   ```

## Quick Reference

| Platform | Icon File | Location |
|----------|-----------|----------|
| macOS | `icon.icns` | `apps/desktop/src-tauri/icons/` |
| Windows | `icon.ico` | `apps/desktop/src-tauri/icons/` |
| Base PNG | `icon.png` (512x512) | `apps/desktop/src-tauri/icons/` |
| Small PNG | `32x32.png` | `apps/desktop/src-tauri/icons/` |
| Medium PNG | `128x128.png` | `apps/desktop/src-tauri/icons/` |
| Retina PNG | `128x128@2x.png` (256x256) | `apps/desktop/src-tauri/icons/` |

## Support

If you encounter issues:
1. Check the main LOGO_REPLACEMENT_GUIDE.md
2. Review Tauri documentation: https://tauri.app/v1/guides/features/icons/
3. Open an issue in the repository

---

**Note**: After replacing icons and building, the new Tymer Studio branding will be complete!
