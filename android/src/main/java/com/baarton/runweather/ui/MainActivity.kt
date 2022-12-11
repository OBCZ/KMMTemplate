package com.baarton.runweather.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.baarton.runweather.SPLASH_SCREEN_DELAY
import com.baarton.runweather.nav.Screen
import com.baarton.runweather.nav.RunWeatherNavHost
import com.baarton.runweather.ui.theme.RunWeatherTheme
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent


//TEST tests with composables?
class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RunWeatherTheme {
                val navController = rememberNavController()
                RunWeatherNavHost(navController = navController)

                lifecycleScope.launchWhenCreated {
                    delay(SPLASH_SCREEN_DELAY)
                    navController.navigate(Screen.Main.route)
                }
            }
        }
    }

}