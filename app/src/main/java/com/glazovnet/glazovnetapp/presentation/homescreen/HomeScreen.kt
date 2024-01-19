package com.glazovnet.glazovnetapp.presentation.homescreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.glazovnet.glazovnetapp.presentation.posts.edit.EditPostScreen
import com.glazovnet.glazovnetapp.presentation.posts.list.PostsListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Home screen")
                }
            )
        }
    ) {
        val secondaryNavController = rememberNavController()
        NavHost(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            navController = secondaryNavController,
            startDestination = "posts-graph"
        ) {
            navigation(
                route = "posts-graph",
                startDestination = "posts-list-screen" //TODO
            ) {
                composable("posts-list-screen") {
                    PostsListScreen(navController = secondaryNavController)
                }
                composable(
                    route = "edit-posts-screen?postId={postId}",
                    arguments = listOf(navArgument("postId") {nullable = true})
                ) {backStackEntry ->
                    EditPostScreen(
                        navController = secondaryNavController,
                        postId = backStackEntry.arguments?.getString("postId")
                    )
                }
            }
        }
    }
}