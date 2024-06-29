package com.glazovnet.glazovnetapp.personalaccount.presentation.addfunds

sealed class AddFundsScreenState() {
    data object EnteringInfo: AddFundsScreenState()
    data object CheckingInfo: AddFundsScreenState()
    data object Loading: AddFundsScreenState()
    data object Success: AddFundsScreenState()
    data object Error: AddFundsScreenState()

}
