package com.glazovnet.glazovnetapp.presentation.posts.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.repository.PostsApiRepository
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postsApiRepository: PostsApiRepository,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<PostModel>())
    val state = _state.asStateFlow()
    val isAdmin = userAuthDataRepository.getIsUserAsAdmin() ?: false

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(stringResourceId = null, message = null, isLoading = true)
            }
            val result = postsApiRepository.getPostById(
                postId = postId,
                token = userAuthDataRepository.getLoginToken() ?: ""
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
}