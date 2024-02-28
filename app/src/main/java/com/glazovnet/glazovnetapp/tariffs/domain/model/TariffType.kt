package com.glazovnet.glazovnetapp.tariffs.domain.model

import com.glazovnet.glazovnetapp.R

sealed class TariffType(
    val stringResourceName: Int
) {
    data object Unlimited: TariffType(
        stringResourceName = R.string.tariff_type_unlimited_name
    )
    data object Limited: TariffType(
        stringResourceName = R.string.tariff_type_limited_name
    )
    data object Archive: TariffType(
        stringResourceName = R.string.tariff_type_archive_name
    )

    companion object {
        fun fromTariffTypeCode(code: Int): TariffType {
            return when (code) {
                0 -> Unlimited
                1 -> Limited
                2 -> Archive
                else -> Archive
            }
        }
        fun TariffType.toTariffTypeCode(): Int {
            return when (this) {
                Unlimited -> 0
                Limited -> 1
                Archive -> 2
            }
        }

        fun values(): List<TariffType> {
            return listOf(
                Unlimited,
                Limited,
                Archive
            )
        }
    }
}
