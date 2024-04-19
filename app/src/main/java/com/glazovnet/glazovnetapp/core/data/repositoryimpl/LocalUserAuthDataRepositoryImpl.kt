package com.glazovnet.glazovnetapp.core.data.repositoryimpl

import android.content.Context
import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.core.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFERENCE_NAME = "userAuthData"
private const val USER_LOGIN = "userLogin"
private const val LOGIN_TOKEN_NAME = "loginToken"
private const val PERSON_ID_NAME = "personId"
private const val CLIENT_ID_NAME = "clientId"
private const val EMPLOYEE_ID_NAME = "employeeId"
private const val EMPLOYEE_ROLES_NAME = "employeeRoles"

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
        return savedLoginToken ?: kotlin.run {
            savedLoginToken = preferences.getString(LOGIN_TOKEN_NAME, null)
            savedLoginToken
        }
    }
    override fun setLoginToken(loginToken: String?) {
        savedLoginToken = loginToken
        preferences.edit().putString(LOGIN_TOKEN_NAME, loginToken).apply()
    }

    private var associatedPersonId: String? = null
    override fun getAssociatedPersonId(): String? {
        return associatedPersonId ?: kotlin.run {
            associatedPersonId = preferences.getString(PERSON_ID_NAME, null)
            associatedPersonId
        }
    }
    override fun setAssociatedPersonId(personId: String?) {
        associatedPersonId = personId
        preferences.edit().putString(PERSON_ID_NAME, personId).apply()
    }

    private var associatedClientId: String? = null
    override fun getAssociatedClientId(): String? {
        return associatedPersonId ?: kotlin.run {
            associatedClientId = preferences.getString(CLIENT_ID_NAME, null)
            associatedClientId
        }
    }
    override fun setAssociatedClientId(clientId: String?) {
        associatedClientId = clientId
        preferences.edit().putString(CLIENT_ID_NAME, clientId).apply()
    }

    private var associatedEmployeeId: String? = null
    override fun getAssociatedEmployeeId(): String? {
        return associatedEmployeeId ?: kotlin.run {
            associatedEmployeeId = preferences.getString(EMPLOYEE_ID_NAME, null)
            associatedEmployeeId
        }
    }
    override fun setAssociatedEmployeeId(employeeId: String?) {
        associatedEmployeeId = employeeId
        preferences.edit().putString(EMPLOYEE_ID_NAME, employeeId).apply()
    }

    private var employeeRoles: List<EmployeeRoles>? = null
    override fun getEmployeeHasRole(roleToCheck: EmployeeRoles): Boolean {
        val roles = getEmployeeRoles()
        return roles?.contains(roleToCheck) ?: false
    }
    override fun getEmployeeRoles(): List<EmployeeRoles>? {
        return employeeRoles ?: kotlin.run {
            val roles = preferences.getStringSet(EMPLOYEE_ROLES_NAME, null)
            employeeRoles = roles?.toList()?.map { EmployeeRoles.valueOf(it) }
            employeeRoles
        }
    }
    override fun setEmployeeRoles(roles: List<String>?) {
        employeeRoles = roles?.map { EmployeeRoles.valueOf(it) }
        preferences.edit().putStringSet(EMPLOYEE_ROLES_NAME, roles?.toSet()).apply()
    }
}