package com.glazovnet.glazovnetapp.core.di

import com.glazovnet.glazovnetapp.announcements.data.repositoryimpl.AnnouncementsApiRepositoryImpl
import com.glazovnet.glazovnetapp.announcements.domain.repository.AnnouncementsApiRepository
import com.glazovnet.glazovnetapp.core.data.repositoryimpl.LocalUserAuthDataRepositoryImpl
import com.glazovnet.glazovnetapp.core.data.repositoryimpl.UtilsApiRepositoryImpl
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.login.data.repositoryimpl.LoginApiRepositoryImpl
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import com.glazovnet.glazovnetapp.personalaccount.data.repositoryimpl.PersonalAccountRepositoryImpl
import com.glazovnet.glazovnetapp.personalaccount.domain.repository.PersonalAccountRepository
import com.glazovnet.glazovnetapp.posts.data.repositoryimpl.PostsApiRepositoryImpl
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.settings.appearance.data.AppearanceSettingsRepositoryImpl
import com.glazovnet.glazovnetapp.settings.appearance.domain.AppearanceSettingsRepository
import com.glazovnet.glazovnetapp.settings.notifications.data.repository.NotificationsApiRepositoryImpl
import com.glazovnet.glazovnetapp.settings.notifications.data.repository.NotificationsLocalSettingRepositoryImpl
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsApiRepository
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsLocalSettingRepository
import com.glazovnet.glazovnetapp.supportrequests.data.repositoryimpl.RequestsApiRepositoryImpl
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.tariffs.data.repositoryimpl.TariffsApiRepositoryImpl
import com.glazovnet.glazovnetapp.tariffs.domain.repository.TariffsApiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsLocalUserAuthDataRepository(
        localUserAuthDataRepositoryImpl: LocalUserAuthDataRepositoryImpl
    ): LocalUserAuthDataRepository

    @Binds
    @Singleton
    abstract fun bindsNotificationsLocalSettingsRepository(
        notificationsLocalSettingRepositoryImpl: NotificationsLocalSettingRepositoryImpl
    ): NotificationsLocalSettingRepository

    @Binds
    @Singleton
    abstract fun bindsAppearanceSettingsRepository(
        appearanceSettingsRepositoryImpl: AppearanceSettingsRepositoryImpl
    ): AppearanceSettingsRepository

    @Binds
    @Singleton
    abstract fun bindsUtilsApiRepository(
        utilsApiRepositoryImpl: UtilsApiRepositoryImpl
    ): UtilsApiRepository

    @Binds
    @Singleton
    abstract fun bindsPostsRepository(
        postsApiRepositoryImpl: PostsApiRepositoryImpl
    ): PostsApiRepository

    @Binds
    @Singleton
    abstract fun bindRequestsRepository(
        requestsApiRepositoryImpl: RequestsApiRepositoryImpl
    ): RequestsApiRepository

    @Binds
    @Singleton
    abstract fun bindsTariffsRepository(
        tariffsApiRepositoryImpl: TariffsApiRepositoryImpl
    ): TariffsApiRepository

    @Binds
    @Singleton
    abstract fun bindsAnnouncementsRepository(
        announcementsApiRepositoryImpl: AnnouncementsApiRepositoryImpl
    ): AnnouncementsApiRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginApiRepositoryImpl: LoginApiRepositoryImpl
    ): LoginApiRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        notificationsApiRepositoryImpl: NotificationsApiRepositoryImpl
    ): NotificationsApiRepository

    @Binds
    @Singleton
    abstract fun bindsPersonalAccountRepository(
        personalAccountRepositoryImpl: PersonalAccountRepositoryImpl
    ): PersonalAccountRepository
}