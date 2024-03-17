package com.glazovnet.glazovnetapp.notifications.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsLocalSettingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFERENCE_NAME = "notificationsSettings"
private const val IS_NOTIFICATIONS_SETUP_COMPLETE_NAME = "isSetupComplete"
private const val IS_NOTIFICATIONS_ENABLED_NAME = "isNotificationsEnabled"
private const val SELECTED_NOTIFICATIONS_TOPICS_NAME = "selectedTopics"


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

    private var isNotificationsEnabled: Boolean? = null
    override fun getIsNotificationsEnabled(): Boolean {
        return isNotificationsEnabled ?: kotlin.run {
            isNotificationsEnabled =  preferences.getBoolean(IS_NOTIFICATIONS_ENABLED_NAME, false)
            isNotificationsEnabled!!
        }
    }
    override fun setIsNotificationsEnabled(status: Boolean) {
        isNotificationsEnabled = status
        preferences.edit().putBoolean(IS_NOTIFICATIONS_ENABLED_NAME, status).apply()
    }

    private var selectedTopics: List<String>? = null
    override fun getSelectedTopics(): List<String> {
        return selectedTopics ?: kotlin.run {
            selectedTopics = preferences.getStringSet(SELECTED_NOTIFICATIONS_TOPICS_NAME, emptySet())?.toList() ?: emptyList()
            selectedTopics!!
        }
    }
    override fun setSelectedTopics(topicsList: List<String>) {
        selectedTopics = topicsList
        preferences.edit().putStringSet(SELECTED_NOTIFICATIONS_TOPICS_NAME, topicsList.toSet()).apply()
    }
}