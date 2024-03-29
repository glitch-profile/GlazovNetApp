package com.glazovnet.glazovnetapp.announcements.domain.repository

import com.glazovnet.glazovnetapp.announcements.domain.model.AddressFilterElement
import com.glazovnet.glazovnetapp.announcements.domain.model.AnnouncementModel
import com.glazovnet.glazovnetapp.core.domain.utils.Resource

interface AnnouncementsApiRepository {

    suspend fun getAllAnnouncements(token: String): Resource<List<AnnouncementModel>>

    suspend fun getAnnouncementsForClient(token: String, userId: String): Resource<List<AnnouncementModel>>

    suspend fun addAnnouncement(announcementModel: AnnouncementModel, token: String): Resource<AnnouncementModel?>

    suspend fun getCitiesWithName(cityName: String, token: String): Resource<List<String>>

    suspend fun getStreetsWithName(cityName: String, streetName: String, token: String): Resource<List<String>>

    suspend fun getAddresses(cityName: String, streetName: String, token: String): Resource<List<AddressFilterElement>>

}