package com.glazovnet.glazovnetapp.settings.core.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.settings.core.presentation.components.SettingsSectorButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigationButtonPressed: () -> Unit,
    onNavigateToNotificationsScreen: () -> Unit,
    onNavigateToInfoScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
//        TopAppBar(
//            title = {
//                Text(
//                    text = stringResource(id = R.string.settings_screen_name)
//                )
//            },
//            navigationIcon = {
//                IconButton(
//                    onClick = {
//                        onNavigationButtonPressed.invoke()
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Menu,
//                        contentDescription = null
//                    )
//                }
//            }
//        )
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        LargeTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.settings_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
//            Text(
//                modifier = Modifier
//                    .padding(start = 32.dp, end = 16.dp),
//                text = stringResource(id = R.string.settings_screen_general_settings_title),
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.titleMedium
//            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
            ) {
                SettingsSectorButton(
                    title = stringResource(id = R.string.notifications_settings_screen_name),
                    description = stringResource(id = R.string.settings_screen_notifications_description),
                    icon = Icons.Default.Notifications,
                    onClick = onNavigateToNotificationsScreen
                )
                SettingsSectorButton(
                    title = stringResource(id = R.string.settings_screen_about_app_title),
                    description = stringResource(id = R.string.settings_screen_about_app_description),
                    icon = Icons.Default.Info,
                    onClick = onNavigateToInfoScreen
                )
            }
        }
    }
}