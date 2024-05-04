package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.core.presentation.states.Visibility

@Composable
fun MessageNotification(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    title: String,
    additionText: String,
    icon: ImageVector? = null,
) {

    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "ErrorMessage Visibility Changes"
    )

    val verticalOffset = transition.animateDp(label = "ErrorMessage Vertical Offset") {
        if (it == Visibility.GONE) {
            (-85).dp
        } else {
            16.dp
        }
    }
    if (verticalOffset.value != (-85).dp) {
        Box(
            modifier = modifier
                .imePadding()
                .fillMaxSize()
                .clipToBounds()
                .offset(x = 0.dp, y = -verticalOffset.value)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = icon,
                        tint = MaterialTheme.colorScheme.inverseOnSurface,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = title,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = additionText,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}