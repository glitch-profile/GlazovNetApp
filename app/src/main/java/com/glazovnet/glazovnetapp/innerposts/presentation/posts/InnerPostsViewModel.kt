package com.glazovnet.glazovnetapp.innerposts.presentation.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.innerposts.domain.model.InnerPostModel
import com.glazovnet.glazovnetapp.innerposts.domain.repository.InnerPostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class InnerPostsViewModel @Inject constructor(
    private val innerPostsRepository: InnerPostsRepository,
    authDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<List<InnerPostModel>>>())
    val state = _state.asStateFlow()

    private val userToken = authDataRepository.getLoginToken() ?: ""

//    init {
//        loadPosts()
//    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, stringResourceId = null, message = null)
            }
            when (val result = innerPostsRepository.getInnerPosts(token = userToken)) {
                is Resource.Success -> {
                    val currentDate = LocalDate.now(ZoneId.systemDefault())
                    //splitting data into today and earlier creation date lists
                    val todayPosts = result.data!!.filter { it.creationDate.toLocalDate() == currentDate }
                    val earlierPosts = result.data.toMutableList().apply {
                        this.removeAll(todayPosts)
                        toImmutableList()
                    }
                    val postLists = listOf(
                        todayPosts,
                        earlierPosts
                    )
                    _state.update {
                        it.copy(
                            data = postLists, //[0] for today, [1] for earlier posts
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