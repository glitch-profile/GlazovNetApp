package com.glazovnet.glazovnetapp.presentation.posts.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator

@Composable
fun PostsListScreen(
    navController: NavController,
    viewModel: PostsListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

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
        } else {
            if (state.value.stringResourceId != null) {
                Text(text = stringResource(id = state.value.stringResourceId!!))
            }
            if (state.value.data != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(
                            items = state.value.data!!,
                            key = { it.id }
                        ) {
                            PostCard(
                                modifier = Modifier.fillMaxWidth(),
                                post = it,
                                onClick = {
                                    navController.navigate("edit-posts-screen?postId=${it.id}")
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