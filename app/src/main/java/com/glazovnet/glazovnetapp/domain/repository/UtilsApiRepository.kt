package com.glazovnet.glazovnetapp.domain.repository

import com.example.glazovnetadminapp.entity.authDto.AuthDataDto
import com.example.glazovnetadminapp.entity.authDto.AuthResponse
import com.glazovnet.glazovnetapp.domain.utils.Resource
import java.io.File

interface UtilsApiRepository {

    suspend fun login(authData: AuthDataDto): Resource<AuthResponse>

    suspend fun uploadImage(file: File, token: String): Resource<List<String>>

}