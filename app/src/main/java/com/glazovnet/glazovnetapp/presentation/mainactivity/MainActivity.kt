package com.glazovnet.glazovnetapp.presentation.mainactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
}
