package com.glazovnet.glazovnetapp.core.presentation.mainactivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glazovnet.glazovnetapp.core.presentation.homescreen.HomeScreen
import com.glazovnet.glazovnetapp.core.ui.theme.GlazovNetAppTheme
import com.glazovnet.glazovnetapp.login.presentation.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.registerAppearanceListener()

        setContent {
            val isUseSystemTheme = viewModel.isUseSystemTheme.collectAsState()
            val isUseDarkTheme = viewModel.isUseDarkTheme.collectAsState()
            val isUseDynamicColor = viewModel.isUseDynamicColor.collectAsState()

            GlazovNetAppTheme(
                useSystemTheme = isUseSystemTheme.value,
                useDarkTheme = isUseDarkTheme.value,
                dynamicColor = isUseDynamicColor.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val messageString = viewModel.messageResourceString.collectAsState()
                    val isErrorVisible = viewModel.isShowingMessage.collectAsState()
                    val startDestination = viewModel.startRoute

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login-screen") {
                            LoginScreen(
                                onNavigateToHomeScreen = {
                                    navController.navigate("home-screen") {
                                        popUpTo("login-screen") {inclusive = true}
                                    }
                                },
                                onNeedToShowMessage = {
                                    viewModel.showMessage(it)
                                }
                            )
                        }

                        composable("home-screen") {
                            HomeScreen(
                                onNavigateToLoginScreen = {
                                    viewModel.logout()
                                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                                    navController.navigate("login-screen") {
                                        if (currentRoute != null) {
                                            popUpTo(currentRoute) {inclusive = true}
                                        }
                                    }
                                },
                                onNeedToShowMessage = {
                                    viewModel.showMessage(it)
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val color = MaterialTheme.colorScheme.inverseSurface
                        AnimatedVisibility(
                            visible = isErrorVisible.value,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color)
                                    .navigationBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                                    .animateContentSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = stringResource(id = messageString.value),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.inverseOnSurface
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.unregisterAppearanceListener()
        super.onDestroy()
    }
}
