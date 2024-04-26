package com.glazovnet.glazovnetapp.settings.notifications.domain.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.mainactivity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.random.Random

private const val CHANNEL_ID = "other"

class PushNotificationService: FirebaseMessagingService() {

    @SuppressLint("DiscouragedApi")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        val deeplink = message.data["deeplink"]

        val clickIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(deeplink),
            this.applicationContext,
            MainActivity::class.java
        )
        val flag = PendingIntent.FLAG_IMMUTABLE
        val clickPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(clickIntent)
            getPendingIntent(1, flag)
        }

        val channelId = message.data["channel_id"] ?: CHANNEL_ID
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .apply {
                if (message.data["title"] !== null) {
                    setContentTitle(message.data["title"])
                } else if (message.data["title_loc_key"] !== null) {
                    val titleResId = resources.getIdentifier(
                        message.data["title_loc_key"],"string", packageName
                    )
                    if (message.data["title_loc_args"] !== null) {
                        val args = message.data["title_loc_args"]!!.split(", ")
                        if (args.size == 1) {
                            setContentTitle(resources.getString(titleResId, args.single()))
                        } else setContentTitle(resources.getString(titleResId, args))
                    } else setContentTitle(resources.getString(titleResId))
                }
            }
            .apply {
                if (message.data["body"] !== null) {
                    setContentText(message.data["body"])
                } else if (message.data["body_loc_key"] !== null) {
                    val bodyResId = resources.getIdentifier(
                        message.data["body_loc_key"],"string", packageName
                    )
                    if (message.data["body_loc_args"] !== null) {
                        val args = message.data["body_loc_args"]!!.split(", ")
                        if (args.size == 1) {
                            setContentText(resources.getString(bodyResId, args.single()))
                        } else setContentText(resources.getString(bodyResId, args))
                    } else setContentText(resources.getString(bodyResId))
                }
            }
            .apply {
                if (message.data["image"] !== null ) {
                    applyImageUrl(this, message.data["image"]!!)
                }
            }
            .setContentIntent(clickPendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    private fun applyImageUrl(
        builder: NotificationCompat.Builder,
        imageUrl: String
    ) = runBlocking {
        val url = URL(imageUrl)

        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                null
            }
        }?.let { bitmap ->
            builder.setLargeIcon(bitmap)
        }
    }
}