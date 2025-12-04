# Password Reset Setup Guide

## ✅ Implementation Complete: Option A - Firebase Reset Link

The app now uses Firebase's built-in password reset email flow instead of custom OTP.

---

## How It Works

### User Flow:
1. User clicks "Forgot Password" on Login screen
2. User enters email address
3. Firebase sends password reset email with link
4. User clicks link in email
5. App opens to Reset Password screen
6. User enters new password
7. Password is reset and user can login

---

## Firebase Console Configuration

### Step 1: Configure Authorized Domains

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Authentication** → **Settings** → **Authorized domains**
4. Add your domain if needed:
   - `your-project-id.firebaseapp.com` (already added by default)
   - Your custom domain (if using one)

### Step 2: Configure Email Templates (Optional)

1. Go to **Authentication** → **Templates**
2. Click on **Password reset**
3. Customize the email template if needed
4. The link format should be:
   ```
   https://YOUR-PROJECT-ID.firebaseapp.com/__/auth/action?mode=resetPassword&oobCode=CODE
   ```

### Step 3: Configure Deep Links

The Android app is configured to handle Firebase password reset links via deep links in `AndroidManifest.xml`.

The link format Firebase uses:
```
https://YOUR-PROJECT-ID.firebaseapp.com/__/auth/action?mode=resetPassword&oobCode=RESET_CODE
```

When user clicks this link:
- If app is installed → Opens app directly
- If app is not installed → Opens web page (can redirect to Play Store)

---

## Files Changed

### ✅ Simplified Files:
1. **ForgotPasswordScreen** - Now only sends reset email (no OTP)
2. **ForgotPasswordViewModel** - Removed OTP generation logic
3. **ForgotPasswordState** - Removed OTP-related fields

### ✅ New Files:
1. **ResetPasswordScreen** - Screen for password reset via deep link
2. **ResetPasswordViewModel** - Handles password reset logic
3. **DeepLinkHelper** - Extracts reset code from deep link

### ✅ Updated Files:
1. **NavGraph** - Removed OTP verification routes, added reset password route
2. **MainActivity** - Added deep link handling
3. **AndroidManifest.xml** - Added deep link intent filters

---

## Testing

### Test the Flow:

1. **Request Password Reset:**
   - Open app → Click "Forgot Password"
   - Enter valid email
   - Click "Send Reset Link"
   - Check email inbox

2. **Reset Password:**
   - Click the reset link in email
   - App should open to Reset Password screen
   - Enter new password
   - Click "Reset Password"
   - Should navigate to Login screen

3. **Login with New Password:**
   - Use new password to login
   - Should work successfully

---

## Deep Link URL Format

Firebase password reset links have this format:
```
https://YOUR-PROJECT-ID.firebaseapp.com/__/auth/action?mode=resetPassword&oobCode=ABC123XYZ
```

The app extracts:
- `mode=resetPassword` → Confirms it's a password reset
- `oobCode=ABC123XYZ` → The reset code to use

---

## Troubleshooting

### Issue: Reset link doesn't open app
- **Solution**: Check AndroidManifest.xml has correct intent filters
- Verify package name matches: `com.example.registration_app`

### Issue: Reset code invalid
- **Solution**: Reset codes expire after 1 hour
- Request a new reset email

### Issue: Email not received
- **Solution**: 
  - Check spam folder
  - Verify email is registered in Firebase
  - Check Firebase Console → Authentication → Users

---

## Next Steps

The implementation is complete! The app now uses Firebase's native password reset flow which is:
- ✅ More secure (Firebase handles verification)
- ✅ Simpler (no custom OTP code needed)
- ✅ Better UX (works across all platforms)

No additional setup needed if Firebase is properly configured!

