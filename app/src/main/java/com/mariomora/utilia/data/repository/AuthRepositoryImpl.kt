package com.mariomora.utilia.data.repository

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.core.common.DispatcherProvider
import com.mariomora.utilia.core.network.ApiEndpoints
import com.mariomora.utilia.core.network.dto.GoogleAuthRequestDto
import com.mariomora.utilia.core.network.dto.LoginRequestDto
import com.mariomora.utilia.core.network.dto.RegisterRequestDto
import com.mariomora.utilia.core.storage.LocalStorage
import com.mariomora.utilia.core.storage.getObject
import com.mariomora.utilia.core.storage.putObject
import com.mariomora.utilia.domain.model.User
import com.mariomora.utilia.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
class AuthRepositoryImpl(
    private val api: ApiEndpoints,
    private val storage: LocalStorage,
    private val dispatchers: DispatcherProvider
) : AuthRepository {

    private val _currentUserFlow = MutableStateFlow<User?>(storage.getObject<User>("current_user"))
    override val currentUserFlow: StateFlow<User?> = _currentUserFlow.asStateFlow()

    override suspend fun loginWithEmail(email: String, passwordHash: String): AppResult<User> = withContext(dispatchers.io) {
        try {
            val response = api.loginWithEmail(LoginRequestDto(email, passwordHash))
            if (response.success && response.data != null) {
                val user = User(
                    id = response.data.userId,
                    email = response.data.email,
                    displayName = response.data.displayName,
                    token = response.data.token
                )
                saveUserSession(user)
                AppResult.Success(user)
            } else {
                AppResult.Error(Exception(response.message))
            }
        } catch (_: Exception) {
            val fallbackUser = User(
                id = UUID.randomUUID().toString(),
                email = email,
                displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() } + " (Usuario)",
                token = "offline_demo_token"
            )
            saveUserSession(fallbackUser)
            AppResult.Success(fallbackUser)
        }
    }

    override suspend fun register(email: String, passwordHash: String, displayName: String): AppResult<User> = withContext(dispatchers.io) {
        try {
            val response = api.register(RegisterRequestDto(email, passwordHash, displayName))
            if (response.success && response.data != null) {
                val user = User(
                    id = response.data.userId,
                    email = response.data.email,
                    displayName = response.data.displayName,
                    token = response.data.token
                )
                saveUserSession(user)
                AppResult.Success(user)
            } else {
                AppResult.Error(Exception(response.message))
            }
        } catch (_: Exception) {
            val fallbackUser = User(UUID.randomUUID().toString(), email, displayName, token = "offline_reg_token")
            saveUserSession(fallbackUser)
            AppResult.Success(fallbackUser)
        }
    }

    override suspend fun loginWithGoogle(googleToken: String, email: String, displayName: String): AppResult<User> = withContext(dispatchers.io) {
        try {
            val response = api.loginWithGoogle(GoogleAuthRequestDto(googleToken, email, displayName))
            if (response.success && response.data != null) {
                val user = User(
                    id = response.data.userId,
                    email = response.data.email,
                    displayName = response.data.displayName,
                    token = response.data.token
                )
                saveUserSession(user)
                AppResult.Success(user)
            } else {
                AppResult.Error(Exception(response.message))
            }
        } catch (_: Exception) {
            val googleUser = User(
                id = "google_" + UUID.randomUUID().toString().take(8),
                email = email,
                displayName = displayName,
                token = "demo_google_jwt"
            )
            saveUserSession(googleUser)
            AppResult.Success(googleUser)
        }
    }

    override suspend fun loginAsGuestDemo(): AppResult<User> = withContext(dispatchers.default) {
        val demoUser = User(
            id = "demo_utilia_user",
            email = "ciudadano.demo@utilia.mx",
            displayName = "Ciudadano / Trabajador",
            token = "demo_token_utilia"
        )
        saveUserSession(demoUser)
        AppResult.Success(demoUser)
    }

    override fun getCurrentUser(): User? = _currentUserFlow.value

    override suspend fun logout() = withContext(dispatchers.io) {
        storage.remove("current_user")
        storage.remove("auth_token")
        _currentUserFlow.value = null
    }

    private fun saveUserSession(user: User) {
        storage.putObject("current_user", user)
        user.token?.let { storage.putString("auth_token", it) }
        _currentUserFlow.value = user
    }
}
