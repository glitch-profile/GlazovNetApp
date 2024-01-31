package com.glazovnet.glazovnetapp.presentation.supportscreen.requestdetails

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RequestDetailsScreen(
    requestId: String,
    onNavigationButtonPressed: () -> Unit,
    onOpenChatButtonPressed: (String) -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel()
) {

}