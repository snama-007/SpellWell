package com.wordwell.libwwmw.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Initial)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    protected fun setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }

    protected fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }
}