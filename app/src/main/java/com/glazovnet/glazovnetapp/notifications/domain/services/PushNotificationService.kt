package com.glazovnet.glazovnetapp.notifications.domain.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.glazovnet.glazovnetapp.core.presentation.mainactivity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "main_channel"
private const val CHANNEL_NAME = "Miscellaneous"

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .apply {
                if (message.notification?.title !== null) {
                    setContentTitle(message.data["title"])
                } else if (message.notification?.titleLocalizationKey !== null) {
                    if (message.notification?.titleLocalizationArgs !== null) {
                        val identifier = resources.getIdentifier(
                            message.notification!!.titleLocalizationKey,"string", packageName
                        )
                        val args = message.notification!!.titleLocalizationArgs!!.toList()
                        if (args.size == 1) {
                            setContentTitle(resources.getString(identifier, args.single()))
                        } else setContentTitle(resources.getString(identifier, args))
                    } else setContentTitle(
                        resources.getString(
                            resources.getIdentifier(
                                message.notification!!.titleLocalizationKey, "string", packageName
                            )
                        )
                    )
                }
            }
            .apply {
                if (message.notification?.body !== null) {
                    setContentText(message.notification?.body)
                } else if (message.notification?.bodyLocalizationKey !== null) {
                    if (message.notification?.bodyLocalizationArgs !== null) {
                        val identifier = resources.getIdentifier(
                            message.notification!!.titleLocalizationKey,"string", packageName
                        )
                        val args = message.notification!!.bodyLocalizationKey!!.toList()
                        if (args.size == 1) {
                            setContentText(resources.getString(identifier, args.single()))
                        } else setContentText(resources.getString(identifier, args))
                    } else setContentText(
                        resources.getString(
                            resources.getIdentifier(
                                message.notification!!.bodyLocalizationKey, "string", packageName
                            )
                        )
                    )
                }
            }
            //TODO Add image to notifications
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
            description = "Channel for all Glazov.Net Notifications"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}