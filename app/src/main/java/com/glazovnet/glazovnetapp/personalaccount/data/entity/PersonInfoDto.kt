package com.glazovnet.glazovnetapp.personalaccount.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class PersonInfoDto(
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val login: String,
    val password: String,
    val profileAvatar: String,
    val isNotificationsEnabled: Boolean
)