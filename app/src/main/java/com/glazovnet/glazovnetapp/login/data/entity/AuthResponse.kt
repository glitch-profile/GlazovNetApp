package com.glazovnet.glazovnetapp.login.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val isAdmin: Boolean
)
