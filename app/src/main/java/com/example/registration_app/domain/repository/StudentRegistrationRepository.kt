package com.example.registration_app.domain.repository

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration

interface StudentRegistrationRepository {
    suspend fun registerStudent(registration: StudentRegistration): AuthResult<Unit>
    suspend fun getStudentRegistrationByUserId(userId: String): AuthResult<StudentRegistration?>
    suspend fun updateStudentRegistration(userId: String, registration: StudentRegistration): AuthResult<Unit>
    suspend fun getStudentsByMajor(major: String): AuthResult<List<StudentRegistration>>
}
