package com.example.registration_app.data.datasource

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val USERS_COLLECTION = "users"
    }

    suspend fun saveUserProfile(user: User): AuthResult<Unit> {
        return try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "username" to (user.username ?: "")
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userMap)
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to save user profile")
        }
    }

    suspend fun getUserProfile(uid: String): AuthResult<User?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                val user = User(
                    uid = document.getString("uid") ?: uid,
                    email = document.getString("email"),
                    username = document.getString("username")
                )
                AuthResult.Success(user)
            } else {
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to get user profile")
        }
    }
}
