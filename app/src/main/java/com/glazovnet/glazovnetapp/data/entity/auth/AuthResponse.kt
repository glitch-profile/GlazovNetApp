package com.glazovnet.glazovnetapp.data.entity.auth

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val isAdmin: Boolean
)
