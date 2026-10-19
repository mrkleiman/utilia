package com.mariomora.utilia.domain.usecase.auth

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.User
import com.mariomora.utilia.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class LoginWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String): AppResult<User> {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (email.isBlank() || !emailRegex.matches(email)) {
            return AppResult.Error(IllegalArgumentException("Correo electrónico no válido."))
        }
        if (pass.length < 6) {
            return AppResult.Error(IllegalArgumentException("La contraseña debe tener mínimo 6 caracteres."))
        }
        return repository.loginWithEmail(email, pass)
    }
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String, name: String): AppResult<User> {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (email.isBlank() || !emailRegex.matches(email)) {
            return AppResult.Error(IllegalArgumentException("Correo no válido."))
        }
        if (pass.length < 6) {
            return AppResult.Error(IllegalArgumentException("La contraseña debe tener mínimo 6 caracteres."))
        }
        if (name.isBlank()) {
            return AppResult.Error(IllegalArgumentException("El nombre es obligatorio."))
        }
        return repository.register(email, pass, name)
    }
}

class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(token: String, email: String, name: String): AppResult<User> =
        repository.loginWithGoogle(token, email, name)
}

class LoginAsGuestDemoUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AppResult<User> = repository.loginAsGuestDemo()
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): User? = repository.getCurrentUser()
    fun flow(): StateFlow<User?> = repository.currentUserFlow
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
