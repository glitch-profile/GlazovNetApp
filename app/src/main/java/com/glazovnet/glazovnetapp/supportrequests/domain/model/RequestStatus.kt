package com.glazovnet.glazovnetapp.supportrequests.domain.model

import com.glazovnet.glazovnetapp.R

sealed class RequestStatus(
    val stringResourceRequestStatus: Int
) {
    data object NotReviewed : RequestStatus(R.string.request_screen_request_status_not_reviewed_text)
    data object Active : RequestStatus(R.string.request_screen_request_status_in_progress_text)
    data object Solved : RequestStatus(R.string.request_screen_request_status_solved_text)

    companion object {
        fun RequestStatus.convertToIntCode(): Int {
            return when (this) {
                NotReviewed -> 0
                Active -> 1
                Solved -> 2
            }
        }
        fun getFromIntCode(typeCode: Int): RequestStatus {
            return when (typeCode) {
                0 -> NotReviewed
                1 -> Active
                2 -> Solved
                else -> NotReviewed
            }
        }

        fun values() = listOf(NotReviewed, Active, Solved)
    }

}
