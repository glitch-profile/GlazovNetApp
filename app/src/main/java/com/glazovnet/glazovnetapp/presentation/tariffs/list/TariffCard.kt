package com.glazovnet.glazovnetapp.presentation.tariffs.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType

@Composable
fun TariffCard(
    modifier: Modifier = Modifier,
    tariff: TariffModel,
    onCardClicked: (tariffId: String) -> Unit
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
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
                Text(
                    text = stringResource(id = R.string.tariff_card_max_speed_text, tariff.maxSpeed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
//                if (tariff.description != null) {
//                    Text(
//                        text = tariff.description,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        softWrap = true,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "₽",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = tariff.costPerMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 1.dp),
                    text = "/" + stringResource(id = R.string.tariff_card_month_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }



//        if (tariff.prepaidTraffic != null) {
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                text = buildAnnotatedString {
//                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
//                        append(stringResource(id = R.string.tariff_card_prepaid_traffic_prefix_text))
//                    }
//                    withStyle(MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold).toSpanStyle()) {
//                        append(pluralStringResource(
//                            id = R.plurals.tariff_card_prepaid_traffic_value,
//                            count = tariff.prepaidTraffic)
//                        )
//                    }
//                },
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                softWrap = true,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun TariffCardPreview() {
    TariffCard(
        modifier = Modifier.fillMaxWidth(),
        tariff = TariffModel(
            id = "",
            name = "Лайк-100",
            description = "Самый быстрый интернет для самых быстрых устройств",
            category = TariffType.Unlimited,
            maxSpeed = 100,
            costPerMonth = 600,
            prepaidTraffic = 3
        ),
        onCardClicked = {}
    )
}