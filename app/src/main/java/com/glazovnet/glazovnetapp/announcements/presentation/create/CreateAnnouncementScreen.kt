package com.glazovnet.glazovnetapp.announcements.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnouncementScreen(
    onNavigationButtonPressed: () -> Unit,
    onNeedToShowMessage: (Int) -> Unit,
    viewModel: CreateAnnouncementViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()
    val announcementTitle = viewModel.announcementTitle.collectAsState()
    val announcementText = viewModel.announcementText.collectAsState()

    val citiesList = viewModel.citiesList.collectAsState()
    val foundAddresses = viewModel.addressesState.collectAsState()
    val selectedAddresses = viewModel.selectedAddresses.collectAsState()

    val isSheetOpen = viewModel.isDetailsSheetOpen.collectAsState()

    LaunchedEffect(null) {
        viewModel.messageStringResource.collectLatest {
            onNeedToShowMessage.invoke(it)
        }
    }

    AddressesSheet(
        isSheetOpen = isSheetOpen.value,
        citiesList = citiesList.value,
        onSearchTextChanged = { cityName, streetName ->
            viewModel.updateSearch(cityName, streetName)
        },
        addressesState = foundAddresses.value,
        onAddressClicked = {
            viewModel.changeSelectionOfAddressElement(it)
        },
        onDismiss = {
            viewModel.hideBottomBar()
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.add_announcement_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.add_announcement_screen_title_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilledTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = announcementTitle.value,
                    onValueChange = { viewModel.updateAnnouncementTitle(it) },
                    placeholder = stringResource(id = R.string.add_announcement_screen_title_placeholder),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.add_announcement_screen_text_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilledTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = announcementText.value,
                    onValueChange = { viewModel.updateAnnouncementText(it) },
                    placeholder = stringResource(id = R.string.add_announcement_screen_text_placeholder),
                    minLines = 5,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.add_announcement_screen_address_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.add_announcement_screen_address_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        viewModel.showBottomSheet()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add address"
                        )
                    }
                }
                Spacer(modifier = (Modifier.height(4.dp)))
                SelectedAddressesScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    addresses = selectedAddresses.value,
                    onAddressClicked = {}
                )
            }
            BottomActionBar(
                onConfirmButtonClick = { viewModel.createAnnouncement() },
                isConfirmButtonEnabled = true
            )
        }
    }
}

@Composable
private fun SelectedAddressesScreen(
    modifier: Modifier = Modifier,
    addresses: List<AddressFilterElement>,
    onAddressClicked: (AddressFilterElement) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (addresses.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(id = R.string.add_announcement_screen_no_addresses_selected_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(id = R.string.add_announcement_screen_no_addresses_selected_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                addresses.forEach {
                    AddressCard(
                        addressState = AddressState(
                            address = it,
                            isSelected = false
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = { onConfirmButtonClick.invoke() },
                enabled = isConfirmButtonEnabled
            ) {
                Text(text = stringResource(id = R.string.reusable_text_save))
            }
        }
    }
}