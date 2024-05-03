package com.glazovnet.glazovnetapp.innerposts.data.mappers

import com.glazovnet.glazovnetapp.innerposts.data.entity.InnerPostModelDto
import com.glazovnet.glazovnetapp.innerposts.domain.model.InnerPostModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun InnerPostModelDto.toInnerPostModel(): InnerPostModel {
    val postCreationTimestamp = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.creationDate),
        ZoneId.systemDefault()
    )
    return InnerPostModel(
        id = this.id,
        title = this.title,
        text = this.text,
        creationDate = postCreationTimestamp
    )
}