package com.example.registration_app.domain.repository

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration

interface StudentRegistrationRepository {
    suspend fun registerStudent(registration: StudentRegistration): AuthResult<Unit>
}
