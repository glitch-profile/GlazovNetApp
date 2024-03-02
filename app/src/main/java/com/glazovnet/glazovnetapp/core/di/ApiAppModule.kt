package com.glazovnet.glazovnetapp.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

private const val BASE_URL = "82.179.120.226" //notebook
//private const val BASE_URL = "146.120.105.211" //computer

private const val PORT = 8080

@Module
@InstallIn(SingletonComponent::class)
object ApiAppModule {

    @Provides
    @Singleton
    @Named("RestClient")
    fun provideGlazovNetKtorClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        expectSuccess = true
        install(WebSockets)
        install(DefaultRequest) {
            url("http://$BASE_URL:$PORT")
        }
    }

    @Provides
    @Singleton
    @Named("WsClient")
    fun provideGlazovNetWebSocketClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        expectSuccess = true
        install(WebSockets)
        install(DefaultRequest) {
            url("ws://$BASE_URL:$PORT")

        }
    }

}