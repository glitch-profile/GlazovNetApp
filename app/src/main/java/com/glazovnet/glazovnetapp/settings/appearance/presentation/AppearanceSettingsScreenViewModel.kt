package com.glazovnet.glazovnetapp.settings.appearance.presentation

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
}