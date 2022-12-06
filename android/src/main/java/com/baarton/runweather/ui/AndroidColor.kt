package com.baarton.runweather.ui

import androidx.compose.ui.graphics.Color


object AndroidColor : PlatformColor<Color> {

    override fun ThemedColor.light(): Color {
        return Color(this.light)
    }

    override fun ThemedColor.dark(): Color {
        return Color(this.dark)
    }
}