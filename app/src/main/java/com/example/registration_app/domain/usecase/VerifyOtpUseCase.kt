package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository

class VerifyOtpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String): AuthResult<Unit> {
        return authRepository.verifyOtp(email, otp)
    }
}
