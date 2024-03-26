package com.glazovnet.glazovnetapp.supportrequests.presentation.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.supportrequests.domain.model.MessageModel
import com.glazovnet.glazovnetapp.supportrequests.domain.model.RequestStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    requestId: String,
    onNavigationButtonPressed: () -> Unit,
    onNeedToShowMessage: (Int) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val requestStatus = viewModel.requestStatus.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.initChatSocket(requestId)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.messageResourceStringChannel.collectLatest {
            onNeedToShowMessage.invoke(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.request_chat_screen_name)
                )
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
            }
        )
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
        } else {
            if (state.value.data != null) {
                MessagesList(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    messages = state.value.data!!
                )
                InputField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isEnabled = requestStatus.value != RequestStatus.Solved,
                    onMessageSend = {
                        viewModel.sendMessage(it)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessagesList(
    modifier: Modifier = Modifier,
    messages: List<MessageModel>,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            if (lazyListState.firstVisibleItemIndex <= 3) {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    if (messages.isEmpty()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.request_chat_no_messages_found),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true,
            state = lazyListState
        ) {
            for (index in messages.indices) {
                val previousSenderId = messages.getOrNull(index + 1)?.senderId
                val isSameSender = previousSenderId == messages[index].senderId
                val topPadding = if (isSameSender) 0.dp else 4.dp

                item(
                    key = messages[index].id
                ) {
                    with(messages[index]) {
                        ChatBubble(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = topPadding,
                                    bottom = 4.dp
                                )
                                .animateItemPlacement(
                                    animationSpec = tween(durationMillis = 300)
                                ),
                            senderName = this.senderName,
                            text = this.text,
                            timestamp = this.timestamp!!,
                            isOwnMessage = this.isOwnMessage,
                            isSameSender = isSameSender,
                            maxBubbleWidth = 280.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onMessageSend: (message: String) -> Unit,
) {
    var messageText by remember {
        mutableStateOf("")
    }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            if (isEnabled) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize()
                ) {
                    FilledTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = messageText,
                        onValueChange = {
                            messageText = it
                        },
                        placeholder = stringResource(id = R.string.request_chat_message_placeholder_text),
                        maxLines = 6,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                onMessageSend.invoke(messageText)
                                messageText = ""
                            }
                        ),
                        background = Color.Transparent
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    onMessageSend.invoke(messageText)
                    messageText = ""
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Send"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.request_chat_messages_not_allowed_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.request_chat_messages_not_allowed_description),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}