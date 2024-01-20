package com.glazovnet.glazovnetapp.presentation.homescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.glazovnet.glazovnetapp.presentation.navigationdrawer.NavigationDrawer
import com.glazovnet.glazovnetapp.presentation.navigationdrawer.NavigationDrawerState
import com.glazovnet.glazovnetapp.presentation.posts.edit.EditPostScreen
import com.glazovnet.glazovnetapp.presentation.posts.list.PostsListScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val coroutineScope = rememberCoroutineScope()
        var drawerState by remember{
            mutableStateOf<NavigationDrawerState>(NavigationDrawerState.Closed)
        }
        val density = LocalDensity.current
        val drawerWidth = with(density) {
            LocalConfiguration.current.screenWidthDp.dp.roundToPx().toFloat() * 0.7f
        }
        val secondaryNavController = rememberNavController()
        val translationX = remember {
            Animatable(0f)
        }
        val decay = rememberSplineBasedDecay<Float>()
        translationX.updateBounds(0f, drawerWidth)
        val draggableState = rememberDraggableState(
            onDelta = { dragAmount ->
                coroutineScope.launch {
                    translationX.snapTo(translationX.value + dragAmount)
                }
            }
        )

        fun toggleDrawerState() {
            coroutineScope.launch {
                if (drawerState == NavigationDrawerState.Open) {
                    translationX.animateTo(0f)
                } else {
                    translationX.animateTo(drawerWidth)
                }
                drawerState = if (drawerState == NavigationDrawerState.Open) NavigationDrawerState.Closed
                else NavigationDrawerState.Open
            }
        }

        NavigationDrawer(
            onNavigate = { route ->
                val currentRoute = secondaryNavController.currentBackStackEntry?.destination?.route
                secondaryNavController.navigate(route) {
                    if (currentRoute != null) {
                        popUpTo(currentRoute) {inclusive = true}
                    }
                }
            }
        )
        Surface(
            modifier = Modifier
                .graphicsLayer {
                    this.clip = true
                    this.translationX = translationX.value
                    val scale: Float = lerp(1f, 0.85f, translationX.value / drawerWidth)
                    val cornerRadius = androidx.compose.ui.unit.lerp(0.dp, 12.dp, translationX.value / drawerWidth)
                    this.scaleY = scale
                    this.scaleX = scale
                    this.shape = RoundedCornerShape(cornerRadius)
                    this.shadowElevation = 6f
                }
                .draggable(
                    draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        val decayX = decay.calculateTargetValue(
                            translationX.value,
                            velocity
                        )
                        coroutineScope.launch {
                            val targetX = if (decayX > drawerWidth * 0.5) drawerWidth
                            else 0f

                            val canReachTargetWithDecay =
                                (decayX > targetX && targetX == drawerWidth)
                                        || (decayX < targetX && targetX == 0f)

                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity,
                                    animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(targetX, initialVelocity = velocity)
                            }
                            drawerState = if (targetX == drawerWidth) NavigationDrawerState.Open
                            else NavigationDrawerState.Closed
                        }
                    }
                )
                .fillMaxSize(),
            shadowElevation = 6.dp
        ) {
            ScreenContents(
                modifier = Modifier
                    .fillMaxSize(),
                navController = secondaryNavController,
                toggleNavigationDrawer = {
                    toggleDrawerState()
                }
            )
        }
    }
}

@Composable
private fun ScreenContents(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    toggleNavigationDrawer: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "posts-graph"
    ) {
        navigation(
            route = "posts-graph",
            startDestination = "posts-list-screen" //TODO
        ) {
            composable("posts-list-screen") {
                PostsListScreen(
                    navController = navController,
                    navigationButtonPressed = { toggleNavigationDrawer.invoke() }
                )
            }
            composable(
                route = "edit-posts-screen?postId={postId}",
                arguments = listOf(navArgument("postId") {nullable = true})
            ) {backStackEntry ->
                EditPostScreen(
                    postId = backStackEntry.arguments?.getString("postId"),
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}