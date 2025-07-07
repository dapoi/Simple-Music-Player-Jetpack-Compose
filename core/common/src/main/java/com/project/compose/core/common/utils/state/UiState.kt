package com.project.compose.core.common.utils.state

sealed class UiState<out T> {
    data object StateInitial : UiState<Nothing>()
    data class StateSuccess<out T>(val data: T?) : UiState<T>()
    data class StateFailed(val throwable: Throwable) : UiState<Nothing>()
    data object StateLoading : UiState<Nothing>()
}