# Firebase Setup Guide

This guide will help you configure Firebase for the Cashi payment app.

## Prerequisites

- A Google account
- Android Studio installed
- Cashi project opened in Android Studio

## Step 1: Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click **Add project** (or select an existing project)
3. Enter your project name (e.g., "Cashi")
4. Follow the setup wizard:
   - Enable Google Analytics (optional)
   - Accept terms and click **Create project**

## Step 2: Add Android App to Firebase

1. In the Firebase Console, click on the Android icon to add an Android app
2. Register your app with these details:
   - **Android package name**: `ke.kiura.cashi`
   - **App nickname** (optional): "Cashi Android"
   - **Debug signing certificate SHA-1** (optional for now, required for Auth later)
3. Click **Register app**

## Step 3: Download google-services.json

1. After registering the app, download the `google-services.json` file
2. Place this file in the following location:
   ```
   Cashi/
   └── composeApp/
       └── google-services.json  ← Place it here
   ```

**IMPORTANT**:
- The `google-services.json` file is already added to `.gitignore` to prevent committing sensitive credentials
- Each developer needs their own copy of this file
- Never commit this file to version control
