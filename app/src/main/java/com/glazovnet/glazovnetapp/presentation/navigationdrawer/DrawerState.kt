package com.glazovnet.glazovnetapp.presentation.navigationdrawer

sealed class NavigationDrawerState {
    data object Open : NavigationDrawerState()
    data object Closed : NavigationDrawerState()
}
