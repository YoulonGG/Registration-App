package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.repository.StudentRegistrationRepository

class RegisterStudentUseCase(
    private val studentRegistrationRepository: StudentRegistrationRepository
) {
    suspend operator fun invoke(registration: StudentRegistration): AuthResult<Unit> {
        return studentRegistrationRepository.registerStudent(registration)
    }
}
