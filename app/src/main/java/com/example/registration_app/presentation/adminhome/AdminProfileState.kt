package com.example.registration_app.presentation.adminhome

data class AdminProfileState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false,
    val editedFirstName: String = "",
    val editedLastName: String = "",
    val editedUsername: String = "",
    val editedPhoneNumber: String = "",
    val editedGender: String = "",
    val dateOfBirth: Long? = null,
    val dateOfBirthDay: String = "",
    val dateOfBirthMonth: String = "",
    val dateOfBirthYear: String = "",
    val isSaving: Boolean = false
)
