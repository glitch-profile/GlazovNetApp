package com.glazovnet.glazovnetapp.tariffs.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.AdditionalVerticalInfo
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffsListScreen(
//    optionalTariffId: String?,
    onNavigationButtonPressed: () -> Unit,
    viewModel: TariffsListViewModel = hiltViewModel()
) {

    val tariffsState = viewModel.tariffsState.collectAsState()
    val unlimitedTariffs = viewModel.unlimitedTariffs.collectAsState()
    val limitedTariffs = viewModel.limitedTariffs.collectAsState()
    val currentClientsTariffData = viewModel.connectedTariffInfo.collectAsState()

    val archiveTariffsState = viewModel.archiveTariffsState.collectAsState()
    val detailsSheetState = viewModel.sheetData.collectAsState()

    val isUserIsClient = viewModel.isUserIsClient
    val isClientAsOrganization = viewModel.isClientAsOrganization.collectAsState()

    val isArchiveSheetOpen = viewModel.isArchiveSheetOpen.collectAsState()
    ArchiveTariffsSheet(
        isSheetOpen = isArchiveSheetOpen.value,
        archiveScreenState = archiveTariffsState.value,
        onDismiss = {
            viewModel.hideArchive()
        }
    )

    DetailsSheet(
        tariffDetails = detailsSheetState.value,
        billingDate = currentClientsTariffData.value?.billingDate,
        isChangingInProgress = tariffsState.value.isUploading,
        isUserIsClient = isUserIsClient,
        isUserAsOrganization = isClientAsOrganization.value,
        onConnectTariffClicked = {
            viewModel.connectTariff(it)
        },
        onDismiss = {
            viewModel.closeSheet()
        }
    )

    LaunchedEffect(null) {
        viewModel.loadActiveTariffs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.tariffs_list_screen_name))
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
                AnimatedVisibility(visible = !tariffsState.value.isLoading) {
                    IconButton(onClick = {
                        if (!tariffsState.value.isLoading) viewModel.loadActiveTariffs()
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
            if (tariffsState.value.isLoading && tariffsState.value.data == null) {
                LoadingComponent()
            } else if (tariffsState.value.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = tariffsState.value.stringResourceId,
                    additionalMessage = tariffsState.value.message
                )
            } else if (tariffsState.value.data != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (currentClientsTariffData.value != null) {
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.tariffs_list_current_sector_title),
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
                            TariffCardV3(
                                tariff = currentClientsTariffData.value!!.currentTariff,
                                onCardClicked = {
                                    viewModel.showDetails(it)
                                }
                            )
                        }
                        if (currentClientsTariffData.value!!.pendingTariff != null) {
                            val dateString = currentClientsTariffData.value!!.billingDate.format(
                                DateTimeFormatter.ofPattern("dd MMMM")
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                                text = stringResource(id = R.string.tariffs_list_pending_sector_title, dateString),
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
                                TariffCardV3(
                                    tariff = currentClientsTariffData.value!!.pendingTariff!!,
                                    onCardClicked = {
                                        viewModel.showDetails(it)
                                    }
                                )
                            }
                        }
                    }
                    if (unlimitedTariffs.value.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.tariffs_list_unlimited_sector_title),
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
                            unlimitedTariffs.value.forEach {
                                TariffCardV3(
                                    tariff = it,
                                    onCardClicked = {tariffId ->
                                        viewModel.showDetails(tariffId)
                                    }
                                )
                            }
                        }
                    }
                    if (limitedTariffs.value.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.tariffs_list_limited_sector_title),
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
                            limitedTariffs.value.forEach {
                                TariffCardV3(
                                    tariff = it,
                                    onCardClicked = {tariffId ->
                                        viewModel.showDetails(tariffId)
                                    }
                                )
                            }
                        }
                    }
                }
                TariffsArchiveButtonBottomBar(
                    onOpenTariffsArchiveClicked = {
                        viewModel.showArchive()
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsSheet(
    tariffDetails: TariffDetailsModel?,
    billingDate: OffsetDateTime?,
    isChangingInProgress: Boolean,
    isUserIsClient: Boolean,
    isUserAsOrganization: Boolean,
    onConnectTariffClicked: (tariffId: String?) -> Unit,
    onDismiss: () -> Unit
) {
    if (tariffDetails != null) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(id = R.string.tariff_details_information_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                val descriptionText = if (tariffDetails.isCurrentTariff) stringResource(id = R.string.tariff_details_current_tariff_description_title)
                else if (tariffDetails.isPendingTariff) stringResource(id = R.string.tariff_details_planned_tariff_description_title)
                else if (tariffDetails.tariff.prepaidTraffic != null) stringResource(id = R.string.tariff_details_limited_tariff_description_title)
                else stringResource(id = R.string.tariff_details_unlimited_tariff_description_title)
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = descriptionText,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdditionalVerticalInfo(
                    title = tariffDetails.tariff.name,
                    description = stringResource(id = R.string.tariff_details_tariff_name_title)
                )
                val maxTariffSpeedText = if (tariffDetails.tariff.maxSpeed == 0) {
                    stringResource(id = R.string.tariff_details_speed_limit_none_value)
                } else {
                    if (tariffDetails.tariff.maxSpeed < 1024) {
                        stringResource(
                            id = R.string.tariff_details_speed_limit_kilobits_value,
                            tariffDetails.tariff.maxSpeed
                        )
                    } else {
                        stringResource(
                            id = R.string.tariff_details_speed_limit_megabits_value,
                            tariffDetails.tariff.maxSpeed / 1024
                        )
                    }
                }
                AdditionalVerticalInfo(
                    title = maxTariffSpeedText,
                    description = stringResource(id = R.string.tariff_details_speed_limit_details_title)
                )
                AdditionalVerticalInfo(
                    title = pluralStringResource(
                        id = R.plurals.reusable_payment_cost_value,
                        count = tariffDetails.tariff.costPerMonth,
                        tariffDetails.tariff.costPerMonth
                    ),
                    description = stringResource(id = R.string.reusable_payment_cost_per_month_text)
                )
                if (tariffDetails.tariff.prepaidTraffic !== null) {
                    AdditionalVerticalInfo(
                        title = if (tariffDetails.tariff.prepaidTraffic < 1024) {
                            pluralStringResource(
                                id = R.plurals.tariff_card_prepaid_traffic_megabytes_value,
                                count = tariffDetails.tariff.prepaidTraffic.toInt(),
                                tariffDetails.tariff.prepaidTraffic.toInt()
                            )
                        } else {
                            pluralStringResource(
                                id = R.plurals.tariff_card_prepaid_traffic_gigabytes_value,
                                count = tariffDetails.tariff.prepaidTraffic.toInt() / 1024,
                                tariffDetails.tariff.prepaidTraffic.toInt() / 1024
                            )
                        },
                        description = stringResource(id = R.string.tariff_details_prepaid_traffic_amount_title)
                    )
                } else {
                    AdditionalVerticalInfo(
                        title = stringResource(id = R.string.tariff_details_prepaid_traffic_unlimited_text),
                        description = stringResource(id = R.string.tariff_details_prepaid_traffic_amount_title)
                    )
                }
                if (tariffDetails.tariff.isActive && isUserIsClient && !tariffDetails.isCurrentTariff) {
                    if (tariffDetails.tariff.isForOrganization == isUserAsOrganization) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(48.dp),
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                if (tariffDetails.isPendingTariff) {
                                    onConnectTariffClicked.invoke(null)
                                } else {
                                    onConnectTariffClicked.invoke(tariffDetails.tariff.id)
                                }
                            },
                            enabled = !isChangingInProgress
                        ) {
                            Text(
                                if (tariffDetails.isPendingTariff) {
                                    stringResource(id = R.string.tariff_details_cancel_pending_tariff_action)
                                } else {
                                    val billingDateText = billingDate?.format(DateTimeFormatter.ofPattern("dd MMMM"))
                                    if (billingDateText != null) {
                                        stringResource(id = R.string.tariff_details_connect_from_billing_date_action, billingDateText)
                                    } else stringResource(id = R.string.tariff_details_connect_from_unknown_billing_date_action)
                                }
                            )
                        }
                    } else Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TariffsArchiveButtonBottomBar(
    onOpenTariffsArchiveClicked: () -> Unit,
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
            onClick = { onOpenTariffsArchiveClicked.invoke() },
        ) {
            Text(text = stringResource(id = R.string.tariffs_list_open_tariffs_archive_button_text))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveTariffsSheet(
    isSheetOpen: Boolean,
    archiveScreenState: ScreenState<List<TariffModel>>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize(),
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            windowInsets = WindowInsets(0.dp)
        ) {
            if (archiveScreenState.isLoading && archiveScreenState.data == null) {
                LoadingComponent()
            } else if (archiveScreenState.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = archiveScreenState.stringResourceId,
                    additionalMessage = archiveScreenState.message
                )
            } else if (archiveScreenState.data != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.tariffs_list_tariffs_archive_text),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    modifier = Modifier
                                        .padding(top = 4.dp),
                                    text = archiveScreenState.data.size.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        items(
                            items = archiveScreenState.data,
                            key = { it.id }
                        ) {
                            TariffCard(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                tariff = it,
                                onCardClicked = {}
                            )
                        }
                    }
                )
            }
        }
    }
}