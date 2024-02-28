package com.glazovnet.glazovnetapp.login.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AuthDataDto(
    val username: String,
    val password: String,
    val asAdmin: Boolean
)
