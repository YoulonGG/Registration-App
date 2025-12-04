package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository

class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, newPassword: String): AuthResult<Unit> {
        return authRepository.changePasswordAfterOtpVerification(email, newPassword)
    }
}
