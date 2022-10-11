package com.baarton.runweather.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.baarton.runweather.android.ui.AndroidColor.dark
import com.baarton.runweather.android.ui.AndroidColor.light
import com.baarton.runweather.ui.ThemedColor.BACKGROUND
import com.baarton.runweather.ui.ThemedColor.ERROR
import com.baarton.runweather.ui.ThemedColor.ON_BACKGROUND
import com.baarton.runweather.ui.ThemedColor.ON_ERROR
import com.baarton.runweather.ui.ThemedColor.ON_PRIMARY
import com.baarton.runweather.ui.ThemedColor.ON_SECONDARY
import com.baarton.runweather.ui.ThemedColor.ON_SURFACE
import com.baarton.runweather.ui.ThemedColor.PRIMARY
import com.baarton.runweather.ui.ThemedColor.PRIMARY_VARIANT
import com.baarton.runweather.ui.ThemedColor.SECONDARY
import com.baarton.runweather.ui.ThemedColor.SECONDARY_VARIANT
import com.baarton.runweather.ui.ThemedColor.SURFACE


private val LightColorPalette = lightColors(
    primary = PRIMARY.light(),
    primaryVariant = PRIMARY_VARIANT.light(),
    secondary = SECONDARY.light(),
    secondaryVariant = SECONDARY_VARIANT.light(),
    onPrimary = ON_PRIMARY.light(),
    onSecondary = ON_SECONDARY.light(),
    error = ERROR.light(),
    onError = ON_ERROR.light(),
    surface = SURFACE.light(),
    onSurface = ON_SURFACE.light(),
    background = BACKGROUND.light(),
    onBackground = ON_BACKGROUND.light(),
)

private val DarkColorPalette = darkColors(
    primary = PRIMARY.dark(),
    primaryVariant = PRIMARY_VARIANT.dark(),
    secondary = SECONDARY.dark(),
    secondaryVariant = SECONDARY_VARIANT.dark(),
    onPrimary = ON_PRIMARY.dark(),
    onSecondary = ON_SECONDARY.dark(),
    error = ERROR.dark(),
    onError = ON_ERROR.dark(),
    surface = SURFACE.dark(),
    onSurface = ON_SURFACE.dark(),
    background = BACKGROUND.dark(),
    onBackground = ON_BACKGROUND.dark(),
)

@Composable
fun RunWeatherTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography, //TODO can be platform-independently defined if in Material Design
        shapes = Shapes, //TODO can be platform-independently defined with PT/DP claim
        content = content
    )
}
