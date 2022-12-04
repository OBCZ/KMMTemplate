package com.baarton.runweather.ui


//IOS https://swiftuirecipes.com/blog/supporting-dark-mode-in-swiftui
actual interface PlatformColor<T> {

    actual fun ThemedColor.light(): T
    actual fun ThemedColor.dark(): T
}