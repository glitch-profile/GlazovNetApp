package com.glazovnet.glazovnetapp.personalaccount.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientInfo

interface PersonalAccountRepository {

    suspend fun getClientData(
        token: String,
        clientId: String
    ): Resource<ClientInfo>

    suspend fun changePassword(
        token: String,
        personId: String,
        oldPassword: String,
        newPassword: String
    ): Resource<Unit>

    suspend fun changeTariff(
        token: String,
        clientId: String,
        newTariffId: String
    ): Resource<Unit>

    suspend fun blockAccount(
        token: String,
        clientId: String
    ): Resource<Unit>

    suspend fun addFunds(
        token: String,
        clientId: String,
        amount: Double,
        note: String?
    ): Resource<Unit>

}