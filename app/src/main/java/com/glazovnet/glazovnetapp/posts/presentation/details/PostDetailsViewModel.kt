package com.glazovnet.glazovnetapp.posts.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.MessageNotificationState
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
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
class PostDetailsViewModel @Inject constructor(
    private val postsApiRepository: PostsApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<PostModel>())
    val state = _state.asStateFlow()

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    private val employeeId = userAuthDataRepository.getAssociatedEmployeeId() ?: ""
    val isEmployeeWithNewsRole = userAuthDataRepository.getEmployeeHasRole(EmployeeRoles.NEWS)

    private val _messageState = MutableStateFlow(MessageNotificationState())
    val messageState = _messageState.asStateFlow()
    private val messageScope = CoroutineScope(Dispatchers.Default + Job())

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(stringResourceId = null, message = null, isLoading = true)
            }
            val result = postsApiRepository.getPostById(
                postId = postId,
                token = loginToken
            )
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(data = result.data) }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(message = result.message, stringResourceId =  result.stringResourceId)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun deletePost(postId: String, onPostDeleted: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val result = postsApiRepository.deletePostById(
                token = loginToken,
                postId = postId,
                employeeId = employeeId
            )
            when (result) {
                is Resource.Success -> {
                    onPostDeleted.invoke()
                }
                else -> {
                    showMessage(
                        titleRes = R.string.post_details_unable_to_delete_title,
                        messageRes = result.stringResourceId!!
                    )
                }
            }
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