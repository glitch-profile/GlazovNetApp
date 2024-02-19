package com.glazovnet.glazovnetapp.data.entity.utils

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import java.util.Locale

@Keep
@Serializable
data class TranslatableDto(
    val en: String?,
    val ru: String
) {
    fun getTranslatableString(): String {
        return if (en == null) {
            ru
        } else {
            if (Locale.getDefault().language.equals(Locale("ru").language)) {
                ru
            } else en
        }
    }
}
