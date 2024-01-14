package com.glazovnet.glazovnetapp.di

import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.usecase.AuthUseCase
import com.glazovnet.glazovnetapp.domain.usecase.PostsUseCase
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
    fun providePostsUseCase(
        postsApiRepository: PostsApiRepository,
        localUserAuthDataRepository: LocalUserAuthDataRepository
    ): PostsUseCase {
        return PostsUseCase(postsApiRepository, localUserAuthDataRepository)
    }

}