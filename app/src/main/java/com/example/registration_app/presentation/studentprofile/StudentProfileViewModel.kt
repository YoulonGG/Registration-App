package com.example.registration_app.presentation.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.model.User
import com.example.registration_app.domain.model.UserType
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

            // Verify user is STUDENT
            if (currentUser.userType != UserType.STUDENT) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Access denied. Student access required."
                )
                return@launch
            }

            val userProfileResult = firestoreDataSource.getUserProfile(currentUser.uid)
            val userProfile: User? = when (userProfileResult) {
                is AuthResult.Success -> userProfileResult.data
                else -> null
            }

            _state.value = _state.value.copy(
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
                    
                    // Convert date of birth strings to timestamp
                    val dateOfBirth = if (!registration?.dateOfBirthDay.isNullOrEmpty() && 
                        !registration?.dateOfBirthMonth.isNullOrEmpty() && 
                        !registration?.dateOfBirthYear.isNullOrEmpty()) {
                        try {
                            val calendar = java.util.Calendar.getInstance().apply {
                                set(
                                    registration.dateOfBirthYear.toInt(),
                                    registration.dateOfBirthMonth.toInt() - 1,
                                    registration.dateOfBirthDay.toInt()
                                )
                            }
                            calendar.timeInMillis
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                    
                    _state.value = _state.value.copy(
                        editedFirstName = nameParts.firstOrNull() ?: "",
                        editedLastName = nameParts.drop(1).joinToString(" ").takeIf { it.isNotEmpty() } ?: "",
                        editedPhoneNumber = registration?.phoneNumber ?: "",
                        editedGender = registration?.gender ?: "",
                        dateOfBirth = dateOfBirth,
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
        _state.value = _state.value.copy(isEditMode = true)
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

    fun updateEditedUsername(value: String) {
        _state.value = _state.value.copy(editedUsername = value)
    }

    fun setDateOfBirth(timestamp: Long) {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        _state.value = _state.value.copy(
            dateOfBirth = timestamp,
            editedDateOfBirthDay = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString(),
            editedDateOfBirthMonth = (calendar.get(java.util.Calendar.MONTH) + 1).toString(),
            editedDateOfBirthYear = calendar.get(java.util.Calendar.YEAR).toString()
        )
    }

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentUser = getCurrentUserUseCase()
            
            if (currentUser == null) {
                _state.value = currentState.copy(
                    isSaving = false,
                    errorMessage = "You are not logged in. Please sign in again."
                )
                return@launch
            }

            // Verify user is STUDENT
            if (currentUser.userType != UserType.STUDENT) {
                _state.value = currentState.copy(
                    isSaving = false,
                    errorMessage = "Access denied. Student access required."
                )
                return@launch
            }

            // Basic validation
            if (currentState.editedFirstName.trim().isEmpty() && currentState.editedLastName.trim().isEmpty()) {
                _state.value = currentState.copy(
                    isSaving = false,
                    errorMessage = "Please enter at least a first name or last name."
                )
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
            
            // Update or create student registration
            val registration = currentState.studentRegistration
            val fullName = "${currentState.editedFirstName.trim()} ${currentState.editedLastName.trim()}".trim()
            
            // Build registration - use existing registration data or create new
            val updatedRegistration = if (registration != null) {
                // Update existing registration - preserve all existing fields
                registration.copy(
                    studentName = if (fullName.isNotEmpty()) fullName else registration.studentName,
                    email = registration.email.takeIf { it.isNotEmpty() } ?: (currentUser.email ?: ""),
                    phoneNumber = if (currentState.editedPhoneNumber.trim().isNotEmpty()) currentState.editedPhoneNumber.trim() else registration.phoneNumber,
                    gender = if (currentState.editedGender.trim().isNotEmpty()) currentState.editedGender.trim() else registration.gender,
                    address = registration.address, // Preserve existing address
                    dateOfBirthDay = if (currentState.editedDateOfBirthDay.isNotEmpty()) currentState.editedDateOfBirthDay else registration.dateOfBirthDay,
                    dateOfBirthMonth = if (currentState.editedDateOfBirthMonth.isNotEmpty()) currentState.editedDateOfBirthMonth else registration.dateOfBirthMonth,
                    dateOfBirthYear = if (currentState.editedDateOfBirthYear.isNotEmpty()) currentState.editedDateOfBirthYear else registration.dateOfBirthYear,
                    course = registration.course, // Preserve existing course
                    major = registration.major, // Preserve existing major
                    userId = registration.userId ?: currentUser.uid,
                    registrationDate = registration.registrationDate // Preserve registration date
                )
            } else {
                // Create new registration with profile data
                StudentRegistration(
                    studentName = fullName.ifEmpty { "Student" },
                    email = currentUser.email ?: "",
                    gender = currentState.editedGender.trim().ifEmpty { "" },
                    phoneNumber = currentState.editedPhoneNumber.trim(),
                    address = "",
                    dateOfBirthDay = currentState.editedDateOfBirthDay.ifEmpty { "" },
                    dateOfBirthMonth = currentState.editedDateOfBirthMonth.ifEmpty { "" },
                    dateOfBirthYear = currentState.editedDateOfBirthYear.ifEmpty { "" },
                    course = "",
                    major = "",
                    userId = currentUser.uid,
                    registrationDate = System.currentTimeMillis()
                )
            }
            
            val registrationUpdateResult = studentRegistrationRepository.updateStudentRegistration(
                currentUser.uid,
                updatedRegistration
            )
            
            if (userUpdateResult is AuthResult.Error) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Failed to update username: ${userUpdateResult.message}"
                )
            } else if (registrationUpdateResult is AuthResult.Error) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Failed to update profile: ${registrationUpdateResult.message}"
                )
            } else if (userUpdateResult is AuthResult.Success && registrationUpdateResult is AuthResult.Success) {
                // Both updates succeeded
                _state.value = _state.value.copy(
                    isEditMode = false,
                    isSaving = false,
                    errorMessage = null
                )
                // Reload profile to show updated data
                loadStudentProfile()
            } else {
                // Unexpected state
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMessage = "Unexpected error occurred. Please try again."
                )
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
