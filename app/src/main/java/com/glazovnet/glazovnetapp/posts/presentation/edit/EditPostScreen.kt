package com.glazovnet.glazovnetapp.posts.presentation.edit

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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.posts.presentation.components.FilledImagePicker
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
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        MediumTopAppBar(
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp),
                            text = stringResource(id = R.string.edit_post_post_title_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            value = postTitle.value,
                            onValueChange = { viewModel.updatePostTitle(it) },
                            placeholder = stringResource(id = R.string.edit_post_post_title_placeholder),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp),
                            text = stringResource(id = R.string.edit_post_post_text_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            value = postText.value,
                            onValueChange = { viewModel.updatePostText(it) },
                            placeholder = stringResource(id = R.string.edit_post_post_text_placeholder),
                            minLines = 5,
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp),
                            text = stringResource(id = R.string.edit_post_post_image_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = (Modifier.height(8.dp)))
                        FilledImagePicker(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(horizontal = 16.dp),
                            imageUri = imageUri.value,
                            onNewImageSelected = { viewModel.updatePostImageUri(it) }
                        )
                    }
                }
            }
            BottomActionBar(
                onConfirmButtonClick = { viewModel.uploadPost(context) },
                isConfirmButtonEnabled = !state.value.isLoading
                        && !state.value.isUploading
                        && postTitle.value.isNotBlank()
                        && postText.value.isNotBlank()
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
//        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
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
                onClick = { onConfirmButtonClick.invoke() },
                enabled = isConfirmButtonEnabled
            ) {
                Text(text = stringResource(id = R.string.reusable_text_save))
            }
        }
    }
}