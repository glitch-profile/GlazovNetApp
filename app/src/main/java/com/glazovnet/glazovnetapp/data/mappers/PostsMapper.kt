package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.posts.PostModelDto
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun PostModelDto.toPostModelDto(): PostModel {
    val postCreationTimestamp = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.creationDate),
        ZoneId.systemDefault()
    )
    val lastEditTimestamp = if (this.lastEditDate != null) {
        OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(this.lastEditDate),
            ZoneId.systemDefault()
        )
    } else null
    return PostModel(
        id = this.id,
        title = this.title,
        creationDateTime = postCreationTimestamp,
        lastEditDate = lastEditTimestamp,
        text = this.text,
        image = this.image
    )
}

fun PostModel.toPostModelDto(): PostModelDto {
    val postCreationDate = this.creationDateTime?.toEpochSecond() ?: 0L
    return PostModelDto(
        id = this.id,
        title = this.title,
        creationDate = postCreationDate,
        lastEditDate = null,
        text = this.text,
        image = this.image
    )
}