package com.glazovnet.glazovnetapp.presentation.supportscreen.requestdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    requestId: String,
    onNavigationButtonPressed: () -> Unit,
    onOpenChatButtonPressed: (String) -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val isAdmin = viewModel.isAdmin

    LaunchedEffect(key1 = null) {
        viewModel.loadRequestDetails(requestId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.request_details_screen_name))
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
            actions = {
                AnimatedVisibility(visible = !state.value.isLoading) {
                    IconButton(onClick = {
                        if (!state.value.isLoading) viewModel.loadRequestDetails(requestId)
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Update page")
                    }
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            } else if (state.value.data != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = state.value.data!!.title,
                        maxLines = 10
                    )
                    RequestText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        requestText = state.value.data!!.description
                    )
                    Divider(Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun RequestText(
    modifier: Modifier = Modifier,
    requestText: String
) {
    Row(
        modifier = modifier
    ) {
        var textFieldSize by remember {
            mutableStateOf(Size.Zero)
        }
        Box(
            modifier = Modifier
                .height(
                    with(LocalDensity.current) {
                        textFieldSize.height.toDp()
                    }
                )
                .width(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    textFieldSize = layoutCoordinates.size.toSize()
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = requestText,
            maxLines = 20
        )
    }
}