package com.example.registration_app.domain.model

data class User(
    val uid: String,
    val email: String?,
    val username: String? = null,
    val userType: UserType? = null
)
