package com.glazovnet.glazovnetapp.core.presentation.mainactivity

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.login.domain.usecases.AuthUseCase
import com.glazovnet.glazovnetapp.settings.appearance.domain.AppearanceSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IS_SYSTEM_THEME = "isUseSystemTheme"
private const val IS_DARK_THEME = "isUseDarkTheme"
private const val IS_DYNAMIC_COLORS_ENABLED = "isUseDynamicColors"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userAuthDataRepository: LocalUserAuthDataRepository,
    private val appearanceSettingsRepository: AppearanceSettingsRepository,
    private val authUseCase: AuthUseCase
): ViewModel() {

    val startRoute = getStartDestination()

    private val _isUseSystemTheme = MutableStateFlow(true)
    val isUseSystemTheme = _isUseSystemTheme.asStateFlow()
    private val _isUseDarkTheme = MutableStateFlow(false)
    val isUseDarkTheme = _isUseDarkTheme.asStateFlow()
    private val _isUseDynamicColor = MutableStateFlow(false)
    val isUseDynamicColor = _isUseDynamicColor.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key.equals(IS_SYSTEM_THEME)) {
            _isUseSystemTheme.update { appearanceSettingsRepository.getIsSystemTheme() }
        }
        if (key.equals(IS_DARK_THEME)) {
            _isUseDarkTheme.update { appearanceSettingsRepository.getIsDarkTheme() }
        }
        if (key.equals(IS_DYNAMIC_COLORS_ENABLED)) {
            _isUseDynamicColor.update { appearanceSettingsRepository.getIsDynamicColorsEnabled() }
        }
    }

    init {
        loadAppearanceSettings()
    }

    private fun loadAppearanceSettings() {
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

    fun registerAppearanceListener() {
        appearanceSettingsRepository.preferences.registerOnSharedPreferenceChangeListener(listener)
    }
    fun unregisterAppearanceListener() {
        appearanceSettingsRepository.preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun getStartDestination(): String {
        val isUserSignedIn =  with(userAuthDataRepository) {
            getLoginToken() != null && getAssociatedPersonId() != null
        }
        return if (isUserSignedIn) "home-screen" else "login-screen"
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
        }
    }
}