package com.glazovnet.glazovnetapp.presentation.tariffs.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffModel
import com.glazovnet.glazovnetapp.domain.models.tariffs.TariffType
import com.glazovnet.glazovnetapp.presentation.ScreenState
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffsListScreen(
    onTariffClicked: (tariffId: String) -> Unit,
    onNavigationButtonPressed: () -> Unit,
    viewModel: TariffsListViewModel = hiltViewModel()
) {

    val tariffsState = viewModel.tariffsState.collectAsState()
    val detailsSheetState = viewModel.sheetData.collectAsState()

    val isSheetOpen = viewModel.isDetailsSheetOpen.collectAsState()
    DetailsSheet(
        isSheetOpen = isSheetOpen.value,
        state = detailsSheetState.value,
        onConnectTariffClicked = {

        },
        onDismiss = {
            viewModel.closeSheet()
        }
    )

    LaunchedEffect(null) {
        viewModel.loadTariffs()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
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
                        if (!tariffsState.value.isLoading) viewModel.loadTariffs()
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
                LoadingIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            } else if (tariffsState.value.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = tariffsState.value.stringResourceId,
                    additionalMessage = tariffsState.value.message
                )
            } else if (tariffsState.value.data != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    content = {
                        items(
                            items = tariffsState.value.data!!.toList(),
                            key = { it.first.toString() }
                        ) {
                                TariffsList(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp),
                                    tariffType = it.first,
                                    tariffs = it.second,
                                    onTariffClicked = {tariffId ->
                                        viewModel.showDetails(tariffId)
                                    }
                                )
//                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                )
            }
        }
    }

}

@Composable
private fun TariffsList(
    modifier: Modifier = Modifier,
    tariffType: TariffType,
    tariffs: List<TariffModel>,
    onTariffClicked: (tariffId: String) -> Unit
) {
   Column(
       modifier = modifier
           .fillMaxWidth(),
   ) {
       Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(horizontal = 16.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
           Text(
               text = stringResource(id = tariffType.stringResourceName),
               style = MaterialTheme.typography.titleLarge,
               fontWeight = FontWeight.Bold,
               color = MaterialTheme.colorScheme.onSurface
           )
           Spacer(modifier = Modifier.width(8.dp))
           Text(
               text = tariffs.size.toString(),
               style = MaterialTheme.typography.titleSmall,
//               fontWeight = FontWeight.Bold,
               color = MaterialTheme.colorScheme.onSurfaceVariant
           )
       }
       Column(
           modifier = Modifier
               .fillMaxWidth()
       ) {
           tariffs.forEach {tariff ->
               TariffCard(
                   modifier = Modifier
                       .fillMaxWidth(),
                   tariff = tariff,
                   onCardClicked = onTariffClicked
               )
           }
       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsSheet(
    isSheetOpen: Boolean,
    state: ScreenState<TariffModel>,
    onConnectTariffClicked: (tariffId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .animateContentSize()
            ) {
                if (state.isLoading) {
                    LoadingIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                } else if (state.stringResourceId !== null) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            text = stringResource(id = R.string.tariffs_list_loading_failed_text),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = state.stringResourceId),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (state.message !== null) {
                                Text(
                                    text = " | ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                } else if (state.data != null) {
                    Text(
                        text = state.data.id
                    )
                }
            }
        }
    }
}