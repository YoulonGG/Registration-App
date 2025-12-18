package com.example.registration_app.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.usecase.ConfirmPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    private var resetCode: String? = null

    fun setResetCode(code: String) {
        resetCode = code
    }

    fun handleIntent(intent: ResetPasswordIntent) {
        when (intent) {
            is ResetPasswordIntent.UpdateNewPassword -> {
                _state.value = _state.value.copy(
                    newPassword = intent.password,
                    errorMessage = null
                )
            }

            is ResetPasswordIntent.UpdateConfirmPassword -> {
                _state.value = _state.value.copy(
                    confirmPassword = intent.confirmPassword,
                    errorMessage = null
                )
            }

            is ResetPasswordIntent.ResetPassword -> {
                resetPassword()
            }

            is ResetPasswordIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun resetPassword() {
        val currentState = _state.value
        val code = resetCode

        if (code == null) {
            _state.value = currentState.copy(
                errorMessage = "Reset link is not valid. Please request a new one."
            )
            return
        }

        if (currentState.newPassword.isBlank() || currentState.confirmPassword.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter your new password"
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
                errorMessage = "Password must be at least 6 letters"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isPasswordReset = false
            )

            when (val result = confirmPasswordResetUseCase(code, currentState.newPassword)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isPasswordReset = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isPasswordReset = false
                    )
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun resetSuccessState() {
        _state.value = _state.value.copy(isPasswordReset = false)
    }
}
