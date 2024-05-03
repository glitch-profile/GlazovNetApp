package com.glazovnet.glazovnetapp.innerposts.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class InnerPostModelDto(
    val id: String,
    val title: String?,
    val text: String,
    val creationDate: Long
)