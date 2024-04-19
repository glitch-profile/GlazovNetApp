package com.glazovnet.glazovnetapp.posts.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingIndicator
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    postId: String,
    onNavigationButtonClicked: () -> Unit,
    onPostDeleted: () -> Unit,
    onEditPostButtonClicked: (postId: String) -> Unit,
    viewModel: PostDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val isEmployeeWithRole = viewModel.isEmployeeWithNewsRole

    LaunchedEffect(key1 = null) {
        viewModel.loadPost(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.post_details_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonClicked.invoke()
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
            if (state.value.isLoading && state.value.data != null) {
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
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = state.value.data!!.title
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = buildAnnotatedString {
                            append(state.value.data!!.creationDateTime!!.getLocalizedOffsetString())
                            state.value.data!!.lastEditDate?.let { editDate ->
                                append(" | ")
                                append(stringResource(id = R.string.post_details_screen_updated_text_short) + " ")
                                append(editDate.getLocalizedOffsetString())
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = state.value.data!!.text
                    )
                    state.value.data!!.image?.let { image ->
                        val imageAspectRatio = image.imageWidth.toFloat() / image.imageHeight.toFloat()
                        Spacer(modifier = Modifier.height(10.dp))
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
//                                .clip(MaterialTheme.shapes.medium)
                                .fillMaxWidth()
                                .aspectRatio(imageAspectRatio),
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.Medium
                        )
                    }
                }
                if (isEmployeeWithRole) {
                    BottomActionBar(
                        isButtonsEnabled = !state.value.isUploading,
                        onEditPostButtonClicked = { onEditPostButtonClicked.invoke(state.value.data!!.id) },
                        onDeletePostButtonClicked = {
                            viewModel.deletePost(
                                postId = state.value.data!!.id,
                                onPostDeleted = onPostDeleted
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    isButtonsEnabled: Boolean,
    onEditPostButtonClicked: () -> Unit,
    onDeletePostButtonClicked: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                enabled = isButtonsEnabled,
                onClick = { onEditPostButtonClicked.invoke() }
            ) {
                Text(text = stringResource(id = R.string.reusable_text_edit))
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                enabled = isButtonsEnabled,
                onClick = onDeletePostButtonClicked
            ) {
                Text(
                    text = stringResource(id = R.string.reusable_text_delete),
                )
            }
        }
    }
}