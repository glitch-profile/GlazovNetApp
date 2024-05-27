package com.glazovnet.glazovnetapp.innerposts.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.MessageNotificationState
import com.glazovnet.glazovnetapp.innerposts.domain.repository.InnerPostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateInnerPostViewModel @Inject constructor(
    private val innerPostsRepository: InnerPostsRepository,
    authDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _postTitle = MutableStateFlow("")
    val postTitle = _postTitle.asStateFlow()
    private val _postText = MutableStateFlow("")
    val postText = _postText.asStateFlow()

    private val _isPostUploading = MutableStateFlow(false)
    val isPostUploading = _isPostUploading.asStateFlow()

    private val userToken = authDataRepository.getLoginToken() ?: ""

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    fun setPostTitle(title: String) {
        _postTitle.update { title }
    }
    fun setPostText(text: String) {
        _postText.update { text }
    }

    fun createPost() {
        viewModelScope.launch {
            _isPostUploading.update { true }
            val result = innerPostsRepository.addInnerPost(
                token = userToken,
                title = postTitle.value.trim(),
                text = postText.value.trim()
            )
            if (result is Resource.Success) {
                showMessage(
                    titleRes = R.string.edit_post_add_result_success_title,
                    messageRes = R.string.edit_post_add_result_success_message
                )
                setPostTitle("")
                setPostText("")
            } else {
                showMessage(
                    titleRes = R.string.reusable_unexpected_error_occurred,
                    messageRes = result.stringResourceId!!
                )
            }
            _isPostUploading.update { false }
        }
    }

    private fun showMessage(titleRes: Int, messageRes: Int) {
        messageScope.coroutineContext.cancelChildren()
        messageScope.launch {
            _messageState.update {
                MessageNotificationState(
                    enabled = true, titleResource = titleRes, additionTextResource = messageRes
                )
            }
            delay(3000L)
            _messageState.update { it.copy(enabled = false) }
        }
    }

}