package com.glazovnet.glazovnetapp.domain.repository

import com.glazovnet.glazovnetapp.data.entity.auth.AuthDataDto
import com.glazovnet.glazovnetapp.data.entity.auth.AuthResponse
import com.glazovnet.glazovnetapp.domain.utils.Resource
import java.io.File

interface UtilsApiRepository {

    suspend fun login(authData: AuthDataDto): Resource<AuthResponse>

    suspend fun uploadImage(file: File, token: String): Resource<List<String>>

    suspend fun getIntroImageUrl(): String

}