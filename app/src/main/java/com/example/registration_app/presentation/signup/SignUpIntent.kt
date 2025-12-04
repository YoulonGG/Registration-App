package com.example.registration_app.presentation.signup

sealed class SignUpIntent {
    data class UpdateUsername(val username: String) : SignUpIntent()
    data class UpdateEmail(val email: String) : SignUpIntent()
    data class UpdatePassword(val password: String) : SignUpIntent()
    data class UpdateConfirmPassword(val confirmPassword: String) : SignUpIntent()
    object SignUp : SignUpIntent()
    object ClearError : SignUpIntent()
}
