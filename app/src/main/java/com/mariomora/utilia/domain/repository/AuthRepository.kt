package com.mariomora.utilia.domain.repository

import com.mariomora.utilia.core.common.AppResult
import com.mariomora.utilia.domain.model.User
import kotlinx.coroutines.flow.StateFlow

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
interface AuthRepository {
    val currentUserFlow: StateFlow<User?>
    suspend fun loginWithEmail(email: String, passwordHash: String): AppResult<User>
    suspend fun register(email: String, passwordHash: String, displayName: String): AppResult<User>
    suspend fun loginWithGoogle(googleToken: String, email: String, displayName: String): AppResult<User>
    suspend fun loginAsGuestDemo(): AppResult<User>
    fun getCurrentUser(): User?
    suspend fun logout()
}
