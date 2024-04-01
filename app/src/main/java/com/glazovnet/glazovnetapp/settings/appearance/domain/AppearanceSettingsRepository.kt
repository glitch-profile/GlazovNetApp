package com.glazovnet.glazovnetapp.settings.appearance.domain

import android.content.SharedPreferences

interface AppearanceSettingsRepository {

    val preferences: SharedPreferences

    fun getIsSystemTheme(): Boolean
    fun setIsSystemTheme(isSystemTheme: Boolean)

    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(isDarkTheme: Boolean)

    fun getIsDynamicColorsEnabled(): Boolean
    fun setIsDynamicColorsEnabled(isDynamicColors: Boolean)

    fun getIsUsingSystemLocale(): Boolean
    fun setIsUsingSystemLocale(isSystemLocale: Boolean)

}