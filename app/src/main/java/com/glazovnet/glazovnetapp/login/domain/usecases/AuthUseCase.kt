package com.glazovnet.glazovnetapp.login.domain.usecases

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.login.data.entity.AuthDataDto
import com.glazovnet.glazovnetapp.login.data.entity.AuthResponse
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val loginApiRepository: LoginApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
) {
    suspend fun login(
        login: String,
        password: String,
        asAdmin: Boolean
    ): Resource<AuthResponse> {
        val authData = AuthDataDto(
            username = login,
            password = password,
            asAdmin = asAdmin
        )
        val loginResult = loginApiRepository.login(authData)
        if (loginResult is Resource.Success) {
            val authResponse = loginResult.data!!
            localUserAuthDataRepository.setLoginToken(authResponse.token, true)
            localUserAuthDataRepository.setAssociatedUserId(authResponse.userId, true)
            localUserAuthDataRepository.setIsUserAsAdmin(authResponse.isAdmin, true)
            localUserAuthDataRepository.setSavedUserLogin(login)
        }
        return loginResult
    }

    suspend fun logout() {
        localUserAuthDataRepository.setLoginToken(null, true)
        localUserAuthDataRepository.setAssociatedUserId(null, true)
        localUserAuthDataRepository.setIsUserAsAdmin(isAdmin = false, true)
    }
}