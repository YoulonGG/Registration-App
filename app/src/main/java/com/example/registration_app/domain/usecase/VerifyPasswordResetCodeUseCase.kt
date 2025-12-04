package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository

class VerifyPasswordResetCodeUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(oobCode: String): AuthResult<String> {
        return authRepository.verifyPasswordResetCode(oobCode)
    }
}
