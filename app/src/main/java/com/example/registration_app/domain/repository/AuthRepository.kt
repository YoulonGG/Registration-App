package com.example.registration_app.domain.repository

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User>
    suspend fun signUpWithEmail(email: String, password: String, username: String): AuthResult<User>
    suspend fun getCurrentUser(): User?
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>
    suspend fun verifyPasswordResetCode(oobCode: String): AuthResult<String>
    suspend fun confirmPasswordReset(oobCode: String, newPassword: String): AuthResult<Unit>
    suspend fun generateOtp(email: String): String
    suspend fun verifyOtp(email: String, otp: String): AuthResult<Unit>
    suspend fun changePasswordAfterOtpVerification(email: String, newPassword: String): AuthResult<Unit>
}
