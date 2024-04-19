package com.glazovnet.glazovnetapp.posts.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
import com.glazovnet.glazovnetapp.posts.domain.repository.PostsApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsListViewModel @Inject constructor(
    private val postsApiRepository: PostsApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<PostModel>>())
    val state = _state.asStateFlow()

    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    val isEmployeeWithNewsRole = userAuthDataRepository.getEmployeeHasRole(EmployeeRoles.NEWS)

    fun getAllPosts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    stringResourceId = null,
                    message = null
                )
            }
            when (val result = postsApiRepository.getAllPosts(loginToken)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            data = result.data,
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            data = null,
                            stringResourceId = result.stringResourceId,
                            message = result.message
                        )
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

}