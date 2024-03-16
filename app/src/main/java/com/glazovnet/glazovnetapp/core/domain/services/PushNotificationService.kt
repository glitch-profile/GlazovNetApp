package com.glazovnet.glazovnetapp.core.domain.services

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}