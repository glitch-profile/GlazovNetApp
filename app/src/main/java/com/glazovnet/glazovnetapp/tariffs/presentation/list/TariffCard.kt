package com.glazovnet.glazovnetapp.tariffs.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel

@Composable
fun TariffCard(
    modifier: Modifier = Modifier,
    tariff: TariffModel,
    onCardClicked: (tariffId: String) -> Unit
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onCardClicked.invoke(tariff.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = tariff.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (tariff.prepaidTraffic != null) {
                    Text(
                        text = buildString {
                            if (tariff.prepaidTraffic < 1024) {
                                append(pluralStringResource(
                                    id = R.plurals.tariff_card_prepaid_traffic_megabytes_value,
                                    count = tariff.prepaidTraffic.toInt(),
                                    tariff.prepaidTraffic.toInt()
                                ))
                            } else {
                                append(pluralStringResource(
                                    id = R.plurals.tariff_card_prepaid_traffic_gigabytes_value,
                                    count = tariff.prepaidTraffic.toInt() / 1024,
                                    tariff.prepaidTraffic.toInt() / 1024
                                ))
                            }
                            append(" ")
                            append(stringResource(id = R.string.tariff_card_of_prepaid_traffic))
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        softWrap = true,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = if (tariff.maxSpeed < 1024) stringResource(id = R.string.tariff_card_max_speed_kilobits_text, tariff.maxSpeed)
                        else stringResource(id = R.string.tariff_card_max_speed_megabits_text, tariff.maxSpeed / 1024),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        softWrap = true,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₽${tariff.costPerMonth}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.tariff_card_month_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
//            Row(
//                verticalAlignment = Alignment.Bottom
//            ) {
//                Text(
//                    text = "₽",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                )
//                Text(
//                    text = tariff.costPerMonth.toString(),
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                )
//                Text(
//                    modifier = Modifier
//                        .padding(bottom = 1.dp),
//                    text = "/" + stringResource(id = R.string.tariff_card_month_text),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                )
//            }
        }
    }
}

@Composable
fun TariffCardV3(
    tariff: TariffModel,
    onCardClicked: (tariffId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClicked.invoke(tariff.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = tariff.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            val descriptionText = if (tariff.prepaidTraffic != null) {
                buildString {
                    if (tariff.prepaidTraffic < 1024) {
                        append(pluralStringResource(
                            id = R.plurals.tariff_card_prepaid_traffic_megabytes_value,
                            count = tariff.prepaidTraffic.toInt(),
                            tariff.prepaidTraffic.toInt()
                        ))
                    } else {
                        append(pluralStringResource(
                            id = R.plurals.tariff_card_prepaid_traffic_gigabytes_value,
                            count = tariff.prepaidTraffic.toInt() / 1024,
                            tariff.prepaidTraffic.toInt() / 1024
                        ))
                    }
                    append(" ")
                    append(stringResource(id = R.string.tariff_card_of_prepaid_traffic))
                }
            } else {
                if (tariff.maxSpeed < 1024) stringResource(id = R.string.tariff_card_max_speed_kilobits_text, tariff.maxSpeed)
                else stringResource(id = R.string.tariff_card_max_speed_megabits_text, tariff.maxSpeed / 1024)
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = descriptionText,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "₽${tariff.costPerMonth}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.tariff_card_month_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}