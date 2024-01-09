package com.glazovnet.glazovnetapp.domain.repository

import android.content.SharedPreferences

interface LocalUserAuthDataRepository {

    val preferences: SharedPreferences

    fun getSavedUserLogin(): String?
    fun setSavedUserLogin(login: String?)

    fun getLoginToken(): String?
    fun setLoginToken(loginToken: String?, isNeedToSave: Boolean)

    fun getAssociatedUserId(): String?
    fun setAssociatedUserId(userId: String?, isNeedToSave: Boolean)

    fun getIsUserAsAdmin(): Boolean?
    fun setIsUserAsAdmin(isAdmin: Boolean, isNeedToSave: Boolean)
}