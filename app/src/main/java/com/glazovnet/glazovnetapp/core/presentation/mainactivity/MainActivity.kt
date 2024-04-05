package com.glazovnet.glazovnetapp.core.presentation.mainactivity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.unregisterAppearanceListener()
        super.onDestroy()
    }
}
