package com.glazovnet.glazovnetapp.core.presentation.states

data class ScreenState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val stringResourceId: Int? = null,
    val message: String? = null
)
