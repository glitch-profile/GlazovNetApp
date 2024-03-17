package com.glazovnet.glazovnetapp.notifications.domain.repository

import android.content.SharedPreferences

interface NotificationsLocalSettingRepository {

    val preferences: SharedPreferences

    fun getIsNotificationsSetupComplete(): Boolean
    fun setIsNotificationsSetupComplete(status: Boolean)

    fun getIsNotificationsEnabled(): Boolean
    fun setIsNotificationsEnabled(status: Boolean)

    fun getSelectedTopics(): List<String>
    fun setSelectedTopics(topicsList: List<String>)

}