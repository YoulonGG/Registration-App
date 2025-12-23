package com.example.registration_app.presentation.adminhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.model.UserType
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProfileState())
    val state: StateFlow<AdminProfileState> = _state.asStateFlow()

    fun loadAdminProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            val currentUser = getCurrentUserUseCase()
            if (currentUser == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "You are not logged in"
                )
                return@launch
            }

            // Verify user is ADMIN
            if (currentUser.userType != UserType.ADMIN) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Access denied. Admin access required."
                )
                return@launch
            }

            val userProfileResult = firestoreDataSource.getUserProfile(currentUser.uid)
            val userProfile: User? = when (userProfileResult) {
                is AuthResult.Success -> userProfileResult.data
                else -> null
            }

            // Load admin profile data
            val adminProfileResult = firestoreDataSource.getAdminProfile(currentUser.uid)
            val adminProfile = when (adminProfileResult) {
                is AuthResult.Success -> adminProfileResult.data
                else -> null
            }

            val dateOfBirth = adminProfile?.get("dateOfBirth") as? Long
            val calendar = if (dateOfBirth != null) {
                java.util.Calendar.getInstance().apply {
                    timeInMillis = dateOfBirth
                }
            } else null

            _state.value = _state.value.copy(
                editedFirstName = adminProfile?.get("firstName") as? String ?: "",
                editedLastName = adminProfile?.get("lastName") as? String ?: "",
                editedUsername = userProfile?.username ?: "",
                editedPhoneNumber = adminProfile?.get("phoneNumber") as? String ?: "",
                editedGender = adminProfile?.get("gender") as? String ?: "",
                dateOfBirth = dateOfBirth,
                dateOfBirthDay = calendar?.get(java.util.Calendar.DAY_OF_MONTH)?.toString() ?: "",
                dateOfBirthMonth = calendar?.let { (it.get(java.util.Calendar.MONTH) + 1).toString() } ?: "",
                dateOfBirthYear = calendar?.get(java.util.Calendar.YEAR)?.toString() ?: "",
                isLoading = false,
                errorMessage = null
            )
        }
    }

    fun enableEditMode() {
        _state.value = _state.value.copy(isEditMode = true)
    }

    fun cancelEditMode() {
        _state.value = _state.value.copy(isEditMode = false)
    }

    fun updateEditedUsername(value: String) {
        _state.value = _state.value.copy(editedUsername = value)
    }

    fun updateEditedFirstName(value: String) {
        _state.value = _state.value.copy(editedFirstName = value)
    }

    fun updateEditedLastName(value: String) {
        _state.value = _state.value.copy(editedLastName = value)
    }

    fun updateEditedPhoneNumber(value: String) {
        _state.value = _state.value.copy(editedPhoneNumber = value)
    }

    fun updateEditedGender(value: String) {
        _state.value = _state.value.copy(editedGender = value)
    }

    fun setDateOfBirth(timestamp: Long) {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        _state.value = _state.value.copy(
            dateOfBirth = timestamp,
            dateOfBirthDay = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString(),
            dateOfBirthMonth = (calendar.get(java.util.Calendar.MONTH) + 1).toString(),
            dateOfBirthYear = calendar.get(java.util.Calendar.YEAR).toString()
        )
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentUser = getCurrentUserUseCase()
            
            if (currentUser == null) {
                _state.value = currentState.copy(errorMessage = "You are not logged in")
                return@launch
            }

            // Verify user is ADMIN
            if (currentUser.userType != UserType.ADMIN) {
                _state.value = currentState.copy(errorMessage = "Access denied. Admin access required.")
                return@launch
            }

            _state.value = currentState.copy(isSaving = true, errorMessage = null)

            // Update username in user profile (preserves userType and email)
            val updatedUser = User(
                uid = currentUser.uid,
                email = currentUser.email, // Preserve existing email
                username = currentState.editedUsername.trim().takeIf { it.isNotEmpty() },
                userType = currentUser.userType // Preserve userType
            )
            
            val userUpdateResult = firestoreDataSource.updateUserProfile(updatedUser)
            
            // Save admin profile data
            val adminProfileData = hashMapOf<String, Any>(
                "firstName" to currentState.editedFirstName.trim(),
                "lastName" to currentState.editedLastName.trim(),
                "phoneNumber" to currentState.editedPhoneNumber.trim(),
                "gender" to currentState.editedGender.trim()
            )
            
            if (currentState.dateOfBirth != null) {
                adminProfileData["dateOfBirth"] = currentState.dateOfBirth!!
            }
            
            val adminProfileResult = firestoreDataSource.saveAdminProfile(currentUser.uid, adminProfileData)
            
            when {
                userUpdateResult is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Cannot save changes. ${userUpdateResult.message}"
                    )
                }
                adminProfileResult is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Cannot save changes. ${adminProfileResult.message}"
                    )
                }
                else -> {
                    _state.value = _state.value.copy(
                        isEditMode = false,
                        isSaving = false,
                        errorMessage = null
                    )
                    loadAdminProfile()
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
