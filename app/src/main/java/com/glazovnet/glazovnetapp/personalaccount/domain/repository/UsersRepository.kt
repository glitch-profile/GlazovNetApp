package com.glazovnet.glazovnetapp.personalaccount.domain.repository

import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.EmployeeModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.PersonModel

interface UsersRepository {

    suspend fun getPersonData(
        token: String,
        personId: String
    ): Resource<PersonModel>

    suspend fun getEmployeeData(
        token: String,
        employeeId: String
    ): Resource<EmployeeModel>

    suspend fun getClientData(
        token: String,
        clientId: String
    ): Resource<ClientModel>

    suspend fun changePassword(
        token: String,
        personId: String,
        oldPassword: String,
        newPassword: String
    ): Resource<Unit>

    suspend fun blockAccount(
        token: String,
        clientId: String
    ): Resource<Unit>

    suspend fun addFunds(
        token: String,
        clientId: String,
        amount: Double,
        note: String?
    ): Resource<Unit>

}