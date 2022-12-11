package com.baarton.runweather.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.baarton.runweather.ui.composables.MainScreen
import com.baarton.runweather.ui.composables.SplashScreen


sealed class Screen(val route: String) {

    companion object {
        const val SPLASH_SCREEN_ROUTE_ID = "splash_screen"
        const val MAIN_SCREEN_ROUTE_ID = "home_screen"
    }

    object Splash : Screen(SPLASH_SCREEN_ROUTE_ID)
    object Main : Screen(MAIN_SCREEN_ROUTE_ID)
}

@Composable
fun RunWeatherNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) { SplashScreen() }
        composable(route = Screen.Main.route) { MainScreen() }
    }
}