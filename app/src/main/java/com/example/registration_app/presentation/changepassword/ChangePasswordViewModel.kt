package com.example.registration_app.presentation.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.repository.AuthRepository
import com.example.registration_app.domain.usecase.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun handleIntent(intent: ChangePasswordIntent) {
        when (intent) {
            is ChangePasswordIntent.UpdateNewPassword -> {
                _state.value = _state.value.copy(
                    newPassword = intent.password,
                    errorMessage = null
                )
            }

            is ChangePasswordIntent.UpdateConfirmPassword -> {
                _state.value = _state.value.copy(
                    confirmPassword = intent.confirmPassword,
                    errorMessage = null
                )
            }

            is ChangePasswordIntent.ChangePassword -> {
                changePassword()
            }

            is ChangePasswordIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun changePassword() {
        val currentState = _state.value

        if (currentState.newPassword.isBlank() || currentState.confirmPassword.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        if (currentState.newPassword != currentState.confirmPassword) {
            _state.value = currentState.copy(
                errorMessage = "Passwords do not match"
            )
            return
        }

        if (currentState.newPassword.length < 6) {
            _state.value = currentState.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        if (currentState.email.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Email not found"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isPasswordChanged = false
            )

            when (val result = changePasswordUseCase(currentState.email, currentState.newPassword)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        resetEmailSent = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isPasswordChanged = false,
                        resetEmailSent = false
                    )
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun resetSuccessState() {
        _state.value = _state.value.copy(isPasswordChanged = false)
    }
}
