package com.glazovnet.glazovnetapp.presentation.posts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.domain.usecase.PostsUseCase
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsListViewModel @Inject constructor(
    private val postsUseCase: PostsUseCase,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<PostModel>>())
    val state = _state.asStateFlow()
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    init {
        _isAdmin.update { userAuthDataRepository.getIsUserAsAdmin() ?: false }
    }

    fun getAllPosts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    stringResourceId = null,
                    message = null
                )
            }
            when (val result = postsUseCase.getAllPosts()) {
                is Resource.Success -> {
                    _state.update {
                        if (result.data != null) {
                            it.copy(
                                data = result.data,
                                isLoading = false
                            )
                        } else {
                            it.copy(
                                isLoading = false,
                                stringResourceId = result.stringResourceId,
                                message = result.message
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            stringResourceId = result.stringResourceId,
                            message = result.message
                        )
                    }
                }
            }
        }
    }

}