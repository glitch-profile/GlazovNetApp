package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.announcements.AnnouncementModelDto
import com.glazovnet.glazovnetapp.domain.models.announcements.AddressFilterElement
import com.glazovnet.glazovnetapp.domain.models.announcements.AnnouncementModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun AnnouncementModel.toAnnouncementModelDto(): AnnouncementModelDto {
    val announcementCreationDate = this.creationDate?.format(DateTimeFormatter.ISO_DATE_TIME) ?: ""
    val filtersList = this.addresses.map { element ->
        listOf(
            element.city.lowercase(),
            element.street.lowercase(),
            element.houseNumber
        )
    }
    return AnnouncementModelDto(
        id = this.id,
        addressFilters = filtersList,
        title = this.title,
        text = this.text,
        creationDate = announcementCreationDate
    )
}

fun AnnouncementModelDto.toAnnouncementModel(): AnnouncementModel {
    val announcementCreationDate = OffsetDateTime.parse(creationDate, DateTimeFormatter.ISO_DATE_TIME)
    val filtersList = this.addressFilters.mapNotNull { element ->
        try {
            AddressFilterElement(
                city = element[0],
                street = element[1],
                houseNumber = element[2]
            )
        } catch (e: Exception) {
            null
        }
    }
    return AnnouncementModel(
        id = this.id,
        addresses = filtersList,
        title = this.title,
        text = this.text,
        creationDate = announcementCreationDate
    )
}