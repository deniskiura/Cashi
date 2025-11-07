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

## Step 4: Enable Firestore Database

1. In the Firebase Console, navigate to **Build** → **Firestore Database**
2. Click **Create database**
3. Choose a location for your database (select the closest to your users)
4. Start in **test mode** for development:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.time < timestamp.date(2025, 3, 1);
       }
     }
   }
   ```
   **Note**: Test mode rules expire after the specified date. Update security rules for production.

## Step 5: Configure Security Rules (Production)

When ready for production, update Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Transactions collection
    match /transactions/{transactionId} {
      // Allow authenticated users to read their own transactions
      allow read: if request.auth != null;

      // Allow creating transactions
      allow create: if request.auth != null
                    && request.resource.data.amount > 0
                    && request.resource.data.currency in ['USD', 'EUR']
                    && request.resource.data.status == 'PENDING';

      // Allow updating transaction status (for completing/failing transactions)
      allow update: if request.auth != null
                    && request.resource.data.status in ['PENDING', 'COMPLETED', 'FAILED'];

      // Prevent deletion
      allow delete: if false;
    }
  }
}
```

## Step 6: Test the Integration

1. Sync your Gradle files
2. Build and run the app
3. Try sending a payment
4. Check the Firebase Console → Firestore Database to see if transactions are being saved

## Firestore Data Structure

The app stores transactions in the following format:

**Collection**: `transactions`

**Document Structure**:
```json
{
  "id": "unique-transaction-id",
  "recipient": "recipient@example.com",
  "amount": 10000,           // Amount in cents (e.g., $100.00)
  "currency": "USD",          // USD or EUR
  "timestamp": 1672531200000, // Unix timestamp in milliseconds
  "status": "COMPLETED"       // PENDING, COMPLETED, or FAILED
}
```

## Troubleshooting

### Build Error: "google-services.json is missing"
- Ensure you've placed the `google-services.json` file in `composeApp/` directory
- Clean and rebuild the project

### Permission Denied Error
- Check your Firestore security rules
- Ensure test mode is enabled during development
- For production, implement Firebase Authentication

### Network Error
- Check device/emulator internet connection
- Verify Firebase project is active
- Check Firestore is enabled in Firebase Console

## Optional: Enable Firebase Authentication

For production apps, you should add Firebase Authentication:

1. In Firebase Console, go to **Build** → **Authentication**
2. Click **Get started**
3. Enable desired sign-in methods (Email/Password, Google, etc.)
4. Update the app to include authentication before making transactions

## Next Steps

1. ✅ Firebase is now integrated with your app
2. Consider adding Firebase Authentication for user management
3. Set up Firebase Analytics to track app usage
4. Configure Firebase Crashlytics for crash reporting
5. Update security rules before deploying to production

## Support

For issues or questions about Firebase integration:
- [Firebase Documentation](https://firebase.google.com/docs/android/setup)
- [Firestore Documentation](https://firebase.google.com/docs/firestore)
