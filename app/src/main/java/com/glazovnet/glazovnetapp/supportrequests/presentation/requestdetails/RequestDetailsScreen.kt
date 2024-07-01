package com.glazovnet.glazovnetapp.supportrequests.presentation.requestdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import com.glazovnet.glazovnetapp.core.presentation.components.AdditionalTextInfo
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import com.glazovnet.glazovnetapp.supportrequests.domain.model.SupportRequestModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    requestId: String,
    onNavigationButtonPressed: () -> Unit,
    onOpenChatButtonPressed: (String) -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val creatorInfo = viewModel.creatorInfo.collectAsState()

    val isEmployeeWithRole = viewModel.isEmployeeWithRole
    val clientId = viewModel.clientId
    val employeeId = viewModel.employeeId

    LaunchedEffect(key1 = null) {
        viewModel.loadRequestInfo(requestId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                        if (!state.value.isLoading) viewModel.loadRequestInfo(requestId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update page"
                        )
                    }
                }
            }
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
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
                    if (isEmployeeWithRole) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = stringResource(id = R.string.request_details_screen_creator_info_title)
                        )
                        AdditionalTextInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            title = stringResource(id = R.string.request_details_screen_creator_account_number),
                            text = creatorInfo.value?.accountNumber ?: stringResource(id = R.string.reusable_text_loading)
                        )
                        AdditionalTextInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            title = stringResource(id = R.string.request_details_screen_creator_name),
                            text = creatorInfo.value?.fullName ?: stringResource(id = R.string.reusable_text_loading)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = stringResource(id = R.string.request_details_screen_additional_info_text)
                    )
                    AdditionalTextInfo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        title = stringResource(id = R.string.request_details_screen_creation_date_text),
                        text = state.value.data!!.creationDate.getLocalizedOffsetString()
                    )
                    if (state.value.data!!.reopenDate != null) {
                        AdditionalTextInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            title = stringResource(id = R.string.request_details_screen_reopen_date_text),
                            text = state.value.data!!.reopenDate!!.getLocalizedOffsetString()
                        )
                    }
                    AdditionalTextInfo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        title = stringResource(id = R.string.request_details_screen_request_status_text),
                        text = stringResource(id = state.value.data!!.status.stringResourceRequestStatus)
                    )
                    if (isEmployeeWithRole) {
                        AdditionalTextInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            title = stringResource(id = R.string.request_details_screen_assigned_supporter_text),
                            text = when (state.value.data!!.associatedSupportId) {
                                null -> stringResource(id = R.string.request_details_screen_assigned_supporter_no)
                                employeeId -> stringResource(id = R.string.request_details_screen_assigned_supporter_you)
                                else -> stringResource(id = R.string.request_details_screen_assigned_supporter_someone)
                            }
                        )
                    }
//                    Divider(Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    ButtonsMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        onOpenChatButtonPressed = {
                            onOpenChatButtonPressed.invoke(state.value.data?.id ?: "")
                        },
                        onAssignSupporterButtonPressed = {
                            viewModel.assignSupporter()
                        },
                        onCloseRequestButtonPressed = {
                            viewModel.closeRequest()
                        },
                        onReopenRequestButtonClicked = {
                            viewModel.reopenRequest()
                        },
                        isButtonsEnabled = !state.value.isLoading && !state.value.isUploading,
                        clientId = clientId,
                        isEmployeeWithRole = isEmployeeWithRole,
                        employeeId = employeeId,
                        request = state.value.data!!
                    )
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

@Composable
private fun ButtonsMenu(
    modifier: Modifier = Modifier,
    onOpenChatButtonPressed: () -> Unit,
    onAssignSupporterButtonPressed: () -> Unit,
    onCloseRequestButtonPressed: () -> Unit,
    onReopenRequestButtonClicked: () -> Unit,
    isButtonsEnabled: Boolean,
    isEmployeeWithRole: Boolean,
    clientId: String,
    employeeId: String,
    request: SupportRequestModel
) {
    Column(
        modifier = modifier
    ) {
        OutlinedButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            onClick = {
                onOpenChatButtonPressed.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.request_details_screen_open_chat_text))
        }
        if (clientId == request.creatorClientId && request.status == RequestStatus.Solved) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    onReopenRequestButtonClicked.invoke()
                },
                enabled = isButtonsEnabled
            ) {
                Text(text = stringResource(id = R.string.request_details_screen_reopen_request_text))
            }
        }
        if (isEmployeeWithRole) {
            Spacer(modifier = Modifier.height(8.dp))
            if (request.associatedSupportId == null) {
                Button(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        onAssignSupporterButtonPressed.invoke()
                    },
                    enabled = isButtonsEnabled
                ) {
                    Text(text = stringResource(id = R.string.request_details_screen_assign_text))
                }
            } else if (request.status == RequestStatus.Active
                && request.associatedSupportId == employeeId
            ) {
                Button(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        onCloseRequestButtonPressed.invoke()
                    },
                    enabled = isButtonsEnabled
                ) {
                    Text(text = stringResource(id = R.string.request_details_screen_mark_as_solved_text))
                }
            }
        }
    }
}