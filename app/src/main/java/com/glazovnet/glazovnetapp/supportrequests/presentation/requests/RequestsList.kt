package com.glazovnet.glazovnetapp.supportrequests.presentation.requests

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.JumpToTopButton
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsListScreen(
    onNavigationButtonClicked: () -> Unit,
    onAddNewRequestClicked: () -> Unit,
    onRequestClicked: (requestId: String) -> Unit,
    viewModel: RequestsViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val isEmployeeWithRole = viewModel.isEmployeeWithRole

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.loadRequests()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.request_screen_name))
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonClicked.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            actions = {
                AnimatedVisibility(visible = !state.value.isLoading && !isEmployeeWithRole) {
                    IconButton(onClick = {
                        if (!state.value.isLoading && !isEmployeeWithRole) viewModel.loadRequests()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update page"
                        )
                    }
                }
                if (!isEmployeeWithRole) {
                    IconButton(onClick = { onAddNewRequestClicked.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new request"
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (state.value.isLoading && state.value.data == null) {
                LoadingComponent()
            } else if (state.value.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = state.value.stringResourceId,
                    additionalMessage = state.value.message
                )
            } else if (state.value.data != null) {
                if (state.value.data!!.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clipToBounds()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                            state = lazyListState,
                            content = {
                                items(
                                    items = state.value.data!!,
                                    key = { it.id }
                                ) {
                                    SupportRequestCard(
                                        modifier = Modifier
                                            .padding(top = 8.dp),
                                        data = it,
                                        showAdditionInfo = isEmployeeWithRole,
                                        onClick = onRequestClicked
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.navigationBarsPadding())
                                }
                            }
                        )
                        val isJumpToTopButtonVisible = derivedStateOf {
                            lazyListState.firstVisibleItemIndex != 0
                        }
                        JumpToTopButton(
                            modifier = Modifier.align(Alignment.TopCenter),
                            enabled = isJumpToTopButtonVisible.value,
                            onClicked = {
                                scope.launch {
                                    lazyListState.animateScrollToItem(0)
                                }
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEmployeeWithRole) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.request_screen_no_request_found_admin_text),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.request_screen_no_request_found_user_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.request_screen_no_request_found_user_main_text),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = stringResource(id = R.string.request_screen_no_request_found_user_autoguide_text),
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                textAlign = TextAlign.Center
//                            )
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                text = stringResource(id = R.string.request_screen_no_request_found_user_manual_request_text),
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                textAlign = TextAlign.Center
//                            )
                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(
////                                modifier = Modifier
////                                    .height(48.dp),
//                                shape = MaterialTheme.shapes.small,
//                                onClick = onAddNewRequestClicked
//                            ) {
//                                Text(text = stringResource(id = R.string.request_screen_no_request_found_user_autoguide_button))
//                            }
//                            TextButton(
//                                shape = MaterialTheme.shapes.small,
//                                onClick = onAddNewRequestClicked
//                            ) {
//                                Text(text = stringResource(id = R.string.request_screen_no_request_found_user_manual_request_button))
//                            }
                            Button(
                                shape = MaterialTheme.shapes.small,
                                onClick = onAddNewRequestClicked
                            ) {
                                Text(text = stringResource(id = R.string.request_screen_add_request_button_text))
                            }
                        }
                    }
                }
            }
        }
    }
}