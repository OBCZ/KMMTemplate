package com.baarton.runweather.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.baarton.runweather.android.ui.MainScreen
import com.baarton.runweather.android.ui.theme.RunWeatherTheme
import com.baarton.runweather.injectLogger
import com.baarton.runweather.models.BreedViewModel
import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val log: Logger by injectLogger("MainActivity")
    private val viewModel: BreedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunWeatherTheme {
                MainScreen(viewModel, log)
            }
        }
    }
}
