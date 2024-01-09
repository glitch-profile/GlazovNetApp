package com.glazovnet.glazovnetapp.data.entity

data class ApiResponseDto<T> (
    val status: Boolean,
    val message: String,
    val data: T
)