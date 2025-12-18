package com.example.registration_app.presentation.login

import com.example.registration_app.domain.model.UserType

data class LoginState(
    val email: String = "",
    val password: String = "",
    val expectedUserType: UserType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
