package com.glazovnet.glazovnetapp.core.presentation.homescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.glazovnet.glazovnetapp.announcements.presentation.create.CreateAnnouncementScreen
import com.glazovnet.glazovnetapp.announcements.presentation.list.AnnouncementsListScreen
import com.glazovnet.glazovnetapp.core.presentation.navigationdrawer.NavigationDrawer
import com.glazovnet.glazovnetapp.core.presentation.navigationdrawer.NavigationDrawerState
import com.glazovnet.glazovnetapp.innerposts.presentation.create.CreateInnerPostScreen
import com.glazovnet.glazovnetapp.innerposts.presentation.posts.InnerPostsScreen
import com.glazovnet.glazovnetapp.personalaccount.presentation.PersonalAccountScreen
import com.glazovnet.glazovnetapp.personalaccount.presentation.balancehistory.BalanceHistoryScreen
import com.glazovnet.glazovnetapp.posts.presentation.details.PostDetailsScreen
import com.glazovnet.glazovnetapp.posts.presentation.edit.EditPostScreen
import com.glazovnet.glazovnetapp.posts.presentation.list.PostsListScreen
import com.glazovnet.glazovnetapp.services.presentation.ServicesScreen
import com.glazovnet.glazovnetapp.settings.appearance.presentation.AppearanceSettingsScreen
import com.glazovnet.glazovnetapp.settings.core.presentation.mainscreen.SettingsScreen
import com.glazovnet.glazovnetapp.settings.notifications.presentation.NotificationsSettingsScreen
import com.glazovnet.glazovnetapp.supportrequests.presentation.chat.ChatScreen
import com.glazovnet.glazovnetapp.supportrequests.presentation.createrequest.CreateRequestScreen
import com.glazovnet.glazovnetapp.supportrequests.presentation.requestdetails.RequestDetailsScreen
import com.glazovnet.glazovnetapp.supportrequests.presentation.requests.RequestsListScreen
import com.glazovnet.glazovnetapp.tariffs.presentation.list.TariffsListScreen
import kotlinx.coroutines.launch

private const val DEEPLINK_PREFIX = "https://glazov.net"

@Composable
fun HomeScreen(
    onNavigateToLoginScreen: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    val isUserIsEmployee = viewModel.hasEmployeeAccess
    val employeeRoles = viewModel.employeeRoles
    val isUserIsClient = viewModel.hasClientAccess

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
        val navControllerRoute = secondaryNavController.currentBackStackEntryAsState()
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
            currentScreenRoute = navControllerRoute.value?.destination?.parent?.route ?: "",
            isUserIsClient = isUserIsClient,
            isUserIsEmployee = isUserIsEmployee,
            employeeRoles = employeeRoles,
            onNavigateOnHomeScreen = { route ->
                val currentRoute = secondaryNavController.currentBackStackEntry?.destination?.parent?.route
                secondaryNavController.navigate(route) {
                    if (currentRoute != null) {
                        popUpTo(currentRoute) {inclusive = true}
                    }
                }
            },
            onNavigateOnMainScreen = {
                onNavigateToLoginScreen.invoke()
            }
        )
        Surface(
            modifier = Modifier
                .graphicsLayer {
                    this.clip = true
                    this.translationX = translationX.value
                    val scale: Float = lerp(1f, 0.85f, translationX.value / drawerWidth)
                    val cornerRadius =
                        androidx.compose.ui.unit.lerp(0.dp, 12.dp, translationX.value / drawerWidth)
                    this.scaleY = scale
                    this.scaleX = scale
                    this.shape = RoundedCornerShape(cornerRadius)
                    this.shadowElevation = 12f
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
            color = MaterialTheme.colorScheme.surface
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
            startDestination = "posts-list-screen",
        ) {
            composable(
                route = "posts-list-screen",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/posts-list"
                    }
                )
            ) {
                PostsListScreen(
                    onNavigationButtonPressed = { toggleNavigationDrawer.invoke() },
                    onNavigationToEditPostScreen =  { (navController.navigate("edit-posts-screen?postId=$it")) },
                    onNavigationToPostDetails = { postId ->
                        navController.navigate("post-details-screen/$postId")
                    }
                )
            }
            composable(
                route = "post-details-screen/{postId}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/posts/{postId}"
                    }
                ),
                arguments = listOf(
                    navArgument("postId") {
                        type = NavType.StringType
                    }
                )
            ) {
                PostDetailsScreen(
                    postId = it.arguments?.getString("postId") ?: "",
                    onNavigationButtonClicked = { navController.popBackStack() },
                    onEditPostButtonClicked = { postId ->
                        navController.navigate("edit-posts-screen?postId=$postId")
                    },
                    onPostDeleted = {
                        navController.popBackStack()
                    }
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
        navigation(
            route = "personal-account-graph",
            startDestination = "personal-account-screen"
        ) {
            composable(
                route = "personal-account-screen",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/personal-account"
                    }
                )
            ) {
                PersonalAccountScreen(
                    onNavigationButtonPressed = toggleNavigationDrawer,
                    onOpenBalanceHistoryScreen = {
                        navController.navigate("balance-history-screen")
                    },
                    onOpenNotificationsSettings = {

                    },
                    onOpenTariffsScreen = {
                        navController.navigate("tariffs-graph")
                    },
                    onOpenServicesScreen = {
                        navController.navigate("services-graph")
                    }
                )
            }
            composable("balance-history-screen") {
                BalanceHistoryScreen(
                    onBackButtonPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
        navigation(
            route = "support-graph",
            startDestination = "requests-list-screen"
        ) {
            composable(
                route = "requests-list-screen",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/requests-list"
                    }
                )
            ) {
                RequestsListScreen(
                    onNavigationButtonClicked = { toggleNavigationDrawer.invoke() },
                    onAddNewRequestClicked = {
                        navController.navigate("create-request-screen")
                    },
                    onRequestClicked = {requestId ->
                        navController.navigate("request-details-screen/$requestId")
                    }
                )
            }
            composable(
                route = "request-details-screen/{requestId}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/requests/{requestId}"
                    }
                ),
                arguments = listOf(
                    navArgument("requestId") {
                        type = NavType.StringType
                    }
                )
            ) {
                RequestDetailsScreen(
                    requestId = it.arguments?.getString("requestId") ?: "",
                    onNavigationButtonPressed = { navController.popBackStack() },
                    onOpenChatButtonPressed = { requestId ->
                        navController.navigate("request-chat-screen/$requestId")
                    }
                )
            }
            composable(
                route = "request-chat-screen/{requestId}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/requests/{requestId}/chat"
                    }
                ),
                arguments = listOf(
                    navArgument("requestId") {
                        type = NavType.StringType
                    }
                )
            ) {
                ChatScreen(
                    requestId = it.arguments?.getString("requestId") ?: "",
                    onNavigationButtonPressed = { navController.popBackStack() }
                )
            }
            composable("create-request-screen") {
                CreateRequestScreen(
                    onNavigationButtonClicked = { navController.popBackStack() }
                )
            }
        }
        navigation(
            startDestination = "tariffs-list-screen",
            route = "tariffs-graph"
        ) {
            composable(
                route = "tariffs-list-screen",
                deepLinks = listOf(
//                    navDeepLink {
//                        uriPattern = "$DEEPLINK_PREFIX/tariffs/{tariffId}"
//                    },
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/tariffs-list"
                    }
                ),
                arguments = listOf(navArgument("tariffId") {nullable = true})
            ) {
//                val tariffId = it.arguments?.getString("tariffId")
                TariffsListScreen(
//                    optionalTariffId = tariffId,
                    onNavigationButtonPressed = { toggleNavigationDrawer.invoke() }
                )
            }
        }
        navigation(
            startDestination = "services-screen",
            route = "services-graph"
        ) {
            composable(route = "services-screen") {
                ServicesScreen(
                    onNavigationButtonPressed = {
                        toggleNavigationDrawer.invoke()
                    }
                )
            }
        }
        navigation(
            startDestination = "announcements-list-screen",
            route = "announcements-graph"
        ) {
            composable(
                route = "announcements-list-screen",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/announcements-list"
                    }
                )
            ) {
                AnnouncementsListScreen(
                    onNavigationButtonPressed = {
                        toggleNavigationDrawer.invoke()
                    },
                    onCreateNewAnnouncementClicked = {
                        navController.navigate("add-announcement-screen")
                    })
            }
            composable("add-announcement-screen") {
                CreateAnnouncementScreen(
                    onNavigationButtonPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
        navigation(
            startDestination = "inner-posts-screen",
            route = "inner-posts-graph"
        ) {
            composable(
                route = "inner-posts-screen",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$DEEPLINK_PREFIX/service-posts"
                    }
                )
            ) {
                InnerPostsScreen(
                    onNavigationButtonClicked = {
                        toggleNavigationDrawer.invoke()
                    },
                    onAddNewInnerPostClicked = {
                        navController.navigate("add-inner-post-screen")
                    }
                )
            }
            composable("add-inner-post-screen") {
                CreateInnerPostScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
        navigation(
            startDestination = "settings-main-screen",
            route = "settings-graph"
        ) {
            composable("settings-main-screen") {
                SettingsScreen(
                    onNavigationButtonPressed = {
                        toggleNavigationDrawer.invoke()
                    },
                    onNavigateToNotificationsScreen = {
                        navController.navigate("notifications-settings")
                    },
                    onNavigateToAppearanceScreen = {
                        navController.navigate("appearance-settings")
                    },
                    onNavigateToInfoScreen = {

                    }
                )
            }
            composable("notifications-settings") {
                NotificationsSettingsScreen(
                    onNavigationButtonPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable("appearance-settings") {
                AppearanceSettingsScreen(
                    onNavigationButtonPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}