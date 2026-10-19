package com.mariomora.utilia.core.common

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(val exception: Throwable, val message: String = exception.localizedMessage ?: "Error desconocido") : AppResult<Nothing>
    data object Loading : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (value: T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (message: String, exception: Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(message, exception)
    return this
}
