package com.example.registration_app.presentation.otpverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OtpVerificationState())
    val state: StateFlow<OtpVerificationState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun handleIntent(intent: OtpVerificationIntent) {
        when (intent) {
            is OtpVerificationIntent.UpdateOtp -> {
                _state.value = _state.value.copy(
                    otp = intent.otp,
                    errorMessage = null
                )
            }

            is OtpVerificationIntent.VerifyOtp -> {
                verifyOtp()
            }

            is OtpVerificationIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun verifyOtp() {
        val currentState = _state.value

        if (currentState.otp.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter the OTP code"
            )
            return
        }

        if (currentState.otp.length != 6) {
            _state.value = currentState.copy(
                errorMessage = "OTP must be 6 digits"
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
                isOtpVerified = false
            )

            when (val result = verifyOtpUseCase(currentState.email, currentState.otp)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isOtpVerified = false
                    )
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}
