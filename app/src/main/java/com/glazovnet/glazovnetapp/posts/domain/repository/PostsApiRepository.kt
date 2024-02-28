package com.glazovnet.glazovnetapp.posts.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel

interface PostsApiRepository {

    suspend fun getAllPosts(
        token: String
    ): Resource<List<PostModel>>

    suspend fun getPostsList (
        limit: Int? = null,
        startIndex: Int? = null,
        token: String
    ): Resource<List<PostModel>>

    suspend fun getPostById (
        postId: String,
        token: String
    ): Resource<PostModel?>

    suspend fun addPost(
        postModel: PostModel,
        token: String
    ): Resource<PostModel?>

    suspend fun editPost(
        postModel: PostModel,
        token: String
    ): Resource<Unit>

    suspend fun deletePostById(
        postId: String,
        token: String
    ): Resource<Unit>

}