package com.glazovnet.glazovnetapp.supportrequests.presentation.autosupport

data class AutoSupportItem(
    val index: Int,
    val titleRes: Int,
    val descriptionRes: Int,
    val descriptionImageUrl: String?,
    val moveToIndexOnSuccess: Int?,
    val successButtonTextRes: Int,
    val moveToIndexOnFailure: Int?,
    val failedButtonTextRes: Int
)
