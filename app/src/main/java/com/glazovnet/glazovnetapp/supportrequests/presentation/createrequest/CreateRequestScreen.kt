package com.glazovnet.glazovnetapp.supportrequests.presentation.createrequest

import androidx.compose.foundation.background
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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.DesignedSwitchButton
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField
import com.glazovnet.glazovnetapp.core.presentation.components.MessageNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    onNavigationButtonClicked: () -> Unit,
    viewModel: CreateRequestViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val requestTitle = viewModel.requestTitle.collectAsState()
    val requestDescription = viewModel.requestDescription.collectAsState()
    val isNotificationsEnabled = viewModel.isNotificationsEnabled.collectAsState()

    val messageState = viewModel.messageState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        MediumTopAppBar(
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
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_title_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        .padding(start = 32.dp, end = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_description_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        .padding(start = 32.dp, end = 16.dp),
                    text = stringResource(id = R.string.create_request_screen_request_settings_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    DesignedSwitchButton(
                        title = stringResource(id = R.string.create_request_screen_request_notifications_title),
                        description = stringResource(id = R.string.create_request_screen_request_notifications_checkbox_placeholder),
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
    MessageNotification(
        enabled = messageState.value.enabled,
        title = stringResource(id = messageState.value.titleResource),
        additionText = stringResource(id = messageState.value.additionTextResource)
    )
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