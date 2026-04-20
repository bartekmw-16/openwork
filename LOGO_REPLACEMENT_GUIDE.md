# Tymer Studio Logo Replacement Guide

## Required Assets

To complete the rebranding, replace the following icon files in `apps/desktop/src-tauri/icons/` with Tymer Studio branded versions:

### Icon Sizes Needed

1. **macOS (.icns)**
   - `icon.icns` - macOS app icon bundle
   - `icon-dev.icns` - Development mode variant

2. **Windows (.ico)**
   - `icon.ico` - Windows app icon

3. **PNG variants**
   - `icon.png` - Base icon (512x512 recommended)
   - `32x32.png` - Small icon
   - `128x128.png` - Medium icon
   - `128x128@2x.png` - Retina display variant (256x256)

4. **Development variants** (in `dev/` subdirectory)
   - `dev/icon.png`
   - `dev/32x32.png`
   - `dev/128x128.png`
   - `dev/128x128@2x.png`

5. **Android variants** (if needed for future Android app)
   - `android/mipmap-anydpi-v26/ic_launcher.xml`
   - `android/values/ic_launcher_background.xml`

## Generation Steps

1. **Get source logo**: Use the Tymer Studio logo provided by the design team
2. **Generate .icns**: Use tools like `iconutil` (macOS) or online converters
3. **Generate .ico**: Use tools like ImageMagick or Icon Workshop
4. **Generate PNGs**: Export at required sizes with transparent backgrounds
5. **Replace files**: Copy generated files to `apps/desktop/src-tauri/icons/`
6. **Test**: Build the app and verify icons appear correctly on all platforms

## Icon Guidelines

- Use transparent backgrounds for all PNG files
- Ensure the logo is clearly visible at 32x32 size
- Maintain aspect ratio across all sizes
- Use proper alpha channels for smooth edges
- Development icons can have a visual indicator (e.g., different color, "DEV" badge)

## Tools

- macOS: `iconutil`, Xcode, or [https://cloudconvert.com/](https://cloudconvert.com/)
- Windows: Icon Workshop, ImageMagick, or online tools
- Multi-platform: [Tauri Icon Tool](https://tauri.app/v1/guides/features/icons/)

## Verification

After replacement, test the app icon appears correctly:
- In the macOS Dock and Applications folder
- In Windows Start Menu and taskbar
- In the app window title bar
- During installation/updates
