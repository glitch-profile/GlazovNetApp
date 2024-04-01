package com.glazovnet.glazovnetapp.settings.appearance.presentation

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.CheckedButton
import com.glazovnet.glazovnetapp.core.presentation.components.DesignedSwitchButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    onNavigationButtonPressed: () -> Unit,
    viewModel: AppearanceSettingsScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isUseSystemTheme = viewModel.isUseSystemTheme.collectAsState()
    val isUseDarkTheme = viewModel.isUseDarkTheme.collectAsState()
    val isUseDynamicColor = viewModel.isUseDynamicColor.collectAsState()
    val isUsingSystemLocale = viewModel.isUsingSystemLocale.collectAsState()
    val language = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        MediumTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.appearance_settings_screen_name)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigationButtonPressed.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp),
                    text = stringResource(id = R.string.appearance_settings_dynamic_color_title),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.small)
                ) {
                    DesignedSwitchButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = stringResource(id = R.string.appearance_settings_use_dynamic_color_title),
                        description = stringResource(id = R.string.appearance_settings_use_dynamic_color_description),
                        isChecked = isUseDynamicColor.value,
                        onStateChanges = {
                            viewModel.setIsUseDynamicColor(it)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp),
                text = stringResource(id = R.string.appearance_settings_color_theme_title),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
            ) {
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = stringResource(id = R.string.appearance_settings_color_theme_auto_title),
                    description = stringResource(id = R.string.appearance_settings_color_theme_auto_description),
                    isChecked = isUseSystemTheme.value,
                    onStateChanges = {
                        viewModel.setIsUseSystemTheme(true)
                    }
                )
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = stringResource(id = R.string.appearance_settings_color_theme_light_title),
                    description = stringResource(id = R.string.appearance_settings_color_theme_light_description),
                    isChecked = !isUseSystemTheme.value && !isUseDarkTheme.value,
                    onStateChanges = {
                        viewModel.setIsUseSystemTheme(false)
                        viewModel.setIsUseDarkTheme(false)
                    }
                )
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = stringResource(id = R.string.appearance_settings_color_theme_dark_title),
                    description = stringResource(id = R.string.appearance_settings_color_theme_dark_description),
                    isChecked = !isUseSystemTheme.value && isUseDarkTheme.value,
                    onStateChanges = {
                        viewModel.setIsUseSystemTheme(false)
                        viewModel.setIsUseDarkTheme(true)
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp),
                text = stringResource(id = R.string.appearance_settings_language_title),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
            ) {
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = stringResource(id = R.string.appearance_settings_language_auto_title),
                    description = stringResource(id = R.string.appearance_settings_language_auto_description),
                    isChecked = isUsingSystemLocale.value,
                    onStateChanges = {
                        viewModel.changeAppLanguage(context, null)
                    }
                )
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "English",
                    description = stringResource(id = R.string.appearance_settings_language_en_description),
                    isChecked = language == Locale("en").language && !isUsingSystemLocale.value,
                    onStateChanges = {
                        viewModel.changeAppLanguage(context, Locale("en").toLanguageTag())
                    }
                )
                CheckedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "Русский",
                    description = stringResource(id = R.string.appearance_settings_language_ru_description),
                    isChecked = language == Locale("ru").language && !isUsingSystemLocale.value,
                    onStateChanges = {
                        viewModel.changeAppLanguage(context, Locale("ru").toLanguageTag())
                    }
                )
            }
        }
    }
}