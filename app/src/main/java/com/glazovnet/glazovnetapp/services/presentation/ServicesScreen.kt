package com.glazovnet.glazovnetapp.services.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
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

    val connectedServices = viewModel.connectedServices.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                                            if (it) viewModel.connectService(service.id)
                                            else viewModel.disconnectService(service.id)
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