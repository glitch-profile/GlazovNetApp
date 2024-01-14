package com.glazovnet.glazovnetapp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.glazovnet.glazovnetapp.R
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun OffsetDateTime.getLocalizedOffsetString(
    daysPattern: String = "dd MMMM yyyy",
    timePattern: String = "HH:mm"
): String {
    val zoneCorrectlyDateTime = this.atZoneSameInstant(ZoneId.systemDefault())
    val localDateTime = zoneCorrectlyDateTime.toLocalDateTime()
    val localDate = localDateTime.toLocalDate()
    val date =  when (LocalDate.now()) {
        localDate -> stringResource(id = R.string.util_date_time_converter_today)
        localDate.plusDays(1L) -> stringResource(id = R.string.util_date_time_converter_yesterday)
        else -> localDate.format(
            DateTimeFormatter.ofPattern(daysPattern)
        ) + " -"
    }
    val time = localDateTime.format(
        DateTimeFormatter.ofPattern(timePattern)
    )
    return "$date $time"
}