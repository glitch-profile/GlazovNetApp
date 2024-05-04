package com.glazovnet.glazovnetapp.settings.notifications.presentation

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.CheckedButton
import com.glazovnet.glazovnetapp.core.presentation.components.DesignedSwitchButton
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MediumTopAppBar(
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
            ) {
                if (state.value.isLoading) {
                    LoadingComponent()
                } else if (state.value.stringResourceId != null) {
                    RequestErrorScreen(
                        messageStringResource = state.value.stringResourceId,
                        additionalMessage = state.value.message
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp),
                            text = stringResource(id = R.string.notifications_settings_screen_general_settings_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                        ) {
                            DesignedSwitchButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                title = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_enabled),
                                description = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_enabled_description),
                                isChecked = isNotificationsEnabled.value.data ?: false,
                                onStateChanges = { viewModel.setIsNotificationsEnabled(it) }
                            )
                            DesignedSwitchButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                title = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_on_device_enabled),
                                description = stringResource(id = R.string.notifications_settings_screen_global_settings_is_notifications_on_device_enabled_description),
                                isChecked = isNotificationsOnDeviceEnabled.value,
                                onStateChanges = { viewModel.setIsNotificationsOnDeviceEnabled(it) }
                            )
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PermissionScreen(
                                isPermissionsGranted = isPermissionsGranted.value,
                                isNotificationsEnabled = isNotificationsOnDeviceEnabled.value,
                                onRequestPermissionClick = {
                                    permissionRequestLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp),
                            text = stringResource(id = R.string.notifications_settings_screen_mailings_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                        ) {
                            availableTopics.value.data?.forEach {
                                val isChecked = selectedTopics.value.contains(it.topicCode)
                                CheckedButton(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    title = it.name,
                                    description = it.description,
                                    isChecked = isChecked,
                                    onStateChanges = { newCheckedState ->
                                        if (newCheckedState) viewModel.selectTopic(it.topicCode)
                                        else viewModel.unselectTopic(it.topicCode)
                                    },
                                    descriptionMaxLines = 1
                                )
                            }
                        }
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
    isPermissionsGranted: Boolean,
    isNotificationsEnabled: Boolean,
    onRequestPermissionClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isNotificationsEnabled && !isPermissionsGranted,
        enter = expandVertically()
                + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        CheckedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.small),
            title = stringResource(id = R.string.notifications_settings_screen_grant_permission),
            description = stringResource(id = R.string.notifications_settings_screen_permissions_description),
            isChecked = false,
            onStateChanges = { onRequestPermissionClick.invoke() },
            descriptionMaxLines = 5
        )
    }
}