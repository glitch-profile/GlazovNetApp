package com.glazovnet.glazovnetapp.presentation.supportscreen.createrequest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.DesignedCheckBox
import com.glazovnet.glazovnetapp.presentation.components.DesignedOutlinedTextField
import com.glazovnet.glazovnetapp.presentation.components.FilledTextField
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    onNavigationButtonClicked: () -> Unit,
    onNeedToShowMessage: (Int) -> Unit,
    viewModel: CreateRequestViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val requestTitle = viewModel.requestTitle.collectAsState()
    val requestDescription = viewModel.requestDescription.collectAsState()
    val isNotificationsEnabled = viewModel.isNotificationsEnabled.collectAsState()

    LaunchedEffect(null) {
        viewModel.messageChannel.collectLatest {
            onNeedToShowMessage.invoke(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.create_request_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonClicked.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_title_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilledTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = requestTitle.value,
                    onValueChange = { viewModel.updateRequestTitle(it) },
                    placeholder = stringResource(id = R.string.create_request_screen_request_title_placeholder),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_description_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilledTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = requestDescription.value,
                    onValueChange = { viewModel.updateRequestDescription(it) },
                    placeholder = stringResource(id = R.string.create_request_screen_request_description_placeholder),
                    minLines = 5,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_notifications_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.create_request_screen_request_notifications_checkbox_placeholder),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    DesignedCheckBox(
                        isChecked = isNotificationsEnabled.value,
                        onStateChanges = { viewModel.updateRequestNotificationSettings(it) }
                    )
                }
            }
            BottomActionBar(
                onConfirmButtonClick = { viewModel.addRequest() },
                isConfirmButtonEnabled = !state.value.isUploading
                        && requestTitle.value.isNotBlank()
                        && requestDescription.value.isNotBlank()
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding()
            .imePadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            onClick = { onConfirmButtonClick.invoke() },
            enabled = isConfirmButtonEnabled
        ) {
            Text(text = stringResource(id = R.string.create_request_screen_save_request_button_text))
        }
    }
}