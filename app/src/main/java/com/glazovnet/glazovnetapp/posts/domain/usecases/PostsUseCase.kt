package com.glazovnet.glazovnetapp.posts.domain.usecases

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
import javax.inject.Inject

class PostsUseCase @Inject constructor(
    private val postsApiRepository: PostsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
) {
    suspend fun getAllPosts(): Resource<List<PostModel>> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return postsApiRepository.getAllPosts(token)
    }

    suspend fun addPost(postModel: PostModel): Resource<PostModel?> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return postsApiRepository.addPost(
            token = token,
            postModel = postModel
        )
    }

    suspend fun getPostById(postId: String): Resource<PostModel?> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return postsApiRepository.getPostById(postId, token)
    }

    suspend fun deletePostById(postId: String): Resource<Unit> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return postsApiRepository.deletePostById(
            token = token,
            postId = postId
        )
    }

    suspend fun updatePost(postModel: PostModel): Resource<Unit> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return postsApiRepository.editPost(
            token = token,
            postModel = postModel
        )
    }
}