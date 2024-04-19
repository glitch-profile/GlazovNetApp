package com.glazovnet.glazovnetapp.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.glazovnet.glazovnetapp.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        createNotificationsChannels()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.075)
                    .build()
            }
            .build()
    }

    private fun createNotificationsChannels() {
        createNotificationChannel(
            channelId = "news",
            channelName = resources.getString(R.string.notification_channel_posts_name),
            channelDescription = resources.getString(R.string.notification_channel_posts_description),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        createNotificationChannel(
            channelId = "tariffs",
            channelName = resources.getString(R.string.notification_channel_tariffs_name),
            channelDescription = resources.getString(R.string.notification_channel_tariffs_description),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        createNotificationChannel(
            channelId = "announcements",
            channelName = resources.getString(R.string.notification_channel_announcements_name),
            channelDescription = resources.getString(R.string.notification_channel_announcements_description),
            NotificationManager.IMPORTANCE_HIGH
        )
        createNotificationChannel(
            channelId = "chat",
            channelName = resources.getString(R.string.notification_channel_chat_name),
            channelDescription = resources.getString(R.string.notification_channel_chat_description),
            NotificationManager.IMPORTANCE_HIGH
        )
        createNotificationChannel(
            channelId = "account_warnings",
            channelName = resources.getString(R.string.notification_channel_account_warnings_name),
            channelDescription = resources.getString(R.string.notification_channel_account_warnings_description),
            NotificationManager.IMPORTANCE_HIGH
        )
        createNotificationChannel(
            channelId = "service_posts",
            channelName = resources.getString(R.string.notification_channel_service_posts_title),
            channelDescription = resources.getString(R.string.notification_channel_service_posts_description),
            NotificationManager.IMPORTANCE_HIGH
        )
        createNotificationChannel(
            channelId = "other",
            channelName = resources.getString(R.string.notification_channel_others_name),
            channelDescription = resources.getString(R.string.notification_channel_others_description),
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String,
        channelImportance: Int
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            channelName,
            channelImportance
        ).apply {
            description = channelDescription
        }

        notificationManager.createNotificationChannel(channel)
    }
}