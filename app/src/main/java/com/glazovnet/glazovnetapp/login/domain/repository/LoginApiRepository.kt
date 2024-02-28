package com.glazovnet.glazovnetapp.login.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.login.data.entity.AuthDataDto
import com.glazovnet.glazovnetapp.login.data.entity.AuthResponse

interface LoginApiRepository {

    suspend fun login(authData: AuthDataDto): Resource<AuthResponse>

}