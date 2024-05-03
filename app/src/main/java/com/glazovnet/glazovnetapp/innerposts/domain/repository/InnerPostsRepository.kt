package com.glazovnet.glazovnetapp.innerposts.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.innerposts.domain.model.InnerPostModel

interface InnerPostsRepository {

    suspend fun getInnerPosts(
        token: String
    ): Resource<List<InnerPostModel>>

    suspend fun addInnerPost(
        token: String,
        title: String,
        text: String
    ): Resource<InnerPostModel?>
}