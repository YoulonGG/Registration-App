package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository

class ConfirmPasswordResetUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(oobCode: String, newPassword: String): AuthResult<Unit> {
        return authRepository.confirmPasswordReset(oobCode, newPassword)
    }
}
