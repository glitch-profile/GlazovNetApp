package com.glazovnet.glazovnetapp.settings.appearance.presentation

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.glazovnet.glazovnetapp.settings.appearance.domain.AppearanceSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppearanceSettingsScreenViewModel @Inject constructor(
    private val appearanceSettingsRepository: AppearanceSettingsRepository
): ViewModel() {
    private val _isUseSystemTheme = MutableStateFlow(true)
    val isUseSystemTheme = _isUseSystemTheme.asStateFlow()
    private val _isUseDarkTheme = MutableStateFlow(false)
    val isUseDarkTheme = _isUseDarkTheme.asStateFlow()
    private val _isUseDynamicColor = MutableStateFlow(false)
    val isUseDynamicColor = _isUseDynamicColor.asStateFlow()
    private val _isUsingSystemLocale = MutableStateFlow(true)
    val isUsingSystemLocale = _isUsingSystemLocale.asStateFlow()

    init {
        _isUseSystemTheme.update {
            appearanceSettingsRepository.getIsSystemTheme()
        }
        _isUseDarkTheme.update {
            appearanceSettingsRepository.getIsDarkTheme()
        }
        _isUseDynamicColor.update {
            appearanceSettingsRepository.getIsDynamicColorsEnabled()
        }
        _isUsingSystemLocale.update {
            appearanceSettingsRepository.getIsUsingSystemLocale()
        }
    }

    fun setIsUseSystemTheme(isSystemTheme: Boolean) {
        if (isUseSystemTheme.value != isSystemTheme) {
            appearanceSettingsRepository.setIsSystemTheme(isSystemTheme)
            _isUseSystemTheme.update { isSystemTheme }
        }
    }
    fun setIsUseDarkTheme(isDarkTheme: Boolean) {
        if (isUseDarkTheme.value != isDarkTheme) {
            appearanceSettingsRepository.setIsDarkTheme(isDarkTheme)
            _isUseDarkTheme.update { isDarkTheme }
        }
    }
    fun setIsUseDynamicColor(isDynamicColorEnabled: Boolean) {
        if (isUseDynamicColor.value != isDynamicColorEnabled) {
            appearanceSettingsRepository.setIsDynamicColorsEnabled(isDynamicColorEnabled)
            _isUseDynamicColor.update { isDynamicColorEnabled }
        }
    }

    fun changeAppLanguage(context: Context, langCode: String?) {
        setIsUsingSystemLocale(langCode == null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .apply {
                    applicationLocales = if (langCode == null) {
                        LocaleList.getEmptyLocaleList()
                    } else {
                        LocaleList.forLanguageTags(langCode)
                    }
                }
        } else {
            if (langCode == null) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode))
            }
        }
    }

    private fun setIsUsingSystemLocale(isSystemLocale: Boolean) {
        if (isUsingSystemLocale.value != isSystemLocale) {
            appearanceSettingsRepository.setIsUsingSystemLocale(isSystemLocale)
            _isUsingSystemLocale.update { isSystemLocale }
        }
    }
}