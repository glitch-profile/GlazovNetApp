package com.glazovnet.glazovnetapp.presentation.mainactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.GlazovNetAppTheme
import com.glazovnet.glazovnetapp.presentation.homescreen.HomeScreen
import com.glazovnet.glazovnetapp.presentation.loginscreen.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GlazovNetAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val messageString = viewModel.messageResourceString.collectAsState()
                    val isErrorVisible = viewModel.isShowingMessage.collectAsState()

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = viewModel.getStartDestination()
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
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                        AnimatedVisibility(
                            visible = isErrorVisible.value,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .animateContentSize(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = stringResource(id = messageString.value),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}
