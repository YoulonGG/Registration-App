package com.example.registration_app.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun handleIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.UpdateUsername -> {
                _state.value = _state.value.copy(
                    username = intent.username,
                    errorMessage = null
                )
            }

            is SignUpIntent.UpdateEmail -> {
                _state.value = _state.value.copy(
                    email = intent.email,
                    errorMessage = null
                )
            }

            is SignUpIntent.UpdatePassword -> {
                _state.value = _state.value.copy(
                    password = intent.password,
                    errorMessage = null
                )
            }

            is SignUpIntent.UpdateConfirmPassword -> {
                _state.value = _state.value.copy(
                    confirmPassword = intent.confirmPassword,
                    errorMessage = null
                )
            }

            is SignUpIntent.SignUp -> {
                signUp()
            }

            is SignUpIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun signUp() {
        val currentState = _state.value

        if (currentState.username.isBlank() || currentState.email.isBlank() || currentState.password.isBlank() || currentState.confirmPassword.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        if (currentState.username.length < 3) {
            _state.value = currentState.copy(
                errorMessage = "Username must be at least 3 characters"
            )
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            _state.value = currentState.copy(
                errorMessage = "Passwords do not match"
            )
            return
        }

        if (currentState.password.length < 6) {
            _state.value = currentState.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            when (val result = signUpUseCase(currentState.email, currentState.password, currentState.username)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isSuccess = false
                    )
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun resetSuccessState() {
        _state.value = _state.value.copy(isSuccess = false)
    }
}