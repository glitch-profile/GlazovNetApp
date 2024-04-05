package com.glazovnet.glazovnetapp.announcements.presentation.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.announcements.presentation.components.SelectionChipButton
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesSheet(
    isSheetOpen: Boolean,
    citiesList: ScreenState<List<String>>,
    onSearchTextChanged: (cityName: String, streetName: String) -> Unit,
    addressesState: List<AddressState>,
    onAddressClicked: (addressState: AddressState) -> Unit,
    onDismiss: () -> Unit
) {
    var city by remember {
        mutableStateOf("")
    }
    var street by remember {
        mutableStateOf("")
    }
    var selectedCityIndex by remember {
        mutableIntStateOf(0)
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            windowInsets = WindowInsets(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(540.dp)
                    //.padding(bottom = 8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.add_announcement_screen_cities_title),
                    style = MaterialTheme.typography.titleMedium
                )
                if (citiesList.isLoading) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.reusable_text_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (citiesList.stringResourceId !== null) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = citiesList.stringResourceId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else citiesList.data?.let {cities ->
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            item { Spacer(modifier = Modifier.width(16.dp)) }
                            items(cities.size) { index ->
                                SelectionChipButton(
                                    modifier = Modifier
                                        .padding(end = 16.dp),
                                    text = cities[index],
                                    onClick = {
                                        selectedCityIndex = index
                                        city = cities[index]
                                        onSearchTextChanged(city, street)
                                    },
                                    isSelected = selectedCityIndex == index
                                )
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.add_announcement_screen_streets_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilledTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = street,
                    onValueChange = {
                        street = it
                        onSearchTextChanged(city, street)
                    },
                    placeholder = stringResource(id = R.string.add_announcement_screen_streets_placeholder),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.add_announcement_screen_found_addresses_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = {
                        items(
                            items = addressesState,
                            key = {"${it.address.city}${it.address.street}${it.address.houseNumber}"}
                        ) {
                            AddressCard(
                                addressState = it,
                                onClick = {
                                    onAddressClicked.invoke(it)
                                }
                            ) 
                        }
                        item { 
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                )
            }
        }
    }
}