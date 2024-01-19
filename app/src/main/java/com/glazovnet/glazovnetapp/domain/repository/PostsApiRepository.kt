package com.glazovnet.glazovnetapp.domain.repository

import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import com.glazovnet.glazovnetapp.domain.utils.Resource

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