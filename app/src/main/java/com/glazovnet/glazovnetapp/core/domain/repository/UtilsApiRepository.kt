package com.glazovnet.glazovnetapp.core.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import java.io.File

interface UtilsApiRepository {

    suspend fun uploadImage(file: File, token: String): Resource<List<String>>

    suspend fun getIntroImageUrl(): String

}