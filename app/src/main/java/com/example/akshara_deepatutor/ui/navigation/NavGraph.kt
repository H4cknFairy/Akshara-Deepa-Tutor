package com.example.akshara_deepatutor.ui.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.akshara_deepatutor.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Subject : Screen("subject")
    
    object Chapter : Screen("chapter/{subjectId}/{subjectName}") {
        fun createRoute(subjectId: Int, subjectName: String) = 
            "chapter/$subjectId/${Uri.encode(subjectName)}"
    }
    
    object ChapterContent : Screen("chapter_content/{chapterId}") {
        fun createRoute(chapterId: Int) = "chapter_content/$chapterId"
    }
    
    object Quiz : Screen("quiz/{chapterId}/{subjectName}") {
        fun createRoute(chapterId: Int, subjectName: String = "none") = 
            "quiz/$chapterId/${Uri.encode(subjectName)}"
    }
    
    object Result : Screen("result/{score}") {
        fun createRoute(score: Int) = "result/$score"
    }
    
    object Dashboard : Screen("dashboard")
    object Reminders : Screen("reminders")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object QuizHistory : Screen("quiz_history")
    object CompletedChapters : Screen("completed_chapters")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showNavigation = currentRoute in listOf(
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Subject.route,
        Screen.Dashboard.route,
        Screen.Reminders.route,
        Screen.QuizHistory.route,
        Screen.CompletedChapters.route,
    )

    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact

    if (showNavigation && !isCompact) {
        // Tablet/Expanded Layout with Navigation Rail
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                header = {
                    // Optional logo or header
                }
            ) {
                NavigationRailItem(
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        if (currentRoute != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationRailItem(
                    selected = currentRoute == Screen.Profile.route,
                    onClick = {
                        if (currentRoute != Screen.Profile.route) {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
            NavHostContainer(navController, windowWidthSizeClass, Modifier.weight(1f))
        }
    } else {
        // Phone Layout or Screens without Nav Rail
        NavHostContainer(navController, windowWidthSizeClass, Modifier.fillMaxSize())
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController, windowWidthSizeClass)
        }
        
        composable(Screen.Subject.route) {
            SubjectScreen(navController, windowWidthSizeClass)
        }
        
        composable(
            route = Screen.Chapter.route,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.IntType; defaultValue = 0 },
                navArgument("subjectName") { type = NavType.StringType; defaultValue = "Subject" }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: "Subject"
            ChapterScreen(navController, subjectId, subjectName, windowWidthSizeClass)
        }
        
        composable(
            route = Screen.ChapterContent.route,
            arguments = listOf(navArgument("chapterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getInt("chapterId") ?: -1
            ChapterContentScreen(navController, chapterId)
        }
        
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("chapterId") { type = NavType.IntType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getInt("chapterId") ?: -1
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: "none"
            QuizScreen(navController, chapterId, subjectName, windowWidthSizeClass)
        }
        
        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ResultScreen(navController, score)
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, windowWidthSizeClass)
        }

        composable(Screen.Reminders.route) {
            RemindersScreen(navController, windowWidthSizeClass)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController, windowWidthSizeClass)
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
        
        composable(Screen.QuizHistory.route) {
            QuizHistoryScreen(navController)
        }
        
        composable(Screen.CompletedChapters.route) {
            CompletedChaptersScreen(navController)
        }
    }
}
