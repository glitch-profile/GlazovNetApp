package com.glazovnet.glazovnetapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.compose.GlazovNetAppTheme
import com.glazovnet.glazovnetapp.domain.repository.LocalUserAuthDataRepository
import com.glazovnet.glazovnetapp.presentation.homescreen.HomeScreen
import com.glazovnet.glazovnetapp.presentation.loginscreen.LoginScreen
import com.glazovnet.glazovnetapp.presentation.posts.edit.EditPostScreen
import com.glazovnet.glazovnetapp.presentation.posts.list.PostsListScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        startDestination = "login-screen"
                    ) {
                        composable("login-screen") {
                            LoginScreen(navController = navController)
                        }

                        composable("home-screen") {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}
