package com.glazovnet.glazovnetapp.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.announcements.AnnouncementModelDto
import com.glazovnet.glazovnetapp.data.entity.utils.AddressModelDto
import com.glazovnet.glazovnetapp.data.entity.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.data.mappers.toAddressFilterElement
import com.glazovnet.glazovnetapp.data.mappers.toAnnouncementModel
import com.glazovnet.glazovnetapp.data.mappers.toAnnouncementModelDto
import com.glazovnet.glazovnetapp.domain.models.announcements.AddressFilterElement
import com.glazovnet.glazovnetapp.domain.models.announcements.AnnouncementModel
import com.glazovnet.glazovnetapp.domain.repository.AnnouncementsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Named

private const val ADDRESSES_PATH = "api/address-info"
private const val ANNOUNCEMENTS_PATH = "api/announcements"

class AnnouncementsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient
): AnnouncementsApiRepository {
    override suspend fun getAllAnnouncements(token: String): Resource<List<AnnouncementModel>> {
        return try {
            val response: ApiResponseDto<List<AnnouncementModelDto>> = client.get("$ANNOUNCEMENTS_PATH/") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toAnnouncementModel() }
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getAnnouncementsForClient(
        token: String,
        userId: String
    ): Resource<List<AnnouncementModel>> {
        return try {
            val response: ApiResponseDto<List<AnnouncementModelDto>> = client.get("$ANNOUNCEMENTS_PATH/for-client") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toAnnouncementModel() }
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun addAnnouncement(
        announcementModel: AnnouncementModel,
        token: String
    ): Resource<AnnouncementModel?> {
        return try {
            val response: ApiResponseDto<AnnouncementModelDto> = client.put("$ANNOUNCEMENTS_PATH/create") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(announcementModel.toAnnouncementModelDto())
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toAnnouncementModel()
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getCitiesWithName(
        cityName: String,
        token: String
    ): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.get("$ADDRESSES_PATH/cities-list") {
                parameter("city", cityName)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data
                )
            } else Resource.Error(R.string.api_response_server_error)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getStreetsWithName(
        cityName: String,
        streetName: String,
        token: String
    ): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.get("$ADDRESSES_PATH/streets-list") {
                parameter("city", cityName)
                parameter("street", streetName)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data
                )
            } else Resource.Error(R.string.api_response_server_error)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getAddresses(
        cityName: String,
        streetName: String,
        token: String
    ): Resource<List<AddressFilterElement>> {
        return try {
            val response: ApiResponseDto<List<AddressModelDto>> = client.get("$ADDRESSES_PATH/addresses") {
                parameter("city", cityName)
                parameter("street", streetName)
                bearerAuth(token)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toAddressFilterElement() }.flatten()
                )
            } else Resource.Error(R.string.api_response_server_error)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }
}