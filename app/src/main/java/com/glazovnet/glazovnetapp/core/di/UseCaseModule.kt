package com.glazovnet.glazovnetapp.core.di

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.usecases.UtilsUseCase
import com.glazovnet.glazovnetapp.login.domain.repository.LoginApiRepository
import com.glazovnet.glazovnetapp.login.domain.usecases.AuthUseCase
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.posts.domain.usecases.PostsUseCase
import com.glazovnet.glazovnetapp.supportrequests.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.supportrequests.domain.usecase.SupportChatUseCase
import com.glazovnet.glazovnetapp.supportrequests.domain.usecase.SupportRequestsUseCase
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
        localUserAuthDataRepository: LocalUserAuthDataRepository,
    ): AuthUseCase {
        return AuthUseCase(
            loginApiRepository = loginApiRepository,
            localUserAuthDataRepository = localUserAuthDataRepository
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