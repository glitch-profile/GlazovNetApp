package com.glazovnet.glazovnetapp.supportrequests.presentation.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R

@Composable
fun AlarmMessage(
    messageCode: String
) {
    val alarmTextRes = remember {
        when (messageCode) {
            "request_closed" -> R.string.request_chat_alarm_request_closed_text
            "request_reopened" -> R.string.request_chat_alarm_request_reopened_text
            else -> null
        }
    }
    if (alarmTextRes != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = stringResource(id = alarmTextRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Divider(Modifier.weight(1f))
        }
    }

}