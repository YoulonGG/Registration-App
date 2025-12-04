package com.example.registration_app.presentation.login

sealed class LoginIntent {
    data class UpdateEmail(val email: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object SignIn : LoginIntent()
    object ClearError : LoginIntent()
}
