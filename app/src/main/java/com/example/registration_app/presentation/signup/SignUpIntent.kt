package com.example.registration_app.presentation.signup

import com.example.registration_app.domain.model.UserType

sealed class SignUpIntent {
    data class UpdateUsername(val username: String) : SignUpIntent()
    data class UpdateEmail(val email: String) : SignUpIntent()
    data class UpdatePassword(val password: String) : SignUpIntent()
    data class UpdateConfirmPassword(val confirmPassword: String) : SignUpIntent()
    data class UpdateUserType(val userType: UserType) : SignUpIntent()
    object SignUp : SignUpIntent()
    object ClearError : SignUpIntent()
}
