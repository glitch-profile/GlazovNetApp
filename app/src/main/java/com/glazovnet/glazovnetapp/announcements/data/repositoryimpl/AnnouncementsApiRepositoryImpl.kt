package com.glazovnet.glazovnetapp.announcements.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.announcements.data.entity.AddressModelDto
import com.glazovnet.glazovnetapp.announcements.data.entity.AnnouncementModelDto
import com.glazovnet.glazovnetapp.announcements.data.mapper.toAddressFilterElement
import com.glazovnet.glazovnetapp.announcements.data.mapper.toAnnouncementModel
import com.glazovnet.glazovnetapp.announcements.data.mapper.toAnnouncementModelDto
import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement
import com.glazovnet.glazovnetapp.announcements.domain.model.AnnouncementModel
import com.glazovnet.glazovnetapp.announcements.domain.repository.AnnouncementsApiRepository
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
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
    override suspend fun getAllAnnouncements(
        token: String,
        employeeId: String
    ): Resource<List<AnnouncementModel>> {
        return try {
            val response: ApiResponseDto<List<AnnouncementModelDto>> = client.get(ANNOUNCEMENTS_PATH) {
                bearerAuth(token)
                header("employee_id", employeeId)
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
        clientId: String
    ): Resource<List<AnnouncementModel>> {
        return try {
            val response: ApiResponseDto<List<AnnouncementModelDto>> = client.get("$ANNOUNCEMENTS_PATH/for-client") {
                bearerAuth(token)
                header("client_id", clientId)
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
        token: String,
        employeeId: String,
        announcementModel: AnnouncementModel
    ): Resource<AnnouncementModel?> {
        return try {
            val response: ApiResponseDto<AnnouncementModelDto?> = client.post("$ANNOUNCEMENTS_PATH/create") {
                bearerAuth(token)
                header("employee_id", employeeId)
                contentType(ContentType.Application.Json)
                setBody(announcementModel.toAnnouncementModelDto())
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data!!.toAnnouncementModel()
                )
            } else Resource.Error(R.string.api_response_server_error, response.message)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getCitiesWithName(
        token: String,
        employeeId: String,
        cityName: String
    ): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.get("$ADDRESSES_PATH/cities-list") {
                bearerAuth(token)
                header("employee_id", employeeId)
                parameter("city", cityName)
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
        token: String,
        employeeId: String,
        cityName: String,
        streetName: String
    ): Resource<List<String>> {
        return try {
            val response: ApiResponseDto<List<String>> = client.get("$ADDRESSES_PATH/streets-list") {
                bearerAuth(token)
                header("employee_id", employeeId)
                parameter("city", cityName)
                parameter("street", streetName)
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
        token: String,
        employeeId: String,
        cityName: String,
        streetName: String
    ): Resource<List<AddressFilterElement>> {
        return try {
            val response: ApiResponseDto<List<AddressModelDto>> = client.get("$ADDRESSES_PATH/addresses") {
                bearerAuth(token)
                header("employee_id", employeeId)
                parameter("city", cityName)
                parameter("street", streetName)
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