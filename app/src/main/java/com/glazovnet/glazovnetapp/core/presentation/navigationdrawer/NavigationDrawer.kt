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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles

@Composable
fun NavigationDrawer(
    currentScreenRoute: String,
    isUserIsClient: Boolean,
    isUserIsEmployee: Boolean,
    employeeRoles: List<EmployeeRoles>,
    onNavigateOnHomeScreen: (route: String) -> Unit,
    onNavigateOnMainScreen: (route: String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        NavigationDrawerItem(
            text = stringResource(id = R.string.posts_list_screen_name),
            icon = Icons.Default.DateRange,
            onClick = {
                onNavigateOnHomeScreen.invoke("posts-graph")
            },
            isSelected = currentScreenRoute == "posts-graph"
        )
        if (isUserIsClient || isUserIsEmployee) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.personal_account_screen_name),
                icon = Icons.Default.Person,
                onClick = {
                    onNavigateOnHomeScreen.invoke("personal-account-graph")
                },
                isSelected = currentScreenRoute == "personal-account-graph"
            )
        }
        if (isUserIsClient || employeeRoles.contains(EmployeeRoles.SUPPORT_CHAT)) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.request_screen_name),
                icon = Icons.Default.Build,
                onClick = {
                    onNavigateOnHomeScreen.invoke("support-graph")
                },
                isSelected = currentScreenRoute == "support-graph"
            )
        }
        NavigationDrawerItem(
            text = stringResource(id = R.string.tariffs_list_screen_name),
            icon = Icons.Default.Menu,
            onClick = {
                onNavigateOnHomeScreen.invoke("tariffs-graph")
            },
            isSelected = currentScreenRoute == "tariffs-graph"
        )
        if (isUserIsClient) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.services_screen_name),
                icon = Icons.Default.List,
                onClick = {
                    onNavigateOnHomeScreen.invoke("services-graph")
                },
                isSelected = currentScreenRoute == "services-graph"
            )
        }
        if (isUserIsClient || employeeRoles.contains(EmployeeRoles.ANNOUNCEMENTS)) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.announcement_list_screen_name),
                icon = Icons.Default.Notifications,
                onClick = {
                    onNavigateOnHomeScreen.invoke("announcements-graph")
                },
                isSelected = currentScreenRoute == "announcements-graph"
            )
        }
        if (isUserIsEmployee) {
            NavigationDrawerItem(
                text = stringResource(id = R.string.inner_posts_screen_name),
                icon = Icons.Default.Lock,
                onClick = {
                    onNavigateOnHomeScreen.invoke("inner-posts-graph")
                },
                isSelected = currentScreenRoute == "inner-posts-graph"
            )
        }
        NavigationDrawerItem(
            text = stringResource(id = R.string.settings_screen_name),
            icon = Icons.Default.Settings,
            onClick = {
                onNavigateOnHomeScreen.invoke("settings-graph")
            },
            isSelected = currentScreenRoute == "settings-graph"
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