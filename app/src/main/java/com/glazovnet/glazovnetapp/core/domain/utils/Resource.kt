package com.glazovnet.glazovnetapp.core.domain.utils

import com.glazovnet.glazovnetapp.R
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ResponseException

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val stringResourceId: Int? = null
) {
    class Success<T>(
        data: T,
        stringResourceId: Int? = null,
        message: String? = null,
    ) : Resource<T>(data, message, stringResourceId)
    class Error<T>(
        stringResourceId: Int,
        message: String? = null,
        data: T? = null
    ) : Resource<T>(data, message, stringResourceId)

    companion object {
        fun <T>generateFromApiResponseError(e: Exception): Error<T> {
            return when (e) {
                is ResponseException -> Error(R.string.api_response_error, e.response.status.toString())
                is ConnectTimeoutException -> Error(R.string.api_response_server_not_available)
                else -> Error(R.string.api_response_unknown_error)
            }
        }
    }
}
