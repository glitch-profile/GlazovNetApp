package com.glazovnet.glazovnetapp.core.presentation.navigationdrawer

sealed class NavigationDrawerState {
    data object Open : NavigationDrawerState()
    data object Closed : NavigationDrawerState()
}
