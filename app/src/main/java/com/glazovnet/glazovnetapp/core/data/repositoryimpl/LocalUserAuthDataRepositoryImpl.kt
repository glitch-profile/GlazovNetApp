package com.glazovnet.glazovnetapp.core.data.repositoryimpl

import android.content.Context
import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFERENCE_NAME = "userAuthData"
private const val USER_LOGIN = "userLogin"
private const val LOGIN_TOKEN_NAME = "loginToken"
private const val USER_ID_NAME = "userId"
private const val USER_AS_ADMIN = "isUserAsAdmin"

class LocalUserAuthDataRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): LocalUserAuthDataRepository {

    override val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun getSavedUserLogin(): String? {
        return preferences.getString(USER_LOGIN, null)
    }
    override fun setSavedUserLogin(login: String?){
        preferences.edit().putString(USER_LOGIN, login).apply()
    }

    private var savedLoginToken: String? = null
    override fun getLoginToken(): String? {
        return if (savedLoginToken != null) savedLoginToken
        else  {
            val loginToken = preferences.getString(LOGIN_TOKEN_NAME, null)
            if (loginToken != null) savedLoginToken = loginToken
            loginToken
        }
    }
    override fun setLoginToken(loginToken: String?, isNeedToSave: Boolean) {
        savedLoginToken = loginToken
        if (isNeedToSave) preferences.edit().putString(LOGIN_TOKEN_NAME, loginToken).apply()
    }

    private var associatedUserId: String? = null
    override fun getAssociatedUserId(): String? {
        return if (associatedUserId != null) associatedUserId
        else {
            val userId = preferences.getString(USER_ID_NAME, null)
            if (userId != null) associatedUserId = userId
            userId
        }
    }
    override fun setAssociatedUserId(userId: String?, isNeedToSave: Boolean) {
        associatedUserId = userId
        if (isNeedToSave) preferences.edit().putString(USER_ID_NAME, userId).apply()
    }

    private var isUserAsAdmin: Boolean? = null
    override fun getIsUserAsAdmin(): Boolean {
        return if (isUserAsAdmin != null) isUserAsAdmin!!
        else {
            val isAdmin: Boolean = preferences.getBoolean(USER_AS_ADMIN, false)
            isUserAsAdmin = isAdmin
            isAdmin
        }
    }
    override fun setIsUserAsAdmin(isAdmin: Boolean, isNeedToSave: Boolean) {
        isUserAsAdmin = isAdmin
        if (isNeedToSave) preferences.edit().putBoolean(USER_AS_ADMIN, isAdmin).apply()
    }
}