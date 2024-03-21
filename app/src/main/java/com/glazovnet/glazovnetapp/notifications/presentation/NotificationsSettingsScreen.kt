package com.glazovnet.glazovnetapp.notifications.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.DesignedCheckBox
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(
    modifier: Modifier = Modifier,
    onNavigationButtonPressed: () -> Unit,
    viewModel: NotificationsSettingsViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val isNotificationsEnabled = viewModel.isNotificationsEnabled.collectAsState()
    val isNotificationsOnDeviceEnabled = viewModel.isNotificationsOnDeviceEnabled.collectAsState()
    val availableTopics = viewModel.availableTopics.collectAsState()
    val selectedTopics = viewModel.selectedTopics.collectAsState()
    val isPermissionsGranted = viewModel.isNotificationsPermissionGranted.collectAsState()

    val context = LocalContext.current
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setIsNotificationsPermissionGranted(isGranted)
        }
    )

    LaunchedEffect(null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    viewModel.setIsNotificationsPermissionGranted(true)
                    Log.i("PERMISSIONS", "NotificationsSettingsScreen: permission granted at screen launch")
                }
                else -> {
                    Log.i("PERMISSIONS", "NotificationsSettingsScreen: permission NOT granted at screen launch")
                    viewModel.setIsNotificationsPermissionGranted(false)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.notifications_settings_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
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
                if (state.value.isLoading) {
                    LoadingIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                } else if (state.value.stringResourceId != null) {
                    RequestErrorScreen(
                        messageStringResource = state.value.stringResourceId,
                        additionalMessage = state.value.message
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.notifications_settings_screen_global_settings_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CheckboxWithTitle(
                        title = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_enabled),
                        subtitle = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_enabled_description),
                        isChecked = isNotificationsEnabled.value.data ?: false,
                        onCheckedChange = { viewModel.setIsNotificationsEnabled(it) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CheckboxWithTitle(
                        title = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_on_device_enabled),
                        subtitle = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_on_device_enabled_description),
                        isChecked = isNotificationsOnDeviceEnabled.value,
                        onCheckedChange = { viewModel.setIsNotificationsOnDeviceEnabled(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.notifications_settings_screen_mailings_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    availableTopics.value.data?.forEach { topic ->
                        val isChecked = selectedTopics.value.contains(topic)
                        CheckboxWithTitle(
                            title = topic,
                            subtitle = "Здесь будет описание рассылки",
                            isChecked = isChecked,
                            onCheckedChange = {
                                if (isChecked) viewModel.unselectTopic(topic)
                                else viewModel.selectTopic(topic)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PermissionScreen(
                            modifier = Modifier
                                .fillMaxWidth(),
                            isPermissionsGranted = isPermissionsGranted.value,
                            isNotificationsEnabled = isNotificationsOnDeviceEnabled.value,
                            onRequestPermissionClick = {
                                permissionRequestLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        )
                    }
                }
            }
            BottomActionBar(
                onConfirmButtonClick = { viewModel.saveChanges() },
                isConfirmButtonEnabled = !state.value.isLoading
                        && !state.value.isUploading
                        && state.value.stringResourceId == null
            )
        }
    }
}

@Composable
private fun CheckboxWithTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        DesignedCheckBox(
            isChecked = isChecked,
            onStateChanges = onCheckedChange
        )
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
//        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
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
                Text(text = stringResource(id = R.string.reusable_text_save))
            }
        }
    }
}

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    isPermissionsGranted: Boolean,
    isNotificationsEnabled: Boolean,
    onRequestPermissionClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isNotificationsEnabled && !isPermissionsGranted,
        enter = slideInVertically()
                + expandVertically()
                + fadeIn(),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = modifier
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = stringResource(id = R.string.notifications_settings_screen_permissions_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = stringResource(id = R.string.notifications_settings_screen_permissions_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = onRequestPermissionClick
            ) {
                Text(text = stringResource(id = R.string.notifications_settings_screen_issue_permission))
            }
        }
    }
}