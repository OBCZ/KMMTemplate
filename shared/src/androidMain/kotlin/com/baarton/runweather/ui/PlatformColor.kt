package com.baarton.runweather.ui


actual interface PlatformColor<T> {
    actual fun ThemedColor.light(): T
    actual fun ThemedColor.dark(): T
}