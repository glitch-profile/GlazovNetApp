package com.glazovnet.glazovnetapp.data.mappers

import com.glazovnet.glazovnetapp.data.entity.posts.PostModelDto
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun PostModelDto.toPostModel(): PostModel {
    val postCreationTimestamp = OffsetDateTime.ofInstant(
        Instant.ofEpochSecond(this.creationDate),
        ZoneId.systemDefault()
    )
    return PostModel(
        id = this.id,
        title = this.title,
        creationDateTime = postCreationTimestamp,
        text = this.text,
        image = this.image
    )
}

fun PostModel.toPostModel(): PostModelDto {
    val postCreationDate = this.creationDateTime?.toEpochSecond() ?: 0L
    return PostModelDto(
        id = this.id,
        title = this.title,
        creationDate = postCreationDate,
        text = this.text,
        image = this.image
    )
}