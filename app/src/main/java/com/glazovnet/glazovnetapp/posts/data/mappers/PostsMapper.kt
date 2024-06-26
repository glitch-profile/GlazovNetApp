package com.glazovnet.glazovnetapp.posts.data.mappers

import com.glazovnet.glazovnetapp.posts.data.entity.PostModelDto
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
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