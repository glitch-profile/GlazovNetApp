package com.glazovnet.glazovnetapp.notifications.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsLocalSettingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFERENCE_NAME = "notificationsSettings"
private const val IS_NOTIFICATIONS_SETUP_COMPLETE_NAME = "isSetupComplete"
private const val LAST_KNOWN_FCM_TOKEN = "lastKnownFcmToken"


class NotificationsLocalSettingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): NotificationsLocalSettingRepository {

    override val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private var isNotificationsSetupComplete: Boolean? = null
    override fun getIsNotificationsSetupComplete(): Boolean {
        return isNotificationsSetupComplete ?: kotlin.run {
            isNotificationsSetupComplete = preferences.getBoolean(
                IS_NOTIFICATIONS_SETUP_COMPLETE_NAME, false
            )
            isNotificationsSetupComplete!!
        }
    }
    override fun setIsNotificationsSetupComplete(status: Boolean) {
        isNotificationsSetupComplete = status
        preferences.edit().putBoolean(IS_NOTIFICATIONS_SETUP_COMPLETE_NAME, status).apply()
    }

    private var lastKnownFcmToken: String? = null
    override fun getLastKnownFcmToken(): String? {
        return lastKnownFcmToken ?: kotlin.run {
            lastKnownFcmToken = preferences.getString(
                LAST_KNOWN_FCM_TOKEN, null
            )
            lastKnownFcmToken
        }
    }

    override fun setLastKnownFcmToken(token: String?) {
        lastKnownFcmToken = token
        preferences.edit().putString(LAST_KNOWN_FCM_TOKEN, token).apply()
    }
}