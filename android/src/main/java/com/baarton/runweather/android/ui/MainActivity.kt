package com.baarton.runweather.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.baarton.runweather.android.ui.composables.MainScreen
import com.baarton.runweather.android.ui.theme.RunWeatherTheme
import org.koin.core.component.KoinComponent

//TEST tests with composables?
class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        //ANDROID resolve installSplashScreen()
        // https://developersancho.medium.com/jetpack-compose-splash-screen-api-36ca40c6196b
        // https://proandroiddev.com/animated-splash-screen-in-android-with-compose-4b7dc1baecc5
        super.onCreate(savedInstanceState)

        setContent {
            RunWeatherTheme {
                MainScreen()
            }
        }
    }

}