package com.baarton.runweather.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baarton.runweather.android.ui.AndroidColor.dark
import com.baarton.runweather.android.ui.AndroidColor.light
import com.baarton.runweather.ui.Dimens
import com.baarton.runweather.ui.ThemedColor.*


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

//TODO can this be platform-independently defined if in Material Design also for iOS somehow?
private val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

private val Shapes = Shapes(
    small = RoundedCornerShape(Dimens.ANDROID_SHAPE_ROUND_SMALL.dp),
    medium = RoundedCornerShape(Dimens.ANDROID_SHAPE_ROUND_MEDIUM.dp),
    large = RoundedCornerShape(Dimens.ANDROID_SHAPE_ROUND_LARGE.dp)
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
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}