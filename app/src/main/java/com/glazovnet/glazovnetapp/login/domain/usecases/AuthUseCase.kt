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
        password: String
    ): Resource<AuthResponse> {
        val authData = AuthDataDto(
            username = login,
            password = password
        )
        val loginResult = loginApiRepository.login(authData)
        if (loginResult is Resource.Success) {
            val authResponse = loginResult.data!!
            localUserAuthDataRepository.setSavedUserLogin(login)
            localUserAuthDataRepository.setLoginToken(authResponse.token)
            localUserAuthDataRepository.setAssociatedPersonId(authResponse.personId)
            localUserAuthDataRepository.setAssociatedClientId(authResponse.clientId)
            localUserAuthDataRepository.setAssociatedEmployeeId(authResponse.employeeId)
            localUserAuthDataRepository.setEmployeeRoles(authResponse.employeeRoles)

            //configuring notifications
            val isNotificationsSetupComplete = notificationsLocalSettingRepository.getIsNotificationsSetupComplete()
            val isNotificationsEnabledOnDevice = notificationsLocalSettingRepository.getIsNotificationsEnabledOnDevice()
            if (isNotificationsSetupComplete && isNotificationsEnabledOnDevice) {
                val lastKnownFcmToken = notificationsLocalSettingRepository.getLastKnownFcmToken()
                notificationsApiRepository.updateFcmToken(
                    authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                    personId = localUserAuthDataRepository.getAssociatedPersonId() ?: "",
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
        if (isNotificationsSetupComplete && isNotificationsEnabledOnDevice) {
            //TODO Replace to removeUserFcmToken
            val result = notificationsApiRepository.updateFcmToken(
                authToken = localUserAuthDataRepository.getLoginToken() ?: "",
                personId = localUserAuthDataRepository.getAssociatedPersonId() ?: "",
                token = notificationsLocalSettingRepository.getLastKnownFcmToken()!!,
                isExclude = true
            )
        }

        //configuring auth local data
        localUserAuthDataRepository.setLoginToken(null)
        localUserAuthDataRepository.setAssociatedPersonId(null)
        localUserAuthDataRepository.setAssociatedClientId(null)
        localUserAuthDataRepository.setAssociatedEmployeeId(null)
        localUserAuthDataRepository.setEmployeeRoles(null)
    }
}