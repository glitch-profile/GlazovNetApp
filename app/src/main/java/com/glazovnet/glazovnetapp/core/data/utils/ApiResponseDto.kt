package com.glazovnet.glazovnetapp.core.data.utils

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ApiResponseDto<T> (
    val status: Boolean,
    val message: String,
    val data: T
)