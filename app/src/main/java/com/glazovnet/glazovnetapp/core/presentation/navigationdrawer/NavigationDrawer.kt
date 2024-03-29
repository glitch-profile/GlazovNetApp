package com.glazovnet.glazovnetapp.core.presentation.navigationdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R

private val items = listOf<NavigationDrawerItemData>(
    NavigationDrawerItemData(
        stringResource = R.string.posts_list_screen_name,
        icon = Icons.Default.DateRange,
        route = "posts-graph"
    ),
    NavigationDrawerItemData(
        stringResource = R.string.request_screen_name,
        icon = Icons.Default.Build,
        route = "support-graph"
    ),
    NavigationDrawerItemData(
        stringResource = R.string.tariffs_list_screen_name,
        icon = Icons.Default.Menu,
        route = "tariffs-graph"
    ),
    NavigationDrawerItemData(
        stringResource = R.string.announcement_list_screen_name,
        icon = Icons.Default.Notifications,
        route = "announcements-graph"
    ),
    NavigationDrawerItemData(
        stringResource = R.string.notifications_settings_screen_name,
        icon = Icons.Default.Notifications,
        route = "notifications-graph"
    )
)
private val logoutItem = NavigationDrawerItemData(
    stringResource = R.string.util_nav_drawer_logout,
    icon = Icons.Default.ExitToApp,
    route = "login-screen"
)

@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    onNavigateOnHomeScreen: (route: String) -> Unit,
    onNavigateOnMainScreen: (route: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        var selectedItemIndexed by rememberSaveable {
            mutableIntStateOf(0)
        }
        items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(62.dp),
                data = item,
                onClick = {
                    onNavigateOnHomeScreen.invoke(it)
                    selectedItemIndexed = index
                },
                isSelected = index == selectedItemIndexed
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        NavigationDrawerItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(62.dp),
            data = logoutItem,
            onClick = {
                onNavigateOnMainScreen.invoke(it)
            },
            isSelected = false
        )

    }
}