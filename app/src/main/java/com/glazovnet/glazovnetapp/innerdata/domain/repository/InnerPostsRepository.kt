package com.glazovnet.glazovnetapp.innerdata.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.innerdata.domain.model.InnerPostModel

interface InnerPostsRepository {

    suspend fun getInnerPosts(
        token: String
    ): Resource<List<InnerPostModel>>

}