package com.glazovnet.glazovnetapp.presentation.homescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.glazovnet.glazovnetapp.presentation.navigationdrawer.NavigationDrawerState
import com.glazovnet.glazovnetapp.presentation.posts.edit.EditPostScreen
import com.glazovnet.glazovnetapp.presentation.posts.list.PostsListScreen
import kotlinx.coroutines.launch

private const val DRAWER_WIDTH = 480f

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
        val secondaryNavController = rememberNavController()
        val translationX = remember {
            Animatable(0f)
        }
        var cornerRadius by remember {
            mutableStateOf(0.dp)
        }
        val decay = rememberSplineBasedDecay<Float>()
        translationX.updateBounds(0f, DRAWER_WIDTH)
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
                    translationX.animateTo(DRAWER_WIDTH)
                }
                drawerState = if (drawerState == NavigationDrawerState.Open) NavigationDrawerState.Closed
                else NavigationDrawerState.Open
            }
        }

        NavigationDrawer(state = drawerState)
        Surface(
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale: Float = lerp(1f, 0.85f, translationX.value / DRAWER_WIDTH)
                    cornerRadius = androidx.compose.ui.unit.lerp(
                        0.dp,
                        12.dp,
                        translationX.value / DRAWER_WIDTH
                    )
                    this.scaleY = scale
                    this.scaleX = scale
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
                            val targetX = if (decayX > DRAWER_WIDTH * 0.5) DRAWER_WIDTH
                            else 0f

                            val canReachTargetWithDecay =
                                (decayX > targetX && targetX == DRAWER_WIDTH)
                                        || (decayX < targetX && targetX == 0f)

                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity,
                                    animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(targetX, initialVelocity = velocity)
                            }
                            drawerState = if (targetX == DRAWER_WIDTH) NavigationDrawerState.Open
                            else NavigationDrawerState.Closed
                        }
                    }
                )
                .fillMaxSize(),
            shape = RoundedCornerShape(cornerRadius),
            shadowElevation = 6.dp
        ) {
            ScreenContents(
                modifier = Modifier
                    .fillMaxSize(),
                navController = secondaryNavController
            )
        }
    }
}

@Composable
private fun NavigationDrawer(
    modifier: Modifier = Modifier,
    state: NavigationDrawerState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        for (i in 0..3) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                onClick = {}
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    text = "button $i"
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ScreenContents(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize(),
        navController = navController,
        startDestination = "posts-graph"
    ) {
        navigation(
            route = "posts-graph",
            startDestination = "posts-list-screen" //TODO
        ) {
            composable("posts-list-screen") {
                PostsListScreen(navController = navController)
            }
            composable(
                route = "edit-posts-screen?postId={postId}",
                arguments = listOf(navArgument("postId") {nullable = true})
            ) {backStackEntry ->
                EditPostScreen(
                    navController = navController,
                    postId = backStackEntry.arguments?.getString("postId")
                )
            }
        }
    }
}