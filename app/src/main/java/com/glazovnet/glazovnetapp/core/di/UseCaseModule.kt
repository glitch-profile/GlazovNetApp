package com.glazovnet.glazovnetapp.core.di

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.usecases.UtilsUseCase
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import com.glazovnet.glazovnetapp.login.domain.usecases.AuthUseCase
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsApiRepository
import com.glazovnet.glazovnetapp.settings.notifications.domain.repository.NotificationsLocalSettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAuthUseCase(
        loginApiRepository: LoginApiRepository,
        notificationsApiRepository: NotificationsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository,
        notificationsLocalSettingRepository: NotificationsLocalSettingRepository
    ): AuthUseCase {
        return AuthUseCase(
            loginApiRepository = loginApiRepository,
            notificationsApiRepository = notificationsApiRepository,
            localUserAuthDataRepository = localUserAuthDataRepository,
            notificationsLocalSettingRepository = notificationsLocalSettingRepository
        )
    }

    @Provides
    @Singleton
    fun provideUtilsUseCase(
        utilsApiRepository: UtilsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): UtilsUseCase {
        return UtilsUseCase(utilsApiRepository, localUserAuthDataRepository)
    }

}