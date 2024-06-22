package com.glazovnet.glazovnetapp.announcements.data.entity

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AnnouncementModelDto(
    val id: String,
    val addressFilters: List<List<String>>,
    val title: String,
    val text: String,
    val creationDate: Long
)
