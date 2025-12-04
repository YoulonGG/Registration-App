package com.example.registration_app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // Minimum splash screen display time for better UX (2 seconds)
            val minDisplayTime = 2000L
            val startTime = System.currentTimeMillis()

            // Check if user is authenticated
            val user = getCurrentUserUseCase()
            val isAuthenticated = user != null

            // Calculate remaining time to meet minimum display time
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = minDisplayTime - elapsedTime

            if (remainingTime > 0) {
                delay(remainingTime)
            }

            _state.value = SplashState(
                isLoading = false,
                isAuthenticated = isAuthenticated
            )
        }
    }
}

