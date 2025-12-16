package com.example.registration_app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // Check if user is authenticated
            val user = getCurrentUserUseCase()
            val isAuthenticated = user != null

            _state.value = SplashState(
                isLoading = false,
                isAuthenticated = isAuthenticated
            )
        }
    }

    fun completeOnboarding() {
        preferencesManager.setOnboardingCompleted(true)
    }
}

