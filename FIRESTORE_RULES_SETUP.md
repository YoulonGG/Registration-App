# Firestore Security Rules Setup

## Current Issue
You're seeing "permission denied" errors because Firestore has default security rules that deny all access.

## Solution

### Option 1: Update Rules in Firebase Console (Recommended for Quick Fix)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Firestore Database** → **Rules** tab
4. Replace the existing rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write access to all documents for development
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

5. Click **Publish**

### Option 2: Deploy Rules Using Firebase CLI

If you have Firebase CLI installed:

```bash
firebase deploy --only firestore:rules
```

## Important Security Note

⚠️ **WARNING**: The rules above allow **anyone** to read and write to your entire database. This is **ONLY** for development/testing purposes.

For production, you should implement proper security rules, for example:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own user document
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Student registrations - users can only read/write their own
    match /student_registrations/{registrationId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow update, delete: if request.auth != null && resource.data.userId == request.auth.uid;
    }
  }
}
```

## After Updating Rules

1. The rules take effect immediately after publishing
2. Restart your app if it's running
3. The "permission denied" errors should be resolved
