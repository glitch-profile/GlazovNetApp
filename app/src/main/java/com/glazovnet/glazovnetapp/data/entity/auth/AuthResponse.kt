package com.example.glazovnetadminapp.entity.authDto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val isAdmin: Boolean
)
