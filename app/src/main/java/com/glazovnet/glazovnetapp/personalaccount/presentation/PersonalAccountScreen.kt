package com.glazovnet.glazovnetapp.personalaccount.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.math.absoluteValue

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
        val tariffState = viewModel.tariffData.collectAsState()

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        TopAppBar(
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
                TopBarComponent(
                    accountState = accountState.value,
                    tariffState = tariffState.value
                )
            }
        }
    }
}

@Composable
private fun TopBarComponent(
    accountState: PersonalAccountScreenState,
    tariffState: ScreenState<TariffModel>
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
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = buildString {
                append(stringResource(welcomeTextRes))
                append(", ")
                append(accountState.personInfo!!.firstName)
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            if (accountState.clientInfo != null) {
                PersonalAccountTopBarCard(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    title = stringResource(id = R.string.personal_account_top_bar_account_number_title),
                    text = accountState.clientInfo.accountNumber,
                    icon = Icons.Default.Person,
                    onCardClicked = { TODO() }
                )
                val balanceFormatted = String.format("%.2f", accountState.clientInfo.balance)
                PersonalAccountTopBarCard(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    title = stringResource(id = R.string.personal_account_top_bar_current_balance_title),
                    text = "$balanceFormatted ₽",
                    icon = Icons.Default.Info,
                    onCardClicked = { TODO() }
                )
                val tariffCardText = if (tariffState.isLoading && tariffState.data == null) {
                    stringResource(id = R.string.reusable_text_loading)
                } else if (tariffState.data != null) {
                    tariffState.data.name
                } else {
                    stringResource(id = R.string.personal_account_top_bar_current_tariff_load_error_text)
                }
                PersonalAccountTopBarCard(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    title = stringResource(id = R.string.personal_account_top_bar_current_tariff_title),
                    text = tariffCardText,
                    icon = Icons.Default.Menu,
                    onCardClicked = { TODO() }
                )
                val hoursDifference = derivedStateOf {
                    val payOffDay = accountState.clientInfo.debitDate
                    val now = OffsetDateTime.now(ZoneId.systemDefault())
                    Duration.between(now, payOffDay).toHours().toInt().absoluteValue
                }
                val daysRemaining = hoursDifference.value / 24
                PersonalAccountTopBarCard(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    title = stringResource(id = R.string.personal_account_top_bar_payment_remaining_days_title),
                    text = if (daysRemaining > 0) {
                        pluralStringResource(
                            id = R.plurals.personal_account_top_bar_payment_remaining_days_value,
                            count = daysRemaining,
                            daysRemaining
                        )
                    } else {
                        if (hoursDifference.value != 0) {
                            pluralStringResource(
                                id = R.plurals.personal_account_top_bar_payment_remaining_hours_value,
                                count = hoursDifference.value,
                                hoursDifference.value
                            )
                        } else stringResource(id = R.string.personal_account_top_bar_payment_remaining_days_less_than_hour_text)
                    },
                    icon = Icons.Default.DateRange,
                    onCardClicked = { TODO() }
                )
            }
            PersonalAccountTopBarCard(
                modifier = Modifier
                    .padding(start = 8.dp),
                title = stringResource(id = R.string.personal_account_top_bar_notification_title),
                text = if (accountState.personInfo!!.isNotificationsEnabled) 
                    stringResource(id = R.string.personal_account_top_bar_notification_enabled_text)
                else stringResource(id = R.string.personal_account_top_bar_notification_disabled_text),
                icon = Icons.Default.Notifications,
                onCardClicked = { TODO() }
            )
            if (accountState.employeeInfo != null) {
                val ratingText = if (accountState.employeeInfo.averageRating >= 4.5f)
                    stringResource(id = R.string.personal_account_top_bar_employee_rating_great_text)
                else if (accountState.employeeInfo.averageRating >= 3.5f)
                    stringResource(id = R.string.personal_account_top_bar_employee_rating_good_text)
                else if (accountState.employeeInfo.averageRating >= 2.5f)
                    stringResource(id = R.string.personal_account_top_bar_employee_rating_acceptable_text)
                else if (accountState.employeeInfo.averageRating == 0f)
                    stringResource(id = R.string.personal_account_top_bar_employee_rating_none_text)
                else stringResource(id = R.string.personal_account_top_bar_employee_rating_bad_text)
                PersonalAccountTopBarCard(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    title = stringResource(id = R.string.personal_account_top_bar_employee_rating_title),
                    text = ratingText,
                    icon = Icons.Default.ThumbUp,
                    onCardClicked = { TODO() }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}