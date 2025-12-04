package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.repository.AuthRepository

class GenerateOtpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): String {
        return authRepository.generateOtp(email)
    }
}
