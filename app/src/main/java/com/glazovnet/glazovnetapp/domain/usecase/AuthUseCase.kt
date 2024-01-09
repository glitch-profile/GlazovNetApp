package com.glazovnet.glazovnetapp.domain.usecase

import com.example.glazovnetadminapp.entity.authDto.AuthDataDto
import com.example.glazovnetadminapp.entity.authDto.AuthResponse
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val utilsApiRepository: UtilsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
) {
    suspend fun login(
        login: String,
        password: String,
        asAdmin: Boolean,
        isRememberAuthData: Boolean
    ): Resource<AuthResponse> {
        val authData = AuthDataDto(
            username = login,
            password = password,
            asAdmin = asAdmin
        )
        val loginResult = utilsApiRepository.login(authData)
        if (loginResult is Resource.Success) {
            val authResponse = loginResult.data!!
            localUserAuthDataRepository.setLoginToken(authResponse.token, isRememberAuthData)
            localUserAuthDataRepository.setAssociatedUserId(authResponse.userId, isRememberAuthData)
            localUserAuthDataRepository.setIsUserAsAdmin(authResponse.isAdmin, isRememberAuthData)
            localUserAuthDataRepository.setSavedUserLogin(login)
        }
        return loginResult
    }

    fun logout() {
        localUserAuthDataRepository.setLoginToken(null, true)
        localUserAuthDataRepository.setAssociatedUserId(null, true)
        localUserAuthDataRepository.setIsUserAsAdmin(isAdmin = false, true)
    }
}