package com.glazovnet.glazovnetapp.presentation.posts.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.DesignedOutlinedTextField
import com.glazovnet.glazovnetapp.presentation.components.ImagePicker
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String?,
    onBackPressed: () -> Unit,
    onNeedToShowMessage: (Int) -> Unit,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val postTitle = viewModel.postTitle.collectAsState()
    val postText = viewModel.postText.collectAsState()
    val imageUri = viewModel.postImageUri.collectAsState()

    LaunchedEffect(null) {
        viewModel.loadPostData(postId)
        viewModel.messageStringResource.collectLatest {
            onNeedToShowMessage.invoke(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (state.value.data != null) stringResource(id = R.string.edit_post_screen_name)
                    else stringResource(id = R.string.add_post_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onBackPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.value.isLoading) {
                    LoadingIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.value.stringResourceId != null) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            text = stringResource(id = state.value.stringResourceId!!)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    DesignedOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = postTitle.value,
                        onTextEdit = { viewModel.updatePostTitle(it) },
                        placeholder = "Post title",
                        minLines = 2,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DesignedOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = postText.value,
                        onTextEdit = { viewModel.updatePostText(it) },
                        placeholder = "Post text",
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(horizontal = 16.dp),
                        imageUri = imageUri.value,
                        onNewImageSelected = { viewModel.updatePostImageUri(it) }
                    )
                }
            }
            BottomActionBar(
                onClearButtonClick = {
                    viewModel.updatePostTitle(state.value.data?.title ?: "")
                    viewModel.updatePostText(state.value.data?.text ?: "")
                    viewModel.updatePostImageUri(state.value.data?.image?.imageUrl?.toUri())
                },
                onConfirmButtonClick = { viewModel.uploadPost(context) },
                isConfirmButtonEnabled = !state.value.isLoading
                        //&& !state.value.isUploading
                        && postTitle.value.isNotBlank()
                        && postText.value.isNotBlank()
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    onClearButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { onClearButtonClick.invoke() }
            ) {
                Text(text = stringResource(id = R.string.reusable_text_clear))
            }
            Spacer(modifier = Modifier.width(24.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onConfirmButtonClick.invoke() },
                enabled = isConfirmButtonEnabled
            ) {
                Text(text = stringResource(id = R.string.reusable_text_save))
            }
        }
    }
}