package com.baarton.runweather.android.ui

import androidx.compose.ui.graphics.Color
import com.baarton.runweather.ui.PlatformColor
import com.baarton.runweather.ui.ThemedColor


object AndroidColor : PlatformColor<Color> {

    override fun ThemedColor.light(): Color {
        return Color(this.light)
    }

    override fun ThemedColor.dark(): Color {
        return Color(this.dark)
    }
}