package com.glazovnet.glazovnetapp.core.presentation.navigationdrawer

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationDrawerItemData(
    val stringResource: Int,
    val icon: ImageVector,
    val route: String
)
