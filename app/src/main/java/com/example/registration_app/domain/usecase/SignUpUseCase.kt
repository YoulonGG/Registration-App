package com.example.registration_app.domain.usecase

import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.model.UserType
import com.example.registration_app.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, username: String, userType: UserType): AuthResult<User> {
        return authRepository.signUpWithEmail(email, password, username, userType)
    }
}
