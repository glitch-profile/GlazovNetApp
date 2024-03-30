package com.glazovnet.glazovnetapp.login.domain.usecases

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.login.data.entity.AuthDataDto
import com.glazovnet.glazovnetapp.login.data.entity.AuthResponse
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsApiRepository
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsLocalSettingRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val loginApiRepository: LoginApiRepository,
    private val notificationsApiRepository: NotificationsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository,
    private val notificationsLocalSettingRepository: NotificationsLocalSettingRepository
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

            //configuring notifications
            val isNotificationsSetupComplete = notificationsLocalSettingRepository.getIsNotificationsSetupComplete()
            val isNotificationsEnabledOnDevice = notificationsLocalSettingRepository.getIsNotificationsEnabledOnDevice()
            val isLoggingInAsAdmin = localUserAuthDataRepository.getIsUserAsAdmin()
            if (isNotificationsSetupComplete && isNotificationsEnabledOnDevice && !isLoggingInAsAdmin) {
                val lastKnownFcmToken = notificationsLocalSettingRepository.getLastKnownFcmToken()
                notificationsApiRepository.updateFcmToken(
                    authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                    clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                    token = lastKnownFcmToken!!
                )
            }
        }
        return loginResult
    }

    suspend fun logout() {
        //configuring notifications
        val isNotificationsSetupComplete = notificationsLocalSettingRepository.getIsNotificationsSetupComplete()
        val isNotificationsEnabledOnDevice = notificationsLocalSettingRepository.getIsNotificationsEnabledOnDevice()
        val isAdmin = localUserAuthDataRepository.getIsUserAsAdmin()
        if (isNotificationsSetupComplete && isNotificationsEnabledOnDevice && !isAdmin) {
            //TODO Replace to removeUserFcmToken
            val result = notificationsApiRepository.updateFcmToken(
                authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                clientId = localUserAuthDataRepository.getAssociatedUserId() ?: "",
                token = notificationsLocalSettingRepository.getLastKnownFcmToken()!!,
                isExclude = true
            )
        }

        //configuring auth local data
        localUserAuthDataRepository.setLoginToken(null, true)
        localUserAuthDataRepository.setAssociatedUserId(null, true)
        localUserAuthDataRepository.setIsUserAsAdmin(isAdmin = false, true)
    }
}