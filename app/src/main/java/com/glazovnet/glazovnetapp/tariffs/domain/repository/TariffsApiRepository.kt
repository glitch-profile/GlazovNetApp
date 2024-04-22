package com.glazovnet.glazovnetapp.tariffs.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel

interface TariffsApiRepository {

    suspend fun getAllTariffs(
        token: String,
        showOrganizationTariffs: Boolean
    ): Resource<List<TariffModel>>

    suspend fun getActiveTariffs(
        token: String,
        showOrganizationTariffs: Boolean
    ): Resource<List<TariffModel>>

    suspend fun getArchiveTariffs(
        token: String,
        showOrganizationTariffs: Boolean
    ): Resource<List<TariffModel>>

    suspend fun getTariffById(
        tariffId: String,
        token: String
    ): Resource<TariffModel?>

}