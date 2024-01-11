package com.glazovnet.glazovnetapp.data.entity.auth

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AuthDataDto(
    val username: String,
    val password: String,
    val asAdmin: Boolean
)
