package com.glazovnet.glazovnetapp.personalaccount.data.mapper

import com.glazovnet.glazovnetapp.personalaccount.data.entity.PersonInfoDto
import com.glazovnet.glazovnetapp.personalaccount.domain.model.PersonModel

fun PersonInfoDto.toPersonModel(): PersonModel {
    return PersonModel(
        firstName = this.firstName,
        lastName = this.lastName,
        middleName = this.middleName,
        login = this.login,
        password = this.password,
        profileAvatar = this.profileAvatar,
        isNotificationsEnabled = this.isNotificationsEnabled
    )
}