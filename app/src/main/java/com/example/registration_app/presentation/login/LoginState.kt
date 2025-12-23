package com.example.registration_app.presentation.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userType: com.example.registration_app.domain.model.UserType? = null
)
