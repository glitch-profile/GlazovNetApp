package com.glazovnet.glazovnetapp.announcements.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement
import com.glazovnet.glazovnetapp.announcements.domain.model.AnnouncementModel
import com.glazovnet.glazovnetapp.announcements.domain.repository.AnnouncementsApiRepository
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateAnnouncementViewModel @Inject constructor(
    private val announcementsApiRepository: AnnouncementsApiRepository,
    private val userAuthDataRepository: LocalUserAuthDataRepository
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<Unit>())
    val state = _state.asStateFlow()
    private val _citiesList = MutableStateFlow(ScreenState<List<String>>(emptyList()))
    val citiesList = _citiesList.asStateFlow()
    private val _addressesState = MutableStateFlow<List<AddressState>>(emptyList())
    val addressesState = _addressesState.asStateFlow()
    private val _selectedAddresses = MutableStateFlow<List<AddressFilterElement>>(emptyList())
    val selectedAddresses = _selectedAddresses.asStateFlow()

    private val _announcementTitle = MutableStateFlow("")
    val announcementTitle = _announcementTitle.asStateFlow()
    private val _announcementText = MutableStateFlow("")
    val announcementText = _announcementText.asStateFlow()

    private val _messageStringResource = Channel<Int>()
    val messageStringResource = _messageStringResource.receiveAsFlow()

    private val _isSheetOpen = MutableStateFlow(false)
    val isDetailsSheetOpen = _isSheetOpen.asStateFlow()

    private val _citiesSearchText = MutableStateFlow("")
    private val citiesSearchJob = _citiesSearchText
        .debounce(300)
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .onEach {
            searchForAddresses(
                _citiesSearchText.value,
                _streetsSearchText.value
            )
        }
        .launchIn(viewModelScope)

    private val _streetsSearchText = MutableStateFlow("")
    private val streetsSearchJob = _streetsSearchText
        .debounce(300)
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)
        .onEach {
            searchForAddresses(
                _citiesSearchText.value,
                _streetsSearchText.value
            )
        }
        .launchIn(viewModelScope)

    init {
        loadCitiesList()
    }

    fun showBottomSheet() {
        _isSheetOpen.update { true }
    }
    fun hideBottomBar() {
        _isSheetOpen.update { false }
    }

    fun updateAnnouncementTitle(title: String) {
        _announcementTitle.update { title }
    }

    fun updateAnnouncementText(text: String) {
        _announcementText.update { text }
    }

    fun updateSearch(
        citySearch: String,
        streetSearch: String
    ) {
        viewModelScope.launch {
            _citiesSearchText.update { citySearch }
            _streetsSearchText.update { streetSearch }
        }
    }

    fun changeSelectionOfAddressElement(
        addressState: AddressState
    ) {
        val updatedAddressState = addressState.copy(
            isSelected = !addressState.isSelected
        )
        val addressesList = addressesState.value.toMutableList().apply {
            val index = this.indexOf(addressState)
            if (index != -1) {
                this[index] = updatedAddressState
            }
        }
        _addressesState.update { addressesList }

        val selectedAddressesList = selectedAddresses.value.toMutableList().apply {
            if (updatedAddressState.isSelected) {
                this.add(updatedAddressState.address)
            } else {
                this.remove(updatedAddressState.address)
            }
        }
        _selectedAddresses.update { selectedAddressesList }
    }

    fun createAnnouncement() {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val announcementToCreate = AnnouncementModel(
                id = "",
                addresses = selectedAddresses.value,
                title = announcementTitle.value,
                text = announcementText.value
            )
            val result = announcementsApiRepository.addAnnouncement(
                announcementModel = announcementToCreate,
                token = userAuthDataRepository.getLoginToken() ?: ""
            )
            when (result) {
                is Resource.Success -> {
                    _messageStringResource.send(R.string.edit_post_add_result_success) //TODO
                }
                is Resource.Error -> {
                    _messageStringResource.send(result.stringResourceId!!)
                }
            }
        }
    }

    private fun searchForAddresses(
        city: String,
        street: String
    ) {
        viewModelScope.launch {
            if (city.isNotBlank() && street.isNotBlank()) {
                val addresses = announcementsApiRepository.getAddresses(
                    cityName = city,
                    streetName = street,
                    token = userAuthDataRepository.getLoginToken() ?: ""
                )
                _addressesState.update {
                    when (addresses) {
                        is Resource.Success ->
                            addresses.data!!.map {
                                AddressState(
                                    address = it,
                                    isSelected = checkIfAddressAlreadySelected(it)
                                )
                            }
                        is Resource.Error ->
                            emptyList()
                    }
                }
            } else _addressesState.update {
                selectedAddresses.value.reversed().map {
                    AddressState(address = it, isSelected = true)
                }
            }
        }
    }

    private fun checkIfAddressAlreadySelected(
        address: AddressFilterElement
    ): Boolean {
        val isAddressSelected = selectedAddresses.value.any { selectedAddress ->
            selectedAddress == address
        }
        return isAddressSelected
    }

    private fun loadCitiesList() {
        viewModelScope.launch {
            _citiesList.update {
                it.copy(
                    isLoading = true,
                    stringResourceId = null,
                    message = null
                )
            }
            val result = announcementsApiRepository.getCitiesWithName(
                cityName = "",
                token = userAuthDataRepository.getLoginToken() ?: ""
            )
            _citiesList.update {
                when (result) {
                    is Resource.Success -> {
                        it.copy(
                            data = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        it.copy(
                            stringResourceId = result.stringResourceId,
                        )
                    }
                }
            }
            _citiesList.update { it.copy(isLoading = false) }
        }
    }

}