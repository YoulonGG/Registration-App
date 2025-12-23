package com.example.registration_app.presentation.adminhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.usecase.GetStudentsByMajorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminStudentListViewModel @Inject constructor(
    private val getStudentsByMajorUseCase: GetStudentsByMajorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminStudentListState())
    val state: StateFlow<AdminStudentListState> = _state.asStateFlow()

    fun loadStudents(major: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            
            when (val result = getStudentsByMajorUseCase(major)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        students = result.data,
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
