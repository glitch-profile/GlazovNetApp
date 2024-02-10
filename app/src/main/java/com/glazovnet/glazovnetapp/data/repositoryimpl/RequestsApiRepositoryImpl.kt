package com.glazovnet.glazovnetapp.data.repositoryimpl

import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.supportrequests.MessageModelDto
import com.glazovnet.glazovnetapp.data.entity.supportrequests.SupportRequestDto
import com.glazovnet.glazovnetapp.data.entity.utils.ApiResponseDto
import com.glazovnet.glazovnetapp.data.mappers.toMessageModel
import com.glazovnet.glazovnetapp.data.mappers.toSupportRequestDto
import com.glazovnet.glazovnetapp.data.mappers.toSupportRequestModel
import com.glazovnet.glazovnetapp.domain.models.supportrequest.MessageModel
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestStatus
import com.glazovnet.glazovnetapp.domain.models.supportrequest.RequestStatus.Companion.convertToIntCode
import com.glazovnet.glazovnetapp.domain.models.supportrequest.SupportRequestModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
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

    override suspend fun getAllRequests(token: String): Resource<List<SupportRequestModel>> {
        return try {
            val response: ApiResponseDto<List<SupportRequestDto>> = client.get("$PATH/all-requests") {
                bearerAuth(token)
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

    override suspend fun getRequestsForClient(token: String): Resource<List<SupportRequestModel>> {
        return try {
            val response: ApiResponseDto<List<SupportRequestDto>> = client.get("$PATH/requests") {
                bearerAuth(token)
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
        requestId: String,
        token: String
    ): Resource<SupportRequestModel?> {
        return try {
            val response: ApiResponseDto<SupportRequestDto> = client.get("$PATH/requests/$requestId") {
                bearerAuth(token)
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
        requestId: String,
        token: String
    ): Resource<List<MessageModel>> {
        return try {
            val response: ApiResponseDto<List<MessageModelDto>> = client.get("$PATH/requests/$requestId/messages") {
                bearerAuth(token)
            }.body()
            if (response.status) {
                val messagesList = response.data.map { it.toMessageModel() }
                Resource.Success(
                    data = messagesList.map { message ->
                        message.copy(isOwnMessage = message.senderId == authDataRepository.getAssociatedUserId())
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
        newRequest: SupportRequestModel,
        token: String
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
        requestId: String,
        newStatus: RequestStatus,
        token: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/requests/$requestId/set-status") {
                bearerAuth(token)
                header("new_status", newStatus.convertToIntCode())
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
        requestId: String,
        newSupporterId: String,
        token: String
    ): Resource<Unit> {
        return try {
            val response: ApiResponseDto<Unit> = client.put("$PATH/requests/$requestId/set-helper") {
                bearerAuth(token)
                header("new_helper_id", newSupporterId)
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

    override suspend fun initRequestsSocket(token: String): Resource<Unit> {
        return try {
            requestsSocket = wsClient.webSocketSession {
                url(port = 8080, path = "$PATH/requests-socket")
                bearerAuth(token)
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

    override suspend fun initChatSocket(requestId: String, token: String): Resource<Unit> {
        return try {
            chatSocket = wsClient.webSocketSession {
                url(port = 8080, path = "$PATH/requests/$requestId/chat-socket")
                bearerAuth(token)
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
                    message.copy(isOwnMessage = message.senderId == authDataRepository.getAssociatedUserId())
                } ?: flow{}
        } catch (e: Exception) {
            e.printStackTrace()
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