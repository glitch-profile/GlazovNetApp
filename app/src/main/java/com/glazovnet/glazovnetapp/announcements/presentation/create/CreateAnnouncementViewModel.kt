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
    private val _addressesState = MutableStateFlow<List<AddressFilterElement>>(emptyList())
    val addressesState = _addressesState.asStateFlow()
    private val _selectedAddresses = MutableStateFlow<List<AddressFilterElement>>(emptyList())
    val selectedAddresses = _selectedAddresses.asStateFlow()

    private val _announcementTitle = MutableStateFlow("")
    val announcementTitle = _announcementTitle.asStateFlow()
    private val _announcementText = MutableStateFlow("")
    val announcementText = _announcementText.asStateFlow()

    private val _messageStringResource = Channel<Int>()
    val messageStringResource = _messageStringResource.receiveAsFlow()

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

    fun updateAnnouncementTitle(title: String) {
        _announcementTitle.update { title }
    }

    fun updateAnnouncementText(text: String) {
        _announcementText.update { text }
    }

    fun changeSelectionOfAddressElement(
        addressFilterElement: AddressFilterElement
    ) {
        val addressesList = addressesState.value.toMutableList()
        val addressIndex = addressesList.indexOfFirst { it == addressFilterElement }
        if (addressIndex != -1) {
            addressesList[addressIndex] = addressFilterElement.copy(
                isSelected = !addressFilterElement.isSelected
            )
            _addressesState.update { addressesList }
        }

        val selectedAddresses = selectedAddresses.value.toMutableList()
        if (addressFilterElement.isSelected) {
            selectedAddresses.remove(addressFilterElement)
        } else {
            selectedAddresses.add(
                addressFilterElement.copy(
                    isSelected = true
                )
            )
        }
        _selectedAddresses.update {
            selectedAddresses
        }
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
                            addresses.data!!.map { checkIfAddressAlreadySelected(it) }
                        is Resource.Error ->
                            emptyList()
                    }
                }
            } else _addressesState.update {
                selectedAddresses.value.reversed()
            }
        }
    }

    private fun checkIfAddressAlreadySelected(
        address: AddressFilterElement
    ): AddressFilterElement {
        val isAddressSelected = selectedAddresses.value.any { selectedAddress ->
            selectedAddress.city == address.city
                    && selectedAddress.street == address.street
                    && selectedAddress.houseNumber == address.houseNumber
        }
        return if (isAddressSelected) {
            address.copy(isSelected = true)
        } else address
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
                            message = result.message
                        )
                    }
                }
            }
        }
    }

}