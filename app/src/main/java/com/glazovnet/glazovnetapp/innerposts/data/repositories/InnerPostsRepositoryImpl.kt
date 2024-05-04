package com.glazovnet.glazovnetapp.innerposts.data.repositories

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.innerposts.data.entity.AddInnerPostModelDto
import com.glazovnet.glazovnetapp.innerposts.data.entity.InnerPostModelDto
import com.glazovnet.glazovnetapp.innerposts.data.mappers.toInnerPostModel
import com.glazovnet.glazovnetapp.innerposts.domain.model.InnerPostModel
import com.glazovnet.glazovnetapp.innerposts.domain.repository.InnerPostsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/inner-posts"

class InnerPostsRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): InnerPostsRepository {

    override suspend fun getInnerPosts(token: String): Resource<List<InnerPostModel>> {
        return try {
            val response: ApiResponseDto<List<InnerPostModelDto>> = client.get(PATH) {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toInnerPostModel() }
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun addInnerPost(token: String, title: String, text: String): Resource<InnerPostModel?> {
        return  try {
            val postToAdd = AddInnerPostModelDto(
                title = title.ifEmpty { null },
                text = text
            )
            val response: ApiResponseDto<InnerPostModelDto?> = client.post("$PATH/create") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(postToAdd)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data!!.toInnerPostModel()
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}