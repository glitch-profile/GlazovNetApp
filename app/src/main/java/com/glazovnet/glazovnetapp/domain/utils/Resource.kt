package com.glazovnet.glazovnetapp.domain.utils

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
}
