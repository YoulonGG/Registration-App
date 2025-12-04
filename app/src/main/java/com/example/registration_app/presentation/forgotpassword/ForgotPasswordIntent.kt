package com.example.registration_app.presentation.forgotpassword

sealed class ForgotPasswordIntent {
    data class UpdateEmail(val email: String) : ForgotPasswordIntent()
    object SendResetEmail : ForgotPasswordIntent()
    object ClearError : ForgotPasswordIntent()
}
