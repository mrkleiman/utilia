package com.mariomora.utilia.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.AuthState
import com.mariomora.utilia.domain.model.User
import com.mariomora.utilia.domain.usecase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
data class AuthUiState(
    val authState: AuthState = AuthState.Unauthenticated,
    val isRegisterMode: Boolean = false,
    val emailInput: String = "",
    val passwordInput: String = "",
    val nameInput: String = "",
    val errorMessage: String? = null
)

class AuthViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val registerUseCase: RegisterUseCase,
    private val loginAsGuestDemoUseCase: LoginAsGuestDemoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val user = getCurrentUserUseCase()
        if (user != null) {
            _uiState.value = _uiState.value.copy(authState = AuthState.Authenticated(user))
        }
    }

    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(emailInput = email, errorMessage = null) }
    fun onPasswordChange(pass: String) { _uiState.value = _uiState.value.copy(passwordInput = pass, errorMessage = null) }
    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(nameInput = name, errorMessage = null) }
    fun toggleMode() { _uiState.value = _uiState.value.copy(isRegisterMode = !_uiState.value.isRegisterMode, errorMessage = null) }

    fun submit() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(authState = AuthState.Loading, errorMessage = null)
            val result = if (state.isRegisterMode) {
                registerUseCase(state.emailInput, state.passwordInput, state.nameInput)
            } else {
                loginWithEmailUseCase(state.emailInput, state.passwordInput)
            }
            handleResult(result)
        }
    }

    fun loginWithGoogleDemo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authState = AuthState.Loading, errorMessage = null)
            val result = loginWithGoogleUseCase("google_demo_token_utilia", "ciudadano.mexico@gmail.com", "Mario Mora (Google)")
            handleResult(result)
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authState = AuthState.Loading, errorMessage = null)
            val result = loginAsGuestDemoUseCase()
            handleResult(result)
        }
    }

    private fun handleResult(result: AppResult<User>) {
        when (result) {
            is AppResult.Success -> {
                _uiState.value = _uiState.value.copy(authState = AuthState.Authenticated(result.data), errorMessage = null)
            }
            is AppResult.Error -> {
                _uiState.value = _uiState.value.copy(authState = AuthState.Error(result.message), errorMessage = result.message)
            }
            AppResult.Loading -> {}
        }
    }
}
