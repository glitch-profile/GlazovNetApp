package com.glazovnet.glazovnetapp.di

import com.glazovnet.glazovnetapp.data.repositoryimpl.LocalUserAuthDataRepositoryImpl
import com.glazovnet.glazovnetapp.data.repositoryimpl.PostsApiRepositoryImpl
import com.glazovnet.glazovnetapp.data.repositoryimpl.RequestsApiRepositoryImpl
import com.glazovnet.glazovnetapp.data.repositoryimpl.TariffsApiRepositoryImpl
import com.glazovnet.glazovnetapp.data.repositoryimpl.UtilsApiRepositoryImpl
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.RequestsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.TariffsApiRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
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

}