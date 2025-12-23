package com.example.registration_app.data.datasource

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.PaymentTransaction
import com.example.registration_app.domain.model.PaymentStatus
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
        private const val PAYMENT_TRANSACTIONS_COLLECTION = "payment_transactions"
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
            // Get current document to preserve existing fields
            val currentDoc = firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
            
            // Build update map - only update provided fields, preserve others
            val userMap = hashMapOf<String, Any>()
            
            // Update email if provided (preserve existing if null)
            if (currentDoc.exists()) {
                userMap["email"] = user.email ?: (currentDoc.getString("email") ?: "")
            } else {
                userMap["email"] = user.email ?: ""
            }
            
            // Update username if provided, otherwise preserve existing
            if (user.username != null && user.username.isNotEmpty()) {
                userMap["username"] = user.username
            } else if (currentDoc.exists()) {
                // Preserve existing username if not provided
                val existingUsername = currentDoc.getString("username")
                if (existingUsername != null && existingUsername.isNotEmpty()) {
                    userMap["username"] = existingUsername
                }
            }
            
            // CRITICAL: Always preserve userType from existing document - never overwrite
            val existingUserType = if (currentDoc.exists()) {
                currentDoc.getString("userType") ?: user.userType?.name ?: ""
            } else {
                user.userType?.name ?: ""
            }
            if (existingUserType.isNotEmpty()) {
                userMap["userType"] = existingUserType
            }
            
            // Preserve uid
            userMap["uid"] = user.uid
            
            if (currentDoc.exists()) {
                // Update existing document
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .update(userMap)
                    .await()
            } else {
                // Create new document if it doesn't exist
                firestore.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(userMap)
                    .await()
            }
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot update profile. Please try again.")
        }
    }

    suspend fun getAdminProfile(uid: String): AuthResult<Map<String, Any>?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                val data = document.data
                AuthResult.Success(data)
            } else {
                AuthResult.Success(null)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load admin profile. Please try again.")
        }
    }

    suspend fun saveAdminProfile(uid: String, profileData: Map<String, Any>): AuthResult<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(profileData)
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot save admin profile. Please try again.")
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
            
            // Ensure all fields have values (empty strings are fine for Firestore)
            val registrationMap = hashMapOf(
                "studentName" to (registration.studentName.ifEmpty { "Student" }),
                "email" to (registration.email.ifEmpty { "" }),
                "gender" to (registration.gender.ifEmpty { "" }),
                "phoneNumber" to (registration.phoneNumber.ifEmpty { "" }),
                "address" to (registration.address.ifEmpty { "" }),
                "dateOfBirthDay" to (registration.dateOfBirthDay.ifEmpty { "" }),
                "dateOfBirthMonth" to (registration.dateOfBirthMonth.ifEmpty { "" }),
                "dateOfBirthYear" to (registration.dateOfBirthYear.ifEmpty { "" }),
                "course" to (registration.course.ifEmpty { "" }),
                "major" to (registration.major.ifEmpty { "" }),
                "userId" to (registration.userId ?: userId),
                "registrationDate" to (registration.registrationDate.takeIf { it > 0 } ?: System.currentTimeMillis())
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

    suspend fun getStudentsByMajor(major: String): AuthResult<List<StudentRegistration>> {
        return try {
            val querySnapshot = firestore.collection(STUDENT_REGISTRATIONS_COLLECTION)
                .whereEqualTo("major", major)
                .get()
                .await()
            
            // Sort by registrationDate descending in memory
            val sortedDocuments = querySnapshot.documents.sortedByDescending { 
                it.getLong("registrationDate") ?: 0L 
            }
            
            val students = sortedDocuments.mapNotNull { document ->
                try {
                    StudentRegistration(
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
                } catch (e: Exception) {
                    null
                }
            }
            
            AuthResult.Success(students)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load students. Please try again.")
        }
    }

    suspend fun savePaymentTransaction(transaction: PaymentTransaction): AuthResult<String> {
        return try {
            val transactionMap = hashMapOf(
                "studentId" to transaction.studentId,
                "studentName" to transaction.studentName,
                "course" to transaction.course,
                "major" to transaction.major,
                "price" to transaction.price,
                "status" to transaction.status.name,
                "userId" to (transaction.userId ?: ""),
                "transactionDate" to transaction.transactionDate,
                "registrationId" to (transaction.registrationId ?: "")
            )
            
            val docRef = firestore.collection(PAYMENT_TRANSACTIONS_COLLECTION)
                .add(transactionMap)
                .await()
            
            AuthResult.Success(docRef.id)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot save payment transaction. Please try again.")
        }
    }

    suspend fun updatePaymentTransaction(transactionId: String, status: PaymentStatus, registrationId: String? = null): AuthResult<Unit> {
        return try {
            val updateMap = hashMapOf<String, Any>(
                "status" to status.name
            )
            if (registrationId != null) {
                updateMap["registrationId"] = registrationId
            }
            
            firestore.collection(PAYMENT_TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .update(updateMap)
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot update payment transaction. Please try again.")
        }
    }

    suspend fun getPaymentTransactionsByUserId(userId: String): AuthResult<List<PaymentTransaction>> {
        return try {
            if (userId.isBlank()) {
                return AuthResult.Error("Invalid user ID")
            }
            
            // Query without orderBy to avoid index requirement, then sort in memory
            val querySnapshot = firestore.collection(PAYMENT_TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val transactions = querySnapshot.documents.mapNotNull { document ->
                try {
                    PaymentTransaction(
                        id = document.id,
                        studentId = document.getString("studentId") ?: "",
                        studentName = document.getString("studentName") ?: "",
                        course = document.getString("course") ?: "",
                        major = document.getString("major") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        status = try {
                            PaymentStatus.valueOf(document.getString("status") ?: "PENDING")
                        } catch (e: Exception) {
                            PaymentStatus.PENDING
                        },
                        userId = document.getString("userId"),
                        transactionDate = document.getLong("transactionDate") ?: 0L,
                        registrationId = document.getString("registrationId")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            // Sort by transactionDate descending in memory
            val sortedTransactions = transactions.sortedByDescending { it.transactionDate }
            
            AuthResult.Success(sortedTransactions)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load payment transactions. Please try again.")
        }
    }

    suspend fun getAllPaymentTransactions(): AuthResult<List<PaymentTransaction>> {
        return try {
            // Get all documents without orderBy, then sort in memory
            val querySnapshot = firestore.collection(PAYMENT_TRANSACTIONS_COLLECTION)
                .get()
                .await()
            
            val transactions = querySnapshot.documents.mapNotNull { document ->
                try {
                    PaymentTransaction(
                        id = document.id,
                        studentId = document.getString("studentId") ?: "",
                        studentName = document.getString("studentName") ?: "",
                        course = document.getString("course") ?: "",
                        major = document.getString("major") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        status = try {
                            PaymentStatus.valueOf(document.getString("status") ?: "PENDING")
                        } catch (e: Exception) {
                            PaymentStatus.PENDING
                        },
                        userId = document.getString("userId"),
                        transactionDate = document.getLong("transactionDate") ?: 0L,
                        registrationId = document.getString("registrationId")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            // Sort by transactionDate descending in memory
            val sortedTransactions = transactions.sortedByDescending { it.transactionDate }
            
            AuthResult.Success(sortedTransactions)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot load payment transactions. Please try again.")
        }
    }

    suspend fun deletePaymentTransaction(transactionId: String): AuthResult<Unit> {
        return try {
            firestore.collection(PAYMENT_TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .delete()
                .await()
            
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Cannot delete payment transaction. Please try again.")
        }
    }
}
