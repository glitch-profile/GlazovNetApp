package com.glazovnet.glazovnetapp.personalaccount.presentation.balancehistory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BalanceHistoryScreen(
    onBackButtonPressed: () -> Unit,
    viewModel: BalanceHistoryViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val groupedTransactions = viewModel.groupedTransactions.collectAsState()

    LaunchedEffect(null) {
        viewModel.loadTransactions()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MediumTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.transactions_screen_name))
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onBackButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                AnimatedVisibility(visible = !state.value.isLoading) {
                    IconButton(onClick = {
                        if (!state.value.isLoading) viewModel.loadTransactions()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update page"
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            if (state.value.isLoading && state.value.data == null) {
                LoadingComponent()
            } else if (state.value.stringResourceId != null) {
                RequestErrorScreen(
                    messageStringResource = state.value.stringResourceId,
                    additionalMessage = state.value.message
                )
            } else if (state.value.data != null) {
                if (groupedTransactions.value != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        content = {
                            groupedTransactions.value!!.keys.forEach { dateTimestamp ->
                                stickyHeader {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                                        text = dateTimestamp.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth()
                                            .clip(MaterialTheme.shapes.small)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    3.dp
                                                )
                                            )
                                    ) {
                                        groupedTransactions.value!![dateTimestamp]!!.forEach { transaction ->
                                            TransactionCard(
                                                transaction = transaction,
                                                onCardClicked = {}
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                } else {
                    // TODO: add no transactions found screen
                }

            }
        }
    }

}