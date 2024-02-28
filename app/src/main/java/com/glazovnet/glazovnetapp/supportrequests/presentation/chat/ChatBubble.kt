package com.glazovnet.glazovnetapp.supportrequests.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import java.time.OffsetDateTime

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    senderName: String,
    text: String,
    timestamp: OffsetDateTime,
    isOwnMessage: Boolean,
    isSameSender: Boolean,
    maxBubbleWidth: Dp = 250.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        val backgroundColor = if (isOwnMessage) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primaryContainer
        val textColor = if (isOwnMessage) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onPrimaryContainer
        val bubbleShape = if (isOwnMessage) RoundedCornerShape(
            topStart = 12.dp, topEnd = 4.dp, bottomEnd = 12.dp, bottomStart = 12.dp
        ) else RoundedCornerShape(
            topStart = 4.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp
        )
        Column {
            if (!isSameSender) {
                Text(
                    modifier = Modifier
                        .align(if (isOwnMessage) Alignment.End else Alignment.Start),
                    text = if (!isOwnMessage) senderName
                    else stringResource(id = R.string.chat_bubble_screen_sender_name_self),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Column(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(backgroundColor)
                    .padding(8.dp)
                    .widthIn(max = maxBubbleWidth)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.End),
                    text = timestamp.getLocalizedOffsetString(
                        daysPattern = "dd MMM"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }

        }
    }
}