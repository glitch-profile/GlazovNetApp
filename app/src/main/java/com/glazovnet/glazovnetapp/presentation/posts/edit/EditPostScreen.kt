package com.glazovnet.glazovnetapp.presentation.posts.edit

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.DesignedOutlinedTextField
import com.glazovnet.glazovnetapp.presentation.components.ImagePicker

@Composable
fun EditPostScreen(
    postId: String?,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    var postTitle by remember {
        mutableStateOf(state.value.data?.title ?: "")
    }
    var postText by remember {
        mutableStateOf(state.value.data?.text ?: "")
    }
    var postImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

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
            Spacer(modifier = Modifier.height(8.dp))
            if (state.value.stringResourceId != null) {
                Text(text = stringResource(id = state.value.stringResourceId!!))
                Spacer(modifier = Modifier.height(8.dp))
            }
            DesignedOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = postTitle,
                onTextEdit = {postTitle = it},
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
                text = postText,
                onTextEdit = {postText = it},
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
                imageUri = postImageUri,
                onNewImageSelected = {postImageUri = it}
            )
        }
    }
}