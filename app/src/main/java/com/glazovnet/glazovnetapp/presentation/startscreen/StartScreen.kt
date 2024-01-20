package com.glazovnet.glazovnetapp.presentation.startscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.LoadingIndicator

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    viewModel: StartScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(null) {
        if (viewModel.isUserSignedIn()) onNavigateToHomeScreen.invoke()
        else onNavigateToLoginScreen.invoke()
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_glazov_net_logo),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        LoadingIndicator(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}