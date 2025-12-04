package com.example.registration_app.data.datasource

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ActionCodeSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(firebaseUser.toDomainUser())
            } else {
                AuthResult.Error("Sign in failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(firebaseUser.toDomainUser())
            } else {
                AuthResult.Error("Sign up failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toDomainUser()
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            // Send password reset email with default Firebase handler
            // The email will contain a link that can open the app via deep link
            // Configure authorized domains in Firebase Console > Authentication > Settings > Authorized domains
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to send password reset email")
        }
    }

    suspend fun verifyPasswordResetCode(oobCode: String): AuthResult<String> {
        return try {
            val email = firebaseAuth.verifyPasswordResetCode(oobCode).await()
            AuthResult.Success(email)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid or expired reset code")
        }
    }

    suspend fun confirmPasswordReset(oobCode: String, newPassword: String): AuthResult<Unit> {
        return try {
            firebaseAuth.confirmPasswordReset(oobCode, newPassword).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to reset password")
        }
    }

    private fun FirebaseUser.toDomainUser(): User {
        return User(
            uid = this.uid,
            email = this.email
        )
    }
}
