package com.glazovnet.glazovnetapp.services.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.AdditionalVerticalInfo
import com.glazovnet.glazovnetapp.core.presentation.components.CheckedButton
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onNavigationButtonPressed: () -> Unit,
    viewModel: ServicesViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val availableServices = viewModel.availableServices.collectAsState()
    val unavailableServices = viewModel.unavailableServices.collectAsState()

    val isUserAsClient = viewModel.clientId != null
    val connectedServices = viewModel.connectedServices.collectAsState()

    val currentOpenService = viewModel.currentOpenService.collectAsState()
    ConnectConfirmationScreen(
        data = currentOpenService.value,
        isConnectionInProgress = state.value.isUploading,
        isUserAsClient = isUserAsClient,
        onDismiss = {
            viewModel.closeDetailsScreen()
        },
        onConnectConfirmed = {
            viewModel.connectService(it)
        },
        onDisconnectConfirmed = {
            viewModel.disconnectService(it)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        LargeTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.services_screen_name)
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
            },
            actions = {
                AnimatedVisibility(visible = !state.value.isLoading) {
                    IconButton(onClick = {
                        if (!state.value.isLoading) viewModel.loadServices()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Update page")
                    }
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
                } else if (state.value.data != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (availableServices.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                                text = stringResource(id = R.string.services_screen_all_services_sector_title),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                            ) {
                                availableServices.value.forEach {  service ->
                                    CheckedButton(
                                        title = service.name,
                                        description = service.description,
                                        isChecked = connectedServices.value.contains(service.id),
                                        onStateChanges = {
                                            viewModel.openDetailsScreen(service.id)
                                        },
                                        descriptionMaxLines = 2
                                    )
                                }
                            }
                        }
                        if (unavailableServices.value.isNotEmpty()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                                text = stringResource(id = R.string.services_screen_unavailable_services_sector_title),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                            ) {
                                unavailableServices.value.forEach {  service ->
                                    CheckedButton(
                                        title = service.name,
                                        description = service.description,
                                        isChecked = connectedServices.value.contains(service.id),
                                        onStateChanges = {

                                        },
                                        descriptionMaxLines = 2
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectConfirmationScreen(
    data: ServiceDetailsModel?,
    isConnectionInProgress: Boolean,
    isUserAsClient: Boolean,
    onDismiss: () -> Unit,
    onConnectConfirmed: (serviceId: String) -> Unit,
    onDisconnectConfirmed: (serviceId: String) -> Unit
) {
    if (data != null) {
        val bottomSheetState = rememberModalBottomSheetState(true)
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = onDismiss,
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                if (isUserAsClient) {
                    Text(
                        text = stringResource(id = R.string.services_screen_details_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (data.isServiceConnected) stringResource(id = R.string.services_screen_details_disconnect_description)
                        else stringResource(id = R.string.services_screen_details_connect_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.services_screen_details_overview_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.services_screen_details_overview_description),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                AdditionalVerticalInfo(
                    title = data.service.name,
                    description = stringResource(id = R.string.services_screen_details_service_name)
                )
                AdditionalVerticalInfo(
                    title = data.service.description,
                    description = stringResource(id = R.string.services_screen_details_service_description_title)
                )
                val costForMonthText = if (data.service.costPerMonth == 0) {
                    stringResource(id = R.string.reusable_payment_cost_free_text)
                } else {
                    pluralStringResource(
                        id = R.plurals.reusable_payment_cost_value,
                        count = data.service.costPerMonth,
                        data.service.costPerMonth
                    )
                }
                AdditionalVerticalInfo(
                    title = costForMonthText,
                    description = stringResource(id = R.string.reusable_payment_cost_per_month_text)
                )
                if (data.service.connectionCost != null) {
                    val priceText = pluralStringResource(
                        id = R.plurals.reusable_payment_cost_value,
                        count = data.service.connectionCost,
                        data.service.connectionCost
                    )
                    AdditionalVerticalInfo(
                        title = stringResource(id = R.string.services_screen_details_payment_policy_instant_description, priceText),
                        description = stringResource(id = R.string.services_screen_details_payment_policy_title)
                    )
                } else {
                    AdditionalVerticalInfo(
                        title = stringResource(id = R.string.services_screen_details_payment_policy_pending_description),
                        description = stringResource(id = R.string.services_screen_details_payment_policy_title)
                    )
                }
                if (isUserAsClient) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            if (data.isServiceConnected) {
                                onDisconnectConfirmed.invoke(data.service.id)
                            } else {
                                onConnectConfirmed.invoke(data.service.id)
                            }
                        },
                        enabled = !isConnectionInProgress
                    ) {
                        Text(
                            text = if (data.isServiceConnected) stringResource(id = R.string.services_screen_details_disconnect_button_text)
                            else stringResource(id = R.string.services_screen_details_connect_button_text)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}