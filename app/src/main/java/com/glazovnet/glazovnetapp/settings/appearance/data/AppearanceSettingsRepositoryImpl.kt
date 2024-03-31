package com.glazovnet.glazovnetapp.settings.appearance.data

import android.content.Context
import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.settings.appearance.domain.AppearanceSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFERENCE_NAME = "notificationsSettings"
private const val IS_SYSTEM_THEME = "isUseSystemTheme"
private const val IS_DARK_THEME = "isUseDarkTheme"
private const val IS_DYNAMIC_COLORS_ENABLED = "isUseDynamicColors"

class AppearanceSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): AppearanceSettingsRepository {

    override val preferences: SharedPreferences
        get() = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private var isUseSystemTheme: Boolean? = null
    override fun getIsSystemTheme(): Boolean {
        return isUseSystemTheme ?: kotlin.run {
            isUseSystemTheme = preferences.getBoolean(IS_SYSTEM_THEME, true)
            isUseSystemTheme!!
        }
    }
    override fun setIsSystemTheme(isSystemTheme: Boolean) {
        isUseSystemTheme = isSystemTheme
        preferences.edit().putBoolean(IS_SYSTEM_THEME, isSystemTheme).apply()
    }

    private var isUseDarkTheme: Boolean? = null
    override fun getIsDarkTheme(): Boolean {
        return isUseDarkTheme ?: kotlin.run {
            isUseDarkTheme = preferences.getBoolean(IS_DARK_THEME, false)
            isUseDarkTheme!!
        }
    }
    override fun setIsDarkTheme(isDarkTheme: Boolean) {
        isUseDarkTheme = isDarkTheme
        preferences.edit().putBoolean(IS_DARK_THEME, isDarkTheme).apply()
    }

    private  var isUseDynamicColors: Boolean? = null
    override fun getIsDynamicColorsEnabled(): Boolean {
        return isUseDynamicColors ?: kotlin.run {
            isUseDynamicColors = preferences.getBoolean(IS_DYNAMIC_COLORS_ENABLED, false)
            isUseDynamicColors!!
        }
    }
    override fun setIsDynamicColorsEnabled(isDynamicColors: Boolean) {
        isUseDynamicColors = isDynamicColors
        preferences.edit().putBoolean(IS_DYNAMIC_COLORS_ENABLED, isDynamicColors).apply()
    }
}