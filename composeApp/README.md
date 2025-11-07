# ComposeApp Module

This module contains the Android application UI and configuration.

## ⚠️ Required Setup

### Firebase Configuration

Before building this module, you need to add your Firebase configuration file:

1. **Follow the Firebase Setup Guide**: See `../FIREBASE_SETUP.md` in the project root
2. **Download your `google-services.json`** from Firebase Console
3. **Place it in this directory**: `composeApp/google-services.json`

The file should be placed at:
```
Cashi/
└── composeApp/
    └── google-services.json  ← Place here
```

### Why is this required?

The app uses Firebase Firestore as the backend for storing payment transactions. The `google-services.json` file contains your Firebase project credentials and configuration.

### Note for Developers

- The `google-services.json` file is listed in `.gitignore`
- Each developer needs their own copy
- An example file is provided: `google-services.json.example`
- Never commit your actual `google-services.json` to version control

## Build Error?

If you see an error like:
```
File google-services.json is missing. The Google Services Plugin cannot function without it.
```

This means you need to follow the steps above to add your Firebase configuration file.

## Alternative: Disable Firebase Temporarily

If you want to build without Firebase (for testing purposes), you can:

1. Comment out the Google Services plugin in `build.gradle.kts`:
   ```kotlin
   // alias(libs.plugins.googleServices)
   ```

2. Update `shared/src/androidMain/kotlin/ke/kiura/cashi/di/PlatformModule.android.kt` to use the mock API:
   ```kotlin
   single<RemoteApi> {
       RemoteApiImpl()  // Mock implementation
   }
   ```

However, note that the mock API returns random responses for testing purposes.
