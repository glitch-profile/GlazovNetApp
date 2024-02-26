package com.glazovnet.glazovnetapp.data.entity.announcements

data class AnnouncementModelDto(
    val id: String,
    val addressFilters: List<List<String>>,
    val title: String,
    val text: String,
    val creationDate: String
)
