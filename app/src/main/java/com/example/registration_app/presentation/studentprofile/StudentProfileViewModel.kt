package com.example.registration_app.presentation.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.repository.StudentRegistrationRepository
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val studentRegistrationRepository: StudentRegistrationRepository,
    private val signOutUseCase: SignOutUseCase,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(StudentProfileState())
    val state: StateFlow<StudentProfileState> = _state.asStateFlow()

    fun loadStudentProfile() {
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

            val userProfileResult = firestoreDataSource.getUserProfile(currentUser.uid)
            val userProfile: User? = when (userProfileResult) {
                is AuthResult.Success -> userProfileResult.data
                else -> null
            }

            _state.value = _state.value.copy(
                editedEmail = userProfile?.email ?: currentUser.email ?: "",
                editedUsername = userProfile?.username ?: ""
            )

            when (val result = studentRegistrationRepository.getStudentRegistrationByUserId(currentUser.uid)) {
                is AuthResult.Success -> {
                    val registration = result.data
                    _state.value = _state.value.copy(
                        studentRegistration = registration,
                        isLoading = false,
                        errorMessage = null
                    )
                    val nameParts = registration?.studentName?.trim()?.split(" ") ?: emptyList()
                    _state.value = _state.value.copy(
                        editedFirstName = nameParts.firstOrNull() ?: "",
                        editedLastName = nameParts.drop(1).joinToString(" ").takeIf { it.isNotEmpty() } ?: "",
                        editedPhoneNumber = registration?.phoneNumber ?: "",
                        editedGender = registration?.gender ?: "",
                        editedDateOfBirthDay = registration?.dateOfBirthDay ?: "",
                        editedDateOfBirthMonth = registration?.dateOfBirthMonth ?: "",
                        editedDateOfBirthYear = registration?.dateOfBirthYear ?: ""
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    fun enableEditMode() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            val userProfileResult = firestoreDataSource.getUserProfile(currentUser?.uid ?: "")
            val userProfile: User? = when (userProfileResult) {
                is AuthResult.Success -> userProfileResult.data
                else -> null
            }
            
            _state.value = _state.value.copy(
                isEditMode = true,
                editedEmail = userProfile?.email ?: currentUser?.email ?: "",
                editedUsername = userProfile?.username ?: ""
            )
        }
    }

    fun cancelEditMode() {
        _state.value = _state.value.copy(isEditMode = false)
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

    fun updateEditedDateOfBirthDay(value: String) {
        _state.value = _state.value.copy(editedDateOfBirthDay = value)
    }

    fun updateEditedDateOfBirthMonth(value: String) {
        _state.value = _state.value.copy(editedDateOfBirthMonth = value)
    }

    fun updateEditedDateOfBirthYear(value: String) {
        _state.value = _state.value.copy(editedDateOfBirthYear = value)
    }

    fun updateEditedEmail(value: String) {
        _state.value = _state.value.copy(editedEmail = value)
    }

    fun updateEditedUsername(value: String) {
        _state.value = _state.value.copy(editedUsername = value)
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentUser = getCurrentUserUseCase()
            
            if (currentUser == null) {
                _state.value = currentState.copy(errorMessage = "You are not logged in")
                return@launch
            }

            _state.value = currentState.copy(isSaving = true, errorMessage = null)

            val updatedUser = User(
                uid = currentUser.uid,
                email = currentState.editedEmail.trim().takeIf { it.isNotEmpty() },
                username = currentState.editedUsername.trim().takeIf { it.isNotEmpty() }
            )
            
            val userUpdateResult = firestoreDataSource.updateUserProfile(updatedUser)
            when (userUpdateResult) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isEditMode = false,
                        isSaving = false,
                        errorMessage = null
                    )
                    loadStudentProfile()
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Cannot save changes. ${userUpdateResult.message}"
                    )
                }
                is AuthResult.Loading -> {}
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
