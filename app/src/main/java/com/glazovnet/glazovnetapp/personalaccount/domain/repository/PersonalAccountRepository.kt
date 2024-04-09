package com.glazovnet.glazovnetapp.personalaccount.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientInfo

interface PersonalAccountRepository {

    suspend fun getClientData(
        token: String,
        userId: String
    ): Resource<ClientInfo>

    suspend fun changePassword(
        token: String,
        userId: String,
        oldPassword: String,
        newPassword: String
    ): Resource<Unit>

    suspend fun changeTariff(
        token: String,
        userId: String,
        newTariffId: String
    ): Resource<Unit>

    suspend fun addFunds(
        token: String,
        userId: String,
        amount: Double,
        note: String?
    ): Resource<Unit>

}