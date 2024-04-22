package com.glazovnet.glazovnetapp.supportrequests.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.supportrequests.data.entity.MessageModelDto
import com.glazovnet.glazovnetapp.supportrequests.data.entity.SupportRequestDto
import com.glazovnet.glazovnetapp.supportrequests.data.mappers.toMessageModel
import com.glazovnet.glazovnetapp.supportrequests.data.mappers.toSupportRequestDto
import com.glazovnet.glazovnetapp.supportrequests.data.mappers.toSupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus.Companion.convertToIntCode
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

private const val PATH = "api/support"

class RequestsApiRepositoryImpl @Inject constructor(
    @Named("RestClient") private val client: HttpClient,
    @Named("WsClient") private val wsClient: HttpClient,
    private val authDataRepository: LocalUserAuthDataRepository
): RequestsApiRepository {

    private var requestsSocket: WebSocketSession? = null
    private var chatSocket: WebSocketSession? = null

    override suspend fun getAllRequests(
        token: String,
        employeeId: String
    ): Resource<List<SupportRequestModel>> {
        return try {
            val response: ApiResponseDto<List<SupportRequestDto>> = client.get("$PATH/all-requests") {
                bearerAuth(token)
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toSupportRequestModel() }
                )
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getRequestsForClient(
        token: String,
        clientId: String
    ): Resource<List<SupportRequestModel>> {
        return try {
            val response: ApiResponseDto<List<SupportRequestDto>> = client.get("$PATH/requests") {
                bearerAuth(token)
                header("client_id", clientId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.map { it.toSupportRequestModel() }
                )
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getRequestById(
        token: String,
        requestId: String,
        clientId: String?,
        employeeId: String?
    ): Resource<SupportRequestModel?> {
        return try {
            val response: ApiResponseDto<SupportRequestDto> = client.get("$PATH/requests/$requestId") {
                bearerAuth(token)
                header("client_id", clientId)
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                Resource.Success(
                    data = response.data.toSupportRequestModel()
                )
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun getMessagesForRequest(
        token: String,
        requestId: String,
        clientId: String?,
        employeeId: String?
    ): Resource<List<MessageModel>> {
        return try {
            val response: ApiResponseDto<List<MessageModelDto>> = client.get("$PATH/requests/$requestId/messages") {
                bearerAuth(token)
                header("client_id", clientId)
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                val messagesList = response.data.map { it.toMessageModel() }
                Resource.Success(
                    data = messagesList.map { message ->
                        message.copy(isOwnMessage = message.senderId == authDataRepository.getAssociatedPersonId())
                    }
                )
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun addRequest(
        token: String,
        newRequest: SupportRequestModel
    ): Resource<SupportRequestModel?> {
        return try {
            val response: ApiResponseDto<SupportRequestDto> = client.post("$PATH/create-request") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(newRequest.toSupportRequestDto())
            }.body()
            if (response.status) {
                Resource.Success(data = response.data.toSupportRequestModel())
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun changeRequestStatus(
        token: String,
        requestId: String,
        newStatus: RequestStatus,
        employeeId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/requests/$requestId/set-status") {
                bearerAuth(token)
                header("new_status", newStatus.convertToIntCode())
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                Resource.Success(Unit)
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun changeRequestSupporter(
        token: String,
        requestId: String,
        employeeId: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/requests/$requestId/set-helper") {
                bearerAuth(token)
                header("employee_id", employeeId)
            }.body()
            if (response.status) {
                Resource.Success(Unit)
            } else {
                Resource.Error(R.string.api_response_server_error, response.message)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun initRequestsSocket(token: String, personId: String): Resource<Unit> {
        return try {
            requestsSocket = wsClient.webSocketSession {
                url(port = 8080, path = "$PATH/requests-socket")
                bearerAuth(token)
                header("person_id", personId)
            }
            if (requestsSocket?.isActive == true) {
                Resource.Success(data = Unit)
            } else {
                Resource.Error(R.string.api_response_websocket_no_connection)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override fun observeRequests(): Flow<SupportRequestModel> {
        return try {
            requestsSocket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val stringRequests = (it as? Frame.Text)?.readText() ?: ""
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    val requestDto = json.decodeFromString<SupportRequestDto>(stringRequests)
                    requestDto.toSupportRequestModel()
                } ?: flow{}
        } catch (e: Exception) {
            e.printStackTrace()
            flow {}
        }
    }

    override suspend fun initChatSocket(
        token: String,
        requestId: String,
        personId: String
    ): Resource<Unit> {
        return try {
            chatSocket = wsClient.webSocketSession {
                url(port = 8080, path = "$PATH/requests/$requestId/chat-socket")
                bearerAuth(token)
                header("person_id", personId)
            }
            if (chatSocket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(R.string.api_response_websocket_no_connection)
            }
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override suspend fun sendMessage(message: String): Resource<Unit> {
        return try {
            chatSocket?.send(Frame.Text(message))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.generateFromApiResponseError(e)
        }
    }

    override fun observeMessages(): Flow<MessageModel> {
        return try {
            chatSocket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val encodedMessage = (it as? Frame.Text)?.readText() ?: ""
                    val json = Json { ignoreUnknownKeys = true }
                    val messageDto = json.decodeFromString<MessageModelDto>(encodedMessage)
                    val message = messageDto.toMessageModel()
                    message.copy(isOwnMessage = message.senderId == authDataRepository.getAssociatedPersonId())
                } ?: flow{}
        } catch (e: Exception) {
            flow { }
        }
    }

    override suspend fun closeRequestsConnection() {
        requestsSocket?.close()
    }

    override suspend fun closeChatConnection() {
        chatSocket?.close()
    }

}