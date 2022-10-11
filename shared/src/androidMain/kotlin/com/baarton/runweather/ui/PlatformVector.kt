package com.baarton.runweather.ui


actual interface PlatformVector<T> {

    actual fun Vector.build(): T
}