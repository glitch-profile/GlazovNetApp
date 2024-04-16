package com.glazovnet.glazovnetapp.innerdata.data.repositories

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.innerdata.data.entity.InnerPostModelDto
import com.glazovnet.glazovnetapp.innerdata.data.mappers.toInnerPostModel
import com.glazovnet.glazovnetapp.innerdata.domain.model.InnerPostModel
import com.glazovnet.glazovnetapp.innerdata.domain.repository.InnerPostsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/inner/"

class InnerPostsRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): InnerPostsRepository {

    override suspend fun getInnerPosts(token: String): Resource<List<InnerPostModel>> {
        return try {
            val response: ApiResponseDto<List<InnerPostModelDto>> = client.get("$PATH/posts") {
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

}