package com.glazovnet.glazovnetapp.presentation.posts.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsListScreen(
    onNavigationButtonPressed: () -> Unit,
    onNavigationToPostDetails: (postId: String) -> Unit,
    onNavigationToEditPostScreen: (postId: String?) -> Unit,
    viewModel: PostsListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val isUserAnAdmin = viewModel.isAdmin.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver{ _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getAllPosts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.posts_list_screen_name))
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
                            if (!state.value.isLoading) viewModel.getAllPosts()
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Update page")
                        }
                    }
                    if (isUserAnAdmin.value) {
                        IconButton(onClick = { onNavigationToEditPostScreen.invoke(null) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add new post")
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.value.isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            } else {
                if (state.value.stringResourceId != null) {
                    Text(text = stringResource(id = state.value.stringResourceId!!))
                }
                if (state.value.data != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        content = {
                            items(
                                items = state.value.data!!,
                                key = { it.id }
                            ) {
                                PostCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    post = it,
                                    onClick = {
                                        onNavigationToEditPostScreen.invoke(it.id) //TODO
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}