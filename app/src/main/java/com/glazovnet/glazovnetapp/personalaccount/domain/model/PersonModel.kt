package com.glazovnet.glazovnetapp.personalaccount.domain.model

data class PersonModel(
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val login: String,
    val password: String,
    val profileAvatar: String?,
    val isNotificationsEnabled: Boolean
)
