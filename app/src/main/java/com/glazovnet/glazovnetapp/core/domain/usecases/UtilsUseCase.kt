package com.glazovnet.glazovnetapp.core.domain.usecases

import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.repository.UtilsApiRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
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