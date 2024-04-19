package com.glazovnet.glazovnetapp.core.domain.repository

import android.content.SharedPreferences
import com.glazovnet.glazovnetapp.core.domain.utils.EmployeeRoles

interface LocalUserAuthDataRepository {

    val preferences: SharedPreferences

    fun getSavedUserLogin(): String?
    fun setSavedUserLogin(login: String?)

    fun getLoginToken(): String?
    fun setLoginToken(loginToken: String?)

    fun getAssociatedPersonId(): String?
    fun setAssociatedPersonId(personId: String?)

    fun getAssociatedClientId(): String?
    fun setAssociatedClientId(clientId: String?)

    fun getAssociatedEmployeeId(): String?
    fun setAssociatedEmployeeId(employeeId: String?)

    fun getEmployeeHasRole(roleToCheck: EmployeeRoles): Boolean
    fun getEmployeeRoles(): List<EmployeeRoles>?
    fun setEmployeeRoles(roles: List<String>?)
}