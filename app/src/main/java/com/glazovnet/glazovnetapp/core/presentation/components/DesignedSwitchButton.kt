package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesignedSwitchButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean,
    onStateChanges: (status: Boolean) -> Unit,
) {
    var showAdditionInfo by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = {
                    onStateChanges.invoke(!isChecked)
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    scope.coroutineContext.cancelChildren()
                    scope.launch {
                        showAdditionInfo = true
                        delay(5000)
                        showAdditionInfo = false
                    }
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            AnimatedVisibility(
                visible = showAdditionInfo,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = description,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        SwitchIndicator(isChecked = isChecked)
    }
}

@Composable
private fun SwitchIndicator(
    modifier: Modifier = Modifier,
    isChecked: Boolean
) {
    val switchWidth = 52.dp
    val switchHeight = 28.dp
    val handleSize = 20.dp
    val handlePadding = 4.dp

    val valueToOffset = if (isChecked) 1f else 0f
    val offset = remember { Animatable(valueToOffset) }
    val scope = rememberCoroutineScope()

    val backgroundColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "switchIndicatorBackgroundColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "switchIndicatorBorderColor"
    )
    val handleColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.outline,
        label = "switchIndicatorHandleColor"
    )

    DisposableEffect(isChecked) {
        if (offset.targetValue != valueToOffset) {
            scope.launch {
                offset.animateTo(valueToOffset, animationSpec = tween(300))
            }
        }
        onDispose {  }
    }

    Box(
        modifier = modifier
            .height(switchHeight)
            .width(switchWidth)
            .clip(RoundedCornerShape(switchHeight))
            .background(backgroundColor),
//            .border(
//                width = 2.dp,
//                color = borderColor,
//                shape = CircleShape
//            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = handlePadding)
                .size(handleSize)
                .offset(x = (switchWidth - handleSize - handlePadding * 2f) * offset.value)
                .clip(RoundedCornerShape(50))
                .background(handleColor)

        )
    }
}