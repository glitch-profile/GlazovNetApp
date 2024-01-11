package com.glazovnet.glazovnetapp.presentation

data class ScreenState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val stringResourceId: Int? = null,
    val message: String? = null
)
