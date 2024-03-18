package com.glazovnet.glazovnetapp.notifications.domain.repository

import android.content.SharedPreferences

interface NotificationsLocalSettingRepository {

    val preferences: SharedPreferences

    fun getIsNotificationsSetupComplete(): Boolean
    fun setIsNotificationsSetupComplete(status: Boolean)

    fun getLastKnownFcmToken(): String?
    fun setLastKnownFcmToken(token: String?)

}