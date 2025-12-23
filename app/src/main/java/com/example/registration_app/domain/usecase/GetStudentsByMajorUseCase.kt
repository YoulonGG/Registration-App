package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.repository.StudentRegistrationRepository

class GetStudentsByMajorUseCase(
    private val studentRegistrationRepository: StudentRegistrationRepository
) {
    suspend operator fun invoke(major: String): AuthResult<List<StudentRegistration>> {
        return studentRegistrationRepository.getStudentsByMajor(major)
    }
}
