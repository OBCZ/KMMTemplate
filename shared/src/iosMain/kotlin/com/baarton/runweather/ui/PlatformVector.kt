package com.baarton.runweather.ui


//IOS https://github.com/SVGKit/SVGKit
actual interface PlatformVector<T> {

    actual fun Vector.build(): T
}