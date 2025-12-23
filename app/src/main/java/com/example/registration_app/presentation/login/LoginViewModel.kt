package com.example.registration_app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.UserType
import com.example.registration_app.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateEmail -> {
                _state.value = _state.value.copy(
                    email = intent.email,
                    errorMessage = null
                )
            }

            is LoginIntent.UpdatePassword -> {
                _state.value = _state.value.copy(
                    password = intent.password,
                    errorMessage = null
                )
            }

            is LoginIntent.SignIn -> {
                signIn()
            }

            is LoginIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun signIn() {
        val currentState = _state.value
        
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter your email and password"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            when (val result = signInUseCase(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    val user = result.data
                    // Validate user has a userType
                    if (user.userType == null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Your account type is missing. Please contact support.",
                            isSuccess = false
                        )
                    } else {
                        // Login successful - store userType for navigation
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null,
                            userType = user.userType
                        )
                    }
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
        _state.value = _state.value.copy(isSuccess = false, userType = null)
    }
}