package com.glazovnet.glazovnetapp.supportrequests.presentation.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel

@Composable
fun SupportRequestCard(
    modifier: Modifier = Modifier,
    data: SupportRequestModel,
    showAdditionInfo: Boolean,
    onClick: (requestId: String) -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onClick.invoke(data.id)
            }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                softWrap = true,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(
                statusText = stringResource(id = data.status.stringResourceRequestStatus)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = data.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            softWrap = true,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (showAdditionInfo) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.request_card_created_by_text) + " ")
                        withStyle(MaterialTheme.typography.titleSmall.toSpanStyle()) {
                            append(data.creatorName)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.request_card_creation_date_text) + " ")
                    withStyle(MaterialTheme.typography.titleSmall.toSpanStyle()) {
                        append(data.creationDate?.getLocalizedOffsetString() ?: "unknown")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
//            Text(
//                text = buildAnnotatedString {
//                    append(stringResource(id = R.string.request_card_status_text) + " ")
//                    withStyle(MaterialTheme.typography.titleSmall.toSpanStyle()) {
//                        append(stringResource(id = data.status.stringResourceRequestStatus))
//                    }
//                },
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
        }
    }
}

@Composable
private fun StatusChip(
    modifier: Modifier = Modifier,
    statusText: String
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}