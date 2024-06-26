package com.glazovnet.glazovnetapp.settings.notifications.domain.repository

import android.content.SharedPreferences

interface NotificationsLocalSettingRepository {

    val preferences: SharedPreferences

    fun getIsNotificationsSetupComplete(): Boolean
    fun setIsNotificationsSetupComplete(status: Boolean)

    fun getIsNotificationsEnabledOnDevice(): Boolean
    fun setIsNotificationsEnabledOnDevice(status: Boolean)

    fun getLastKnownFcmToken(): String?
    fun setLastKnownFcmToken(token: String?)

}