package com.example.registration_app.presentation.home

import com.example.registration_app.domain.model.User

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = false
)
