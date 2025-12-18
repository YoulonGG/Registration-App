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
                AuthResult.Error("Cannot sign in. Please try again.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot sign in. Please check your email and password.")
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(firebaseUser.toDomainUser())
            } else {
                AuthResult.Error("Cannot create account. Please try again.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot create account. Please try again.")
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
            AuthResult.Error(e.message ?: "Cannot send reset email. Please try again.")
        }
    }

    suspend fun verifyPasswordResetCode(oobCode: String): AuthResult<String> {
        return try {
            val email = firebaseAuth.verifyPasswordResetCode(oobCode).await()
            AuthResult.Success(email)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Reset link is not valid or expired. Please request a new one.")
        }
    }

    suspend fun confirmPasswordReset(oobCode: String, newPassword: String): AuthResult<Unit> {
        return try {
            firebaseAuth.confirmPasswordReset(oobCode, newPassword).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot reset password. Please try again.")
        }
    }

    private fun FirebaseUser.toDomainUser(): User {
        return User(
            uid = this.uid,
            email = this.email
        )
    }
}
