package com.glazovnet.glazovnetapp.notifications.domain.usecases

import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.notifications.domain.repository.NotificationsLocalSettingRepository
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationsTopicsUseCase @Inject constructor(
    private val notificationsLocalSettingRepository: NotificationsLocalSettingRepository,
    private val utilsApiRepository: UtilsApiRepository
) {

    suspend fun enableNotifications(
        authToken: String,
        clientId: String
    ) {
        val fcmToken = Firebase.messaging.token.await()
        utilsApiRepository.updateUserFcmToken(
            token = authToken,
            clientId = clientId,
            newToken = fcmToken
        )
        notificationsLocalSettingRepository.setIsNotificationsEnabled(true)
    }

    suspend fun disableNotifications(
        authToken: String,
        clientId: String
    ) {
        val previousStatus = notificationsLocalSettingRepository.getIsNotificationsEnabled()
        if (previousStatus) {
            utilsApiRepository.updateUserFcmToken(
                token = authToken,
                clientId = clientId,
                newToken = null
            )
            notificationsLocalSettingRepository.setIsNotificationsEnabled(false)
        }
    }

    fun subscribeToTopics(topicsList: List<String>) {
        val currentTopicsList = notificationsLocalSettingRepository.getSelectedTopics()
        //finding topics to unsubscribe
        val topicsToUnsubscribe = currentTopicsList.filter { !topicsList.contains(it) }
        topicsToUnsubscribe.forEach {
            Firebase.messaging.unsubscribeFromTopic(it)
        }
        //finding topics to subscribe
        val topicsToSubscribe = topicsList.filter { !currentTopicsList.contains(it) }
        topicsToSubscribe.forEach {
            Firebase.messaging.subscribeToTopic(it)
        }
        notificationsLocalSettingRepository.setSelectedTopics(topicsList = topicsList)
    }

    fun unsubscribeFromAllTopics() {
        val topicsToUnsubscribe = notificationsLocalSettingRepository.getSelectedTopics()
        topicsToUnsubscribe.forEach {
            Firebase.messaging.unsubscribeFromTopic(it)
        }
        notificationsLocalSettingRepository.setSelectedTopics(emptyList())
    }

}