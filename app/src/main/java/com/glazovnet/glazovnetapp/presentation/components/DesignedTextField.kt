package com.glazovnet.glazovnetapp.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun DesignedTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextEdit: (String) -> Unit,
    labelText: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    leadingIcon: ImageVector? = null,
    isEnable: Boolean = true,
    isReadOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = true,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    val unfocusedColor = primaryColor.copy(alpha = 0.7f)
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { onTextEdit.invoke(it) },
        enabled = isEnable,
        readOnly = isReadOnly,
        label = if (labelText != null) {
            { Text(text = labelText)}
        } else null,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder)}
        } else null,
        leadingIcon = if (leadingIcon != null) {
            { androidx.compose.material3.Icon(imageVector = leadingIcon, contentDescription = null) }
        } else null,
        supportingText = if (supportingText != null) {
            { Text(text = supportingText)}
        } else null,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            unfocusedLabelColor = unfocusedColor,
            unfocusedTextColor = unfocusedColor,
            unfocusedLeadingIconColor = unfocusedColor,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            unfocusedPlaceholderColor = unfocusedColor,

            focusedLabelColor = primaryColor,
            focusedTextColor = primaryColor,
            focusedLeadingIconColor = primaryColor,
            focusedIndicatorColor = primaryColor,
            cursorColor = primaryColor,
            focusedPlaceholderColor = primaryColor,

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
        )
    )
}