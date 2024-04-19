package com.glazovnet.glazovnetapp.announcements.domain.repository

import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement
import com.glazovnet.glazovnetapp.announcements.domain.model.AnnouncementModel
import com.glazovnet.glazovnetapp.core.domain.utils.Resource

interface AnnouncementsApiRepository {

    suspend fun getAllAnnouncements(token: String, employeeId: String): Resource<List<AnnouncementModel>>

    suspend fun getAnnouncementsForClient(token: String, clientId: String): Resource<List<AnnouncementModel>>

    suspend fun addAnnouncement(token: String, employeeId: String, announcementModel: AnnouncementModel): Resource<AnnouncementModel?>

    suspend fun getCitiesWithName(token: String, employeeId: String, cityName: String): Resource<List<String>>

    suspend fun getStreetsWithName(token: String, employeeId: String, cityName: String, streetName: String): Resource<List<String>>

    suspend fun getAddresses(token: String, employeeId: String, cityName: String, streetName: String): Resource<List<AddressFilterElement>>

}