package com.glazovnet.glazovnetapp.announcements.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.announcements.domain.model.AnnouncementModel
import com.glazovnet.glazovnetapp.announcements.domain.repository.AnnouncementsApiRepository
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementsListViewModel @Inject constructor(
    private val announcementsApiRepository: AnnouncementsApiRepository,
    userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<List<AnnouncementModel>>())
    val state = _state.asStateFlow()

    val isEmployeeWithRole = userAuthDataRepository.getEmployeeHasRole(EmployeeRoles.ANNOUNCEMENTS)
    private val loginToken = userAuthDataRepository.getLoginToken() ?: ""
    private val clientId = userAuthDataRepository.getAssociatedClientId() ?: ""
    private val employeeId = userAuthDataRepository.getAssociatedEmployeeId() ?: ""

    fun loadAnnouncements() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, message = null, stringResourceId = null)
            }
            val result = if (isEmployeeWithRole) {
                announcementsApiRepository.getAllAnnouncements(
                    token = loginToken,
                    employeeId = employeeId
                )
            } else {
                announcementsApiRepository.getAnnouncementsForClient(
                    token = loginToken,
                    clientId = clientId
                )
            }
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(data = result.data)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(message = result.message, stringResourceId = result.stringResourceId)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

}