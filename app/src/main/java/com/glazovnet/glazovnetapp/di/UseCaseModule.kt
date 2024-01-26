package com.glazovnet.glazovnetapp.di

import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.usecase.AuthUseCase
import com.glazovnet.glazovnetapp.domain.usecase.PostsUseCase
import com.glazovnet.glazovnetapp.domain.usecase.SupportChatUseCase
import com.glazovnet.glazovnetapp.domain.usecase.SupportRequestsUseCase
import com.glazovnet.glazovnetapp.domain.usecase.UtilsUseCase
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
        utilsApiRepository: UtilsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): AuthUseCase {
        return AuthUseCase(
            utilsApiRepository, localUserAuthDataRepository
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

    @Provides
    @Singleton
    fun providePostsUseCase(
        postsApiRepository: PostsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): PostsUseCase {
        return PostsUseCase(postsApiRepository, localUserAuthDataRepository)
    }

    @Provides
    @Singleton
    fun provideRequestsUseCase(
        requestsApiRepository: RequestsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): SupportRequestsUseCase {
        return SupportRequestsUseCase(requestsApiRepository, localUserAuthDataRepository)
    }

    @Provides
    @Singleton
    fun provideSupportChatUseCase(
        requestsApiRepository: RequestsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): SupportChatUseCase {
        return SupportChatUseCase(requestsApiRepository, localUserAuthDataRepository)
    }
}