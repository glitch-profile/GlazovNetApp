package com.glazovnet.glazovnetapp.personalaccount.presentation

import com.glazovnet.glazovnetapp.personalaccount.domain.model.ClientModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.EmployeeModel
import com.glazovnet.glazovnetapp.personalaccount.domain.model.PersonModel

data class PersonalAccountScreenState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val stringResourceMessage: Int? = null,
    val message: String? = null,
    val personInfo: PersonModel? = null,
    val clientInfo: ClientModel? = null,
    val employeeInfo: EmployeeModel? = null
)
