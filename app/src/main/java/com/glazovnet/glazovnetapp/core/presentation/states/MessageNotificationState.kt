package com.glazovnet.glazovnetapp.core.presentation.states

import com.glazovnet.glazovnetapp.R

data class MessageNotificationState(
    val enabled: Boolean = false,
    val titleResource: Int = R.string.api_response_unknown_error,
    val additionTextResource: Int = R.string.reusable_text_unknown,
    val iconRes: Int? = null
)