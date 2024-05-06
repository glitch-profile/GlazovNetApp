package com.glazovnet.glazovnetapp.personalaccount.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import com.glazovnet.glazovnetapp.core.presentation.components.CheckedButton
import com.glazovnet.glazovnetapp.core.presentation.components.LoadingComponent
import com.glazovnet.glazovnetapp.core.presentation.components.RequestErrorScreen
import com.glazovnet.glazovnetapp.core.presentation.states.ScreenState
import com.glazovnet.glazovnetapp.tariffs.domain.model.TariffModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalAccountScreen(
    onNavigationButtonPressed: () -> Unit,
    onOpenNotificationsSettings: () -> Unit,
    onOpenTariffsScreen: () -> Unit,
    onOpenServicesScreen: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val accountState = viewModel.state.collectAsState()
        val tariffState = viewModel.tariffData.collectAsState()
        val servicesState = viewModel.servicesData.collectAsState()

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
                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.personal_account_info_general_sector_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                ) {
                    if (accountState.value.clientInfo != null) {
                        with(accountState.value.clientInfo!!) {
                            AccountInfoComponent(
                                icon = Icons.Default.AccountCircle,
                                title = stringResource(id = R.string.personal_account_info_account_number_title),
                                text = this.accountNumber
                            )
                            AccountInfoComponentWithAction(
                                icon = Icons.Default.Settings,
                                title = stringResource(id = R.string.personal_account_info_account_status_title),
                                text = if (this.isAccountActive) stringResource(id = R.string.personal_account_values_account_active_text)
                                else stringResource(id = R.string.personal_account_values_account_locked_text),
                                actionTitle = stringResource(id = R.string.personal_account_actions_lock_account),
                                onActionClick = {
                                    // TODO
                                }
                            )
                            AccountInfoComponent(
                                icon = Icons.Default.LocationOn,
                                title = stringResource(id = R.string.personal_account_info_user_address_title),
                                text = this.address
                            )
                        }
                        with(accountState.value.personInfo!!) {
                            AccountInfoComponent(
                                icon = Icons.Default.Person,
                                title = stringResource(id = R.string.personal_account_info_user_full_name_title),
                                text = "${this.lastName} ${this.firstName} ${this.middleName}"
                            )
                            AccountInfoComponentWithAction(
                                icon = Icons.Default.Notifications,
                                title = stringResource(id = R.string.personal_account_info_notification_settings_title),
                                text = if (this.isNotificationsEnabled) stringResource(id = R.string.personal_account_values_notifications_enabled_text)
                                else stringResource(id = R.string.personal_account_values_notifications_disabled_text),
                                actionTitle = stringResource(id = R.string.personal_account_actions_edit_notification_settings),
                                onActionClick = onOpenNotificationsSettings
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.personal_account_info_security_sector_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                ) {
                    with(accountState.value.personInfo!!) {
                        AccountInfoComponent(
                            icon = Icons.Default.Person,
                            title = stringResource(id = R.string.personal_account_info_user_login_title),
                            text = this.login
                        )
                        var isPasswordVisible by remember {
                            mutableStateOf(false)
                        }
                        val passwordMasked by remember {
                            derivedStateOf {
                                accountState.value.personInfo!!.password.map { '•' }.joinToString("")
                            }
                        }
                        AccountInfoComponentWithAction(
                            icon = Icons.Default.Lock,
                            title = stringResource(id = R.string.personal_account_info_user_password_title),
                            text = if (isPasswordVisible) this.password else passwordMasked,
                            onActionClick = {
                                isPasswordVisible = !isPasswordVisible
                            },
                            actionTitle = if (isPasswordVisible) stringResource(id = R.string.personal_account_actions_hide_password)
                            else stringResource(id = R.string.personal_account_actions_show_password)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                CheckedButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.small),
                    title = stringResource(id = R.string.personal_account_actions_edit_password),
                    description = stringResource(id = R.string.personal_account_actions_edit_password_description),
                    isChecked = true,
                    titleMaxLines = 1,
                    descriptionMaxLines = 1,
                    onStateChanges = {
                        // TODO
                    }
                )
                if (accountState.value.clientInfo != null) {
                    Text(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        text = stringResource(id = R.string.personal_account_info_client_information_sector_title),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    ) {
                        with(accountState.value.clientInfo!!) {
                            AccountInfoComponent(
                                icon = Icons.Default.DateRange,
                                title = stringResource(id = R.string.personal_account_info_client_creation_date_title),
                                text = this.accountCreationDate.toLocalDate().format(
                                    DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                            )
                            AccountInfoComponent(
                                icon = Icons.Default.DateRange,
                                title = stringResource(id = R.string.personal_account_info_nearest_debit_date_title),
                                text = this.debitDate.getLocalizedOffsetString()
                            )
                            AccountInfoComponentWithAction(
                                icon = Icons.Default.Info,
                                title = stringResource(id = R.string.personal_account_info_balance_title),
                                text = String.format("%.2f", this.balance) + " ₽",
                                actionTitle = stringResource(id = R.string.personal_account_actions_show_balance_history),
                                onActionClick = {
                                    //TODO
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CheckedButton(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(MaterialTheme.shapes.small),
                        title = stringResource(id = R.string.personal_account_actions_add_funds),
                        description = stringResource(id = R.string.personal_account_actions_add_funds_description),
                        isChecked = true,
                        titleMaxLines = 1,
                        descriptionMaxLines = 1,
                        onStateChanges = {
                            // TODO
                        }
                    )
                    if (tariffState.value.data != null) {
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.personal_account_info_connected_tariff_sector_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        ) {
                            with(tariffState.value.data!!) {
                                AccountInfoComponentWithAction(
                                    icon = Icons.Default.List,
                                    title = stringResource(id = R.string.personal_account_info_tariff_name_title),
                                    text = this.name,
                                    actionTitle = stringResource(id = R.string.personal_account_actions_edit_tariff),
                                    onActionClick = onOpenTariffsScreen
                                )
                                AccountInfoComponent(
                                    icon = Icons.Default.Info,
                                    title = stringResource(id = R.string.personal_account_info_tariff_cost_title),
                                    text = pluralStringResource(
                                        id = R.plurals.reusable_payment_cost_value,
                                        count = this.costPerMonth,
                                        this.costPerMonth
                                    )
                                )
                            }
                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                        CheckedButton(
//                            modifier = Modifier
//                                .padding(horizontal = 16.dp)
//                                .clip(MaterialTheme.shapes.small),
//                            title = "Открыть список тарифов",
//                            description = "Сменить подключенный тариф",
//                            isChecked = true,
//                            titleMaxLines = 1,
//                            descriptionMaxLines = 1,
//                            onStateChanges = {
//                                // TODO
//                            }
//                        )
                    }
                    if (servicesState.value.data != null) {
                        Text(
                            modifier = Modifier
                                .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.personal_account_info_connected_services_sector_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        ) {
                            with(servicesState.value.data!!) {
                                AccountInfoComponent(
                                    icon = Icons.Default.Info,
                                    title = stringResource(id = R.string.personal_account_info_services_status_title),
                                    text = if (this.isEmpty()) {
                                        stringResource(id = R.string.personal_account_values_no_services_text)
                                    } else {
                                        pluralStringResource(
                                            id = R.plurals.personal_account_values_connected_services_count_text,
                                            count = this.size,
                                            this.size
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        CheckedButton(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(MaterialTheme.shapes.small),
                            title = stringResource(id = R.string.personal_account_action_manage_connected_services),
                            description = stringResource(id = R.string.personal_account_action_manage_connected_services_description),
                            isChecked = true,
                            titleMaxLines = 1,
                            descriptionMaxLines = 1,
                            onStateChanges = {
                                onOpenServicesScreen.invoke()
                            }
                        )
                    }
                }
                if (accountState.value.employeeInfo != null) {
                    Text(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        text = stringResource(id = R.string.personal_account_info_employee_info_sector_title),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    ) {
                        with(accountState.value.employeeInfo!!) {
                            AccountInfoComponent(
                                icon = Icons.Default.DateRange,
                                title = stringResource(id = R.string.personal_account_info_employee_creation_date_title),
                                text = this.accountCreationDate.toLocalDate().format(
                                    DateTimeFormatter.ofPattern("dd MMMM yyyy")
                                )
                            )
                            if (this.numberOfRatings != 0) {
                                val ratingFormatted = String.format("%.2f", this.averageRating)
                                val numberOfRatings = this.numberOfRatings
                                AccountInfoComponent(
                                    icon = Icons.Default.ThumbUp,
                                    title = stringResource(id = R.string.personal_account_info_employee_rating_title),
                                    text = buildString {
                                        append("$ratingFormatted | ")
                                        append("${stringResource(id = R.string.personal_account_values_ratings_count_prefix_text)} ")
                                        append(pluralStringResource(
                                            id = R.plurals.personal_account_values_ratings_count_text,
                                            count = numberOfRatings,
                                            numberOfRatings
                                        ))
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
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
                val hoursDifference = derivedStateOf { //TODO: rework to reduce calculations
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

@Composable
private fun AccountInfoComponent(
    icon: ImageVector,
    title: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountInfoComponentWithAction(
    icon: ImageVector,
    title: String,
    text: String,
    actionTitle: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onActionClick.invoke() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = actionTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}