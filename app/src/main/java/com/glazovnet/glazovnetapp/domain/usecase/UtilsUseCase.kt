package com.glazovnet.glazovnetapp.domain.usecase

import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import java.io.File
import javax.inject.Inject

class UtilsUseCase @Inject constructor(
    private val utilsApiRepository: UtilsApiRepository,
    private val localUserAuthDataRepository: LocalUserAuthDataRepository
) {
    suspend fun uploadFile(file: File): Resource<List<String>> {
        val token = localUserAuthDataRepository.getLoginToken() ?: ""
        return utilsApiRepository.uploadImage(
            file = file,
            token = token
        )
    }
}