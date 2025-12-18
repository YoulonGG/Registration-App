package com.example.registration_app.presentation.adminhome

import com.example.registration_app.domain.model.User

data class AdminHomeState(
    val user: User? = null,
    val isLoading: Boolean = false
)
