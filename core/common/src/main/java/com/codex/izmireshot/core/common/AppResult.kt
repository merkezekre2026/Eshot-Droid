package com.codex.izmireshot.core.common

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

data class AppError(
    val message: String,
    val cause: Throwable? = null,
    val recoverable: Boolean = true,
)

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Success<T>(val data: T, val isStale: Boolean = false) : LoadState<T>
    data class Empty(val message: String) : LoadState<Nothing>
    data class Error(val message: String, val staleData: Any? = null) : LoadState<Nothing>
}
