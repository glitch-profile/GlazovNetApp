package com.glazovnet.glazovnetapp.posts.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.posts.data.entity.AddPostModelDto
import com.glazovnet.glazovnetapp.posts.data.entity.PostModelDto
import com.glazovnet.glazovnetapp.posts.data.mappers.toPostModelDto
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/posts"

class PostsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): PostsApiRepository {
    override suspend fun getAllPosts(token: String): Resource<List<PostModel>> {
        return try {
            val response: ApiResponseDto<List<PostModelDto>> = client.get(PATH) {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toPostModelDto() }
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
            val response: ApiResponseDto<PostModelDto?> = client.get("$PATH/$postId"){
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data!!.toPostModelDto()
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun addPost(
        token: String,
        employeeId: String,
        title: String,
        text: String,
        image: ImageModelDto?
    ): Resource<PostModel?> {
        return try {
            val postModel = AddPostModelDto(
                id = null,
                title = title,
                text = text,
                image = image
            )
            val response: ApiResponseDto<PostModelDto?> = client.post("$PATH/add") {
                bearerAuth(token)
                header("employee_id", employeeId)
                contentType(ContentType.Application.Json)
                setBody(postModel)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data!!.toPostModelDto()
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun editPost(
        token: String,
        employeeId: String,
        postId: String,
        title: String,
        text: String,
        image: ImageModelDto?
    ): Resource<Unit> {
        return try {
            val postModel = AddPostModelDto(
                id = postId,
                title = title,
                text = text,
                image = image
            )
            val response: ApiResponseDto<Unit> = client.put("$PATH/edit") {
                bearerAuth(token)
                header("employee_id", employeeId)
                contentType(ContentType.Application.Json)
                setBody(postModel)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = Unit
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun deletePostById(
        postId: String,
        token: String,
        employeeId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.delete("$PATH/delete") {
                bearerAuth(token)
                header("employee_id", employeeId)
                parameter("post_id", postId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = Unit
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}