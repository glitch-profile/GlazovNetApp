package com.glazovnet.glazovnetapp.presentation.announcements.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsListScreen(
    onNavigationButtonPressed: () -> Unit,
    onCreateNewAnnouncementClicked: () -> Unit,
    viewModel: AnnouncementsListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val isUserAdmin = viewModel.isUserAdmin

    LaunchedEffect(null) {
        viewModel.loadAnnouncements()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.announcement_list_screen_name))
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
                        if (!state.value.isLoading) viewModel.loadAnnouncements()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Update page")
                    }
                }
                if (isUserAdmin) {
                    IconButton(onClick = { onCreateNewAnnouncementClicked.invoke() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add new announcement")
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
            } else if (state.value.data != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    content = {
                        items(
                            items = state.value.data!!,
                            key = { it.id }
                        ) {
                            AnnouncementCard(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                announcement = it,
                                showAddressCount = isUserAdmin,
                                onCardClicked = {
                                    //TODO
                                }
                            )
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
