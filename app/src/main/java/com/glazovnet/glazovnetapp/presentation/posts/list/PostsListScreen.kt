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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R

@Composable
fun PostsListScreen(
    viewModel: PostsListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.value.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.reusable_text_loading),
                textAlign = TextAlign.Center
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
                                    TODO()
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