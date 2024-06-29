package com.glazovnet.glazovnetapp.personalaccount.presentation.addfunds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.core.presentation.components.AdditionalVerticalInfo
import com.glazovnet.glazovnetapp.core.presentation.components.FilledTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsBottomSheet(
    isSheetOpen: Boolean,
    accountNumber: String,
    clientAddress: String,
    onDismiss: () -> Unit,
    viewModel: AddFundsViewModel = hiltViewModel()
) {
    if (isSheetOpen) {
        val state = viewModel.state.collectAsState()
        val amount = viewModel.amount.collectAsState()
        val additionalNote = viewModel.additionalNote.collectAsState()

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            windowInsets = WindowInsets(0.dp),
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Add funds",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                when (state.value) {
                    AddFundsScreenState.EnteringInfo -> {
                        EnterInfoComponent(
                            amount = amount.value,
                            additionalText = additionalNote.value,
                            onAmountChanges = {
                                viewModel.setAmount(it)
                            },
                            onNoteChanges = {
                                viewModel.setAdditionalNote(it)
                            },
                            onConfirmClicked = {
                                viewModel.updateScreenState(AddFundsScreenState.CheckingInfo)
                            }
                        )
                    }
                    AddFundsScreenState.CheckingInfo -> {
                        CheckInfoComponent(
                            amount = amount.value,
                            additionalText = additionalNote.value,
                            accountNumber = accountNumber,
                            clientAddress = clientAddress,
                            onBackClicked = {
                                viewModel.updateScreenState(AddFundsScreenState.EnteringInfo)
                            },
                            onConfirmClicked = {
                                viewModel.makePayment()
                            }
                        )
                    }
                    AddFundsScreenState.Loading -> {
                        LoadingPaymentComponent()
                    }
                    AddFundsScreenState.Error -> {
                        TransactionResultComponent(isSuccess = false)
                    }
                    AddFundsScreenState.Success -> {
                        TransactionResultComponent(isSuccess = true)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EnterInfoComponent(
    amount: String,
    additionalText: String,
    onAmountChanges: (String) -> Unit,
    onNoteChanges: (String) -> Unit,
    onConfirmClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Fill in the required information",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .padding(start = 32.dp, end = 16.dp),
            text = "Payment amount",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        FilledTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = amount,
            onValueChange = onAmountChanges,
            placeholder = "Enter the payment amount",
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .padding(start = 32.dp, end = 16.dp),
            text = "Additional Info",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        FilledTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = additionalText,
            onValueChange = onNoteChanges,
            placeholder = "Enter additional message",
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onDone = { onConfirmClicked.invoke() }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small,
            onClick = onConfirmClicked,
            enabled = amount.toFloatOrNull() !== null && amount.toFloat() >= 10f
        ) {
            Text(text = "Continue")
        }
    }
}

@Composable
private fun CheckInfoComponent(
    amount: String,
    additionalText: String,
    accountNumber: String,
    clientAddress: String,
    onBackClicked: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Is this correct?",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        AdditionalVerticalInfo(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            title = String.format("%.2f", amount.toFloatOrNull() ?: 0.00) + " ₽",
            description = "Payment amount"
        )
        if (additionalText.isNotBlank()) {
            AdditionalVerticalInfo(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                title = additionalText,
                description = "Additional message"
            )
        }
        AdditionalVerticalInfo(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            title = accountNumber,
            description = "Account number"
        )
        AdditionalVerticalInfo(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            title = clientAddress,
            description = "Client address"
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .height(48.dp),
                shape = MaterialTheme.shapes.small,
                onClick = onBackClicked,
            ) {
                Text(text = "Change info")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = onConfirmClicked,
            ) {
                Text(text = "Confirm")
            }
        }
    }
}

@Composable
private fun LoadingPaymentComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Completing transaction...",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Processing transaction",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "This can take few seconds!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TransactionResultComponent(
    isSuccess: Boolean
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "Transaction results",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(12.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSuccess) "Thanks you!"
            else "Unable to complete transaction",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isSuccess) "Funds have been credited to your account"
            else "Please try again later...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
