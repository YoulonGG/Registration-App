package com.example.registration_app.data.datasource

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.model.UserType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val STUDENT_REGISTRATIONS_COLLECTION = "student_registrations"
    }

    suspend fun saveUserProfile(user: User): AuthResult<Unit> {
        return try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "username" to (user.username ?: ""),
                "userType" to (user.userType?.name ?: "")
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userMap)
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot save profile. Please try again.")
        }
    }

    suspend fun getUserProfile(uid: String): AuthResult<User?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                val userTypeString = document.getString("userType")
                val userType = userTypeString?.let { 
                    try {
                        UserType.valueOf(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                val user = User(
                    uid = document.getString("uid") ?: uid,
                    email = document.getString("email"),
                    username = document.getString("username"),
                    userType = userType
                )
                AuthResult.Success(user)
            } else {
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load profile. Please try again.")
        }
    }

    suspend fun updateUserProfile(user: User): AuthResult<Unit> {
        return try {
            val userMap = hashMapOf(
                "email" to (user.email ?: ""),
                "username" to (user.username ?: ""),
                "userType" to (user.userType?.name ?: "")
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .update(userMap.toMap())
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot update profile. Please try again.")
        }
    }

    suspend fun saveStudentRegistration(registration: StudentRegistration): AuthResult<Unit> {
        return try {
            val registrationMap = hashMapOf(
                "studentName" to registration.studentName,
                "email" to registration.email,
                "gender" to registration.gender,
                "phoneNumber" to registration.phoneNumber,
                "address" to registration.address,
                "dateOfBirthDay" to registration.dateOfBirthDay,
                "dateOfBirthMonth" to registration.dateOfBirthMonth,
                "dateOfBirthYear" to registration.dateOfBirthYear,
                "course" to registration.course,
                "major" to registration.major,
                "userId" to (registration.userId ?: ""),
                "registrationDate" to registration.registrationDate
            )
            
            firestore.collection(STUDENT_REGISTRATIONS_COLLECTION)
                .add(registrationMap)
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot save registration. Please try again.")
        }
    }

    suspend fun getStudentRegistrationByUserId(userId: String): AuthResult<StudentRegistration?> {
        return try {
            val querySnapshot = firestore.collection(STUDENT_REGISTRATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()
            
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val registration = StudentRegistration(
                    studentName = document.getString("studentName") ?: "",
                    email = document.getString("email") ?: "",
                    gender = document.getString("gender") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    address = document.getString("address") ?: "",
                    dateOfBirthDay = document.getString("dateOfBirthDay") ?: "",
                    dateOfBirthMonth = document.getString("dateOfBirthMonth") ?: "",
                    dateOfBirthYear = document.getString("dateOfBirthYear") ?: "",
                    course = document.getString("course") ?: "",
                    major = document.getString("major") ?: "",
                    userId = document.getString("userId"),
                    registrationDate = document.getLong("registrationDate") ?: 0L
                )
                AuthResult.Success(registration)
            } else {
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load registration. Please try again.")
        }
    }

    suspend fun updateStudentRegistration(userId: String, registration: StudentRegistration): AuthResult<Unit> {
        return try {
            val querySnapshot = firestore.collection(STUDENT_REGISTRATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()
            
            val registrationMap = hashMapOf(
                "studentName" to registration.studentName,
                "email" to registration.email,
                "gender" to registration.gender,
                "phoneNumber" to registration.phoneNumber,
                "address" to registration.address,
                "dateOfBirthDay" to registration.dateOfBirthDay,
                "dateOfBirthMonth" to registration.dateOfBirthMonth,
                "dateOfBirthYear" to registration.dateOfBirthYear,
                "course" to registration.course,
                "major" to registration.major,
                "userId" to (registration.userId ?: userId),
                "registrationDate" to registration.registrationDate
            )
            
            if (querySnapshot.isEmpty) {
                // Create new registration if it doesn't exist (upsert behavior)
                firestore.collection(STUDENT_REGISTRATIONS_COLLECTION)
                    .add(registrationMap)
                    .await()
            } else {
                // Update existing registration
                val document = querySnapshot.documents[0]
                document.reference.update(registrationMap.toMap()).await()
            }
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot update registration. Please try again.")
        }
    }
}
