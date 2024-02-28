package com.glazovnet.glazovnetapp.core.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DesignedCheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onStateChanges: (Boolean) -> Unit,
    label: String? = null,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    val unfocusedColor = primaryColor.copy(alpha = 0.7f)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {onStateChanges.invoke(it)},
            colors = CheckboxDefaults.colors(
                uncheckedColor = unfocusedColor,
                checkedColor = primaryColor
            )
        )
        if (label != null) {
            val textColor by animateColorAsState(
                if (isChecked) primaryColor
                else unfocusedColor,
                label = "CheckboxTextColorAnimation"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}