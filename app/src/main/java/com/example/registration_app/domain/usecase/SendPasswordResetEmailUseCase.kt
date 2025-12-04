package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository

class SendPasswordResetEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): AuthResult<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}
