package com.glazovnet.glazovnetapp.personalaccount.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalAccountScreen(
    onNavigationButtonPressed: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val accountState = viewModel.state.collectAsState()

        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.personal_account_screen_name))
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
            actions = {
                AnimatedVisibility(visible = !accountState.value.isLoading) {
                    IconButton(onClick = {
                        if (!accountState.value.isLoading)  {
                            viewModel.loadUserInfo()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update page"
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            )
        )
        if (accountState.value.isLoading && accountState.value.personInfo == null) {
            LoadingComponent()
        } else if (accountState.value.stringResourceMessage != null) {
            RequestErrorScreen(
                messageStringResource = accountState.value.stringResourceMessage,
                additionalMessage = accountState.value.message
            )
        } else if (accountState.value.personInfo != null) { // if personInfo != null, then all important info should be already loaded
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .padding(bottom = 16.dp)
                ) {
                    val welcomeTextRes = remember {
                        val currentTime = LocalDateTime.now(ZoneId.systemDefault()).hour
                        if (IntRange(6, 11).contains(currentTime)) {
                            R.string.personal_account_morning_text
                        } else if (IntRange(12, 17).contains(currentTime)) {
                            R.string.personal_account_afternoon_text
                        } else if (IntRange(18, 21).contains(currentTime)) {
                            R.string.personal_account_evening_text
                        } else {
                            R.string.personal_account_night_text
                        }
                    }
                    Text(
                        text = buildString {
                            append(stringResource(welcomeTextRes))
                            append(", ")
                            append("Name")
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}