package com.example.registration_app.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.usecase.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.UpdateEmail -> {
                _state.value = _state.value.copy(
                    email = intent.email,
                    errorMessage = null
                )
            }

            is ForgotPasswordIntent.SendResetEmail -> {
                sendResetEmail()
            }

            is ForgotPasswordIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun sendResetEmail() {
        val currentState = _state.value

        if (currentState.email.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter your email address"
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter a valid email address"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isResetEmailSent = false
            )

            when (val result = sendPasswordResetEmailUseCase(currentState.email)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isResetEmailSent = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isResetEmailSent = false
                    )
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}
