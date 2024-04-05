package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.states.Visibility

@Composable
fun JumpToTopButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClicked: () -> Unit
) {

    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "JumpToTopButton State changes"
    )
    val topOffset = transition.animateDp(label = "JumpToTopButton top offset") {
        if (it == Visibility.GONE) {
            (-48).dp
        } else {
            16.dp
        }
    }

    if (topOffset.value != (-48).dp) {
        val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        Box(
            modifier = modifier
                .offset(x = 0.dp, y = topOffset.value)
        ) {
            Row(
                modifier = modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onClicked.invoke() }
                    .background(surfaceColor)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.jump_to_top_button_text),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

}