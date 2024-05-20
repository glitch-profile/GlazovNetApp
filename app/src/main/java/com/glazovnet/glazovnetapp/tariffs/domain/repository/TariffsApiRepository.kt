package com.glazovnet.glazovnetapp.tariffs.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel

interface TariffsApiRepository {

    suspend fun getAllTariffs(
        token: String,
        clientId: String?,
        employeeId: String?
    ): Resource<List<TariffModel>>

    suspend fun getActiveTariffs(
        token: String,
        clientId: String?,
        employeeId: String?
    ): Resource<List<TariffModel>>

    suspend fun getArchiveTariffs(
        token: String,
        clientId: String?,
        employeeId: String?
    ): Resource<List<TariffModel>>

    suspend fun getTariffById(
        tariffId: String,
        token: String
    ): Resource<TariffModel?>

    suspend fun changeTariff(
        token: String,
        clientId: String,
        newTariffId: String?
    ): Resource<Unit>

}