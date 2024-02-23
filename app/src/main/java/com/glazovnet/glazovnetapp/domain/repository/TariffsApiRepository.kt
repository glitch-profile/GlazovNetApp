package com.glazovnet.glazovnetapp.domain.repository

import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.utils.Resource

interface TariffsApiRepository {

    suspend fun getAllTariffs(
        token: String
    ): Resource<List<TariffModel>>

    suspend fun getTariffById(
        tariffId: String,
        token: String
    ): Resource<TariffModel?>

}