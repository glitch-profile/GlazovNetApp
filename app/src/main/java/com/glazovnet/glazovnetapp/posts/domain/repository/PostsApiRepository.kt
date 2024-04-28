package com.glazovnet.glazovnetapp.posts.domain.repository

import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
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
        token: String,
        employeeId: String,
        title: String,
        text: String,
        image: ImageModelDto?
    ): Resource<PostModel?>

    suspend fun editPost(
        token: String,
        employeeId: String,
        postId: String,
        title: String,
        text: String,
        image: ImageModelDto?
    ): Resource<Unit>

    suspend fun deletePostById(
        postId: String,
        token: String,
        employeeId: String
    ): Resource<Unit>

}