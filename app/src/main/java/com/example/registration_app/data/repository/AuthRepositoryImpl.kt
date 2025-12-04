package com.example.registration_app.data.repository

import com.example.registration_app.data.datasource.FirebaseAuthDataSource
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.data.datasource.OtpDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val otpDataSource: OtpDataSource
) : AuthRepository {
    override suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        val authResult = firebaseAuthDataSource.signInWithEmail(email, password)
        
        return when (authResult) {
            is AuthResult.Success -> {
                // Fetch username from Firestore if available
                val profileResult = firestoreDataSource.getUserProfile(authResult.data.uid)
                when (profileResult) {
                    is AuthResult.Success -> {
                        profileResult.data?.let { userWithUsername ->
                            AuthResult.Success(userWithUsername)
                        } ?: authResult
                    }
                    else -> authResult // Return auth user if Firestore fetch fails
                }
            }
            else -> authResult
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, username: String): AuthResult<User> {
        // First, create user in Firebase Authentication
        val authResult = firebaseAuthDataSource.signUpWithEmail(email, password)
        
        return when (authResult) {
            is AuthResult.Success -> {
                // User created successfully, now save username to Firestore
                val userWithUsername = authResult.data.copy(username = username)
                val saveResult = firestoreDataSource.saveUserProfile(userWithUsername)
                
                when (saveResult) {
                    is AuthResult.Success -> AuthResult.Success(userWithUsername)
                    is AuthResult.Error -> {
                        // User created but failed to save username - still return success
                        // as user can login. Username can be saved later
                        AuthResult.Success(userWithUsername)
                    }
                    is AuthResult.Loading -> AuthResult.Success(userWithUsername)
                }
            }
            is AuthResult.Error -> authResult
            is AuthResult.Loading -> authResult
        }
    }

    override suspend fun getCurrentUser(): User? {
        val authUser = firebaseAuthDataSource.getCurrentUser() ?: return null
        
        // Fetch username from Firestore if available
        val profileResult = firestoreDataSource.getUserProfile(authUser.uid)
        return when (profileResult) {
            is AuthResult.Success -> {
                profileResult.data ?: authUser // Return Firestore user with username, or auth user if not found
            }
            else -> authUser // Return auth user if Firestore fetch fails
        }
    }

    override suspend fun signOut() {
        firebaseAuthDataSource.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return firebaseAuthDataSource.sendPasswordResetEmail(email)
    }

    override suspend fun verifyPasswordResetCode(oobCode: String): AuthResult<String> {
        return firebaseAuthDataSource.verifyPasswordResetCode(oobCode)
    }

    override suspend fun confirmPasswordReset(oobCode: String, newPassword: String): AuthResult<Unit> {
        return firebaseAuthDataSource.confirmPasswordReset(oobCode, newPassword)
    }

    override suspend fun generateOtp(email: String): String {
        return otpDataSource.generateAndStoreOtp(email)
    }

    override suspend fun verifyOtp(email: String, otp: String): AuthResult<Unit> {
        return if (otpDataSource.verifyOtp(email, otp)) {
            AuthResult.Success(Unit)
        } else {
            AuthResult.Error("Invalid or expired OTP")
        }
    }

    override suspend fun changePasswordAfterOtpVerification(email: String, newPassword: String): AuthResult<Unit> {
        // Check if OTP was verified for this email
        if (!otpDataSource.isOtpVerified(email)) {
            return AuthResult.Error("OTP not verified. Please verify OTP first.")
        }
        
        // After OTP verification, we send password reset email
        // Firebase requires password reset via email link for security
        // Note: For a production app, you would implement a backend service
        // that uses Firebase Admin SDK to reset password directly after OTP verification
        val resetResult = firebaseAuthDataSource.sendPasswordResetEmail(email)
        
        if (resetResult is AuthResult.Success) {
            // Clear verification after use
            otpDataSource.clearVerification(email)
            // In production: Implement backend API that uses Firebase Admin SDK
            // to reset password directly: admin.auth().updateUser(uid, {password: newPassword})
            return AuthResult.Success(Unit)
        }
        
        return resetResult
    }
}
