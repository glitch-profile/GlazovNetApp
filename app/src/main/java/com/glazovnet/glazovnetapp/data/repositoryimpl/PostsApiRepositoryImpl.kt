package com.glazovnet.glazovnetapp.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.posts.PostModelDto
import com.glazovnet.glazovnetapp.data.entity.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.data.mappers.toPostModelDto
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import com.glazovnet.glazovnetapp.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Named

private const val PATH = "api/posts"

class PostsApiRepositoryImpl(
    @Named("RestClient") private val client: HttpClient
): PostsApiRepository {
    override suspend fun getAllPosts(token: String): Resource<List<PostModel>> {
        return try {
            val response: ApiResponseDto<List<PostModelDto>> = client.get("$PATH/") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toPostModelDto() },
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getPostsList(
        limit: Int?,
        startIndex: Int?,
        token: String
    ): Resource<List<PostModel>> {
        return try {
            val response: ApiResponseDto<List<PostModelDto>> = client.get("$PATH/list") {
                bearerAuth(token)
                parameter("limit", limit)
                parameter("start_index", startIndex)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toPostModelDto() },
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getPostById(postId: String, token: String): Resource<PostModel?> {
        return try {
            val response: ApiResponseDto<List<PostModelDto>> = client.get("$PATH/$postId"){
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.firstOrNull()?.toPostModelDto(),
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun addPost(postModel: PostModel, token: String): Resource<PostModel?> {
        return try {
            val response: ApiResponseDto<List<PostModelDto>> = client.post("$PATH/add") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(postModel.toPostModelDto())
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.firstOrNull()?.toPostModelDto(),
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun editPost(postModel: PostModel, token: String): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/edit") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(postModel.toPostModelDto())
            }.body()
            if (response.status) {
                Resource.Success(
                    data = Unit,
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun deletePostById(postId: String, token: String): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.delete("$PATH/delete") {
                bearerAuth(token)
                parameter("post_id", postId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = Unit,
                    message = response.message
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}