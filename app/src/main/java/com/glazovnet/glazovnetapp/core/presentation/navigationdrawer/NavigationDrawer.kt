package com.glazovnet.glazovnetapp.core.presentation.navigationdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R

@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    isUserAdmin: Boolean,
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
        NavigationDrawerItem(
            text = stringResource(id = R.string.posts_list_screen_name),
            icon = Icons.Default.DateRange,
            onClick = {
                onNavigateOnHomeScreen.invoke("posts-graph")
                selectedItemIndexed = 0
            },
            isSelected = selectedItemIndexed == 0
        )
        NavigationDrawerItem(
            text = stringResource(id = R.string.request_screen_name),
            icon = Icons.Default.Build,
            onClick = {
                onNavigateOnHomeScreen.invoke("support-graph")
                selectedItemIndexed = 1
            },
            isSelected = selectedItemIndexed == 1
        )
        NavigationDrawerItem(
            text = stringResource(id = R.string.tariffs_list_screen_name),
            icon = Icons.Default.Menu,
            onClick = {
                onNavigateOnHomeScreen.invoke("tariffs-graph")
                selectedItemIndexed = 2
            },
            isSelected = selectedItemIndexed == 2
        )
        NavigationDrawerItem(
            text = stringResource(id = R.string.announcement_list_screen_name),
            icon = Icons.Default.Notifications,
            onClick = {
                onNavigateOnHomeScreen.invoke("announcements-graph")
                selectedItemIndexed = 3
            },
            isSelected = selectedItemIndexed == 3
        )
        if (isUserAdmin) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.service_screen_name),
                icon = Icons.Default.Lock,
                onClick = {
                    onNavigateOnHomeScreen.invoke("posts-graph")
                    selectedItemIndexed = 4
                },
                isSelected = selectedItemIndexed == 4
            )
        }
        NavigationDrawerItem(
            text = stringResource(id = R.string.settings_screen_name),
            icon = Icons.Default.Settings,
            onClick = {
                onNavigateOnHomeScreen.invoke("settings-graph")
                selectedItemIndexed = 5
            },
            isSelected = selectedItemIndexed == 5
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        NavigationDrawerItem(
            text = stringResource(id = R.string.util_nav_drawer_logout),
            icon = Icons.Default.ExitToApp,
            onClick = {
                onNavigateOnMainScreen.invoke("login-screen")
            },
            isSelected = false
        )

    }
}