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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.AdditionalTextInfo
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffsListScreen(
    optionalTariffId: String?,
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

    val isArchiveSheetOpen = viewModel.isArchiveSheetOpen.collectAsState()
    ArchiveTariffsSheet(
        isSheetOpen = isArchiveSheetOpen.value,
        archiveScreenState = archiveTariffsState.value,
        onDismiss = {
            viewModel.hideArchive()
        }
    )

    DetailsSheet(
        tariffModel = detailsSheetState.value,
        isChangingInProgress = tariffsState.value.isUploading,
        isUserIsClient = isUserIsClient,
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
                                onCardClicked = {}
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
                                    onCardClicked = {}
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
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1f)
//                        .nestedScroll(scrollBehavior.nestedScrollConnection),
//                    content = {
//                        item {
//                            Spacer(modifier = Modifier.height(8.dp))
//                            TariffsGroupTitle(
//                                text = stringResource(id = R.string.tariff_type_unlimited_name),
//                                tariffsCount = unlimitedTariffs.value.size
//                            )
//                        }
//                        items(
//                            items = unlimitedTariffs.value,
//                            key = { it.id }
//                        ) {
//                            TariffCardV2(
//                                tariff = it,
//                                onCardClicked = {tariffId ->
//                                    viewModel.showDetails(tariffId)
//                                }
//                            )
//                        }
//                        item {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ) {
//                                Spacer(modifier = Modifier.height(8.dp))
//                                TariffsGroupTitle(
//                                    text = stringResource(id = R.string.tariff_type_limited_name),
//                                    tariffsCount = limitedTariffs.value.size
//                                )
//                            }
//                        }
//                        items(
//                            items = limitedTariffs.value,
//                            key = {it.id}
//                        ) {
//                            TariffCardV2(
//                                tariff = it,
//                                onCardClicked = {tariffId ->
//                                    viewModel.showDetails(tariffId)
//                                }
//                            )
//                        }
//                        item {
//                            Spacer(modifier = Modifier.navigationBarsPadding())
//                        }
//                    }
//                )
                TariffsArchiveButtonBottomBar(
                    onOpenTariffsArchiveClicked = {
                        viewModel.showArchive()
                    }
                )
            }
        }
    }

}

@Composable
private fun TariffsGroupTitle(
    text: String,
    tariffsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .padding(top = 4.dp),
            text = tariffsCount.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsSheet(
    tariffModel: TariffModel?,
    isChangingInProgress: Boolean,
    isUserIsClient: Boolean,
    onConnectTariffClicked: (tariffId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    if (tariffModel != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                //verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = tariffModel.name
                )
                if (tariffModel.description != null) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = tariffModel.description
                    )
                }
                AdditionalTextInfo(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    title = stringResource(id = R.string.tariff_card_speed_limit_details_text),
                    text = if (tariffModel.maxSpeed == 0) {
                        stringResource(id = R.string.tariff_card_speed_limit_none_value)
                    } else {
                        if (tariffModel.maxSpeed < 1024) {
                            stringResource(
                                id = R.string.tariff_card_speed_limit_kilobits_value,
                                tariffModel.maxSpeed
                            )
                        } else {
                            stringResource(
                                id = R.string.tariff_card_speed_limit_megabits_value,
                                tariffModel.maxSpeed / 1024
                            )
                        }
                    }
                )
                AdditionalTextInfo(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    title = stringResource(id = R.string.reusable_payment_cost_per_month_text),
                    text = pluralStringResource(
                        id = R.plurals.reusable_payment_cost_value,
                        count = tariffModel.costPerMonth,
                        formatArgs = arrayOf(tariffModel.costPerMonth)
                    )
                )
                if (tariffModel.prepaidTraffic !== null) {
                    AdditionalTextInfo(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        title = stringResource(id = R.string.tariff_card_prepaid_traffic_amount_text),
                        text = if (tariffModel.prepaidTraffic < 1024) {
                            pluralStringResource(
                                id = R.plurals.tariff_card_prepaid_traffic_megabytes_value,
                                count = tariffModel.prepaidTraffic.toInt(),
                                tariffModel.prepaidTraffic.toInt()
                            )
                        } else {
                            pluralStringResource(
                                id = R.plurals.tariff_card_prepaid_traffic_gigabytes_value,
                                count = tariffModel.prepaidTraffic.toInt() / 1024,
                                tariffModel.prepaidTraffic.toInt() / 1024
                            )
                        }
                    )
                } else {
                    AdditionalTextInfo(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        title = stringResource(id = R.string.tariff_card_prepaid_traffic_amount_text),
                        text = stringResource(id = R.string.tariff_card_prepaid_traffic_unlimited_text)
                    )
                }
                if (tariffModel.prepaidTrafficDescription !== null) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = tariffModel.prepaidTrafficDescription
                    )
                }
                if (tariffModel.isActive && isUserIsClient) {
                    Divider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            onConnectTariffClicked.invoke(tariffModel.id)
                        },
                        enabled = !isChangingInProgress
                    ) {
                        Text(text = stringResource(id = R.string.tariff_card_connect_from_billing_date)) //TODO(Change billing date to actual number)
                    }
                }
                Spacer(modifier = Modifier.navigationBarsPadding())
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