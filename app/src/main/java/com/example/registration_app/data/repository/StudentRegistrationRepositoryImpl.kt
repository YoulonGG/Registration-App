package com.example.registration_app.data.repository

import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.repository.StudentRegistrationRepository
import javax.inject.Inject

class StudentRegistrationRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : StudentRegistrationRepository {
    override suspend fun registerStudent(registration: StudentRegistration): AuthResult<Unit> {
        return firestoreDataSource.saveStudentRegistration(registration)
    }

    override suspend fun getStudentRegistrationByUserId(userId: String): AuthResult<StudentRegistration?> {
        return firestoreDataSource.getStudentRegistrationByUserId(userId)
    }

    override suspend fun updateStudentRegistration(userId: String, registration: StudentRegistration): AuthResult<Unit> {
        return firestoreDataSource.updateStudentRegistration(userId, registration)
    }
}
