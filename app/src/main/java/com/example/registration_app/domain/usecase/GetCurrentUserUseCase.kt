package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
