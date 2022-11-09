package com.baarton.runweather.ktor

interface ImageDataApi {

    fun buildUrl(imageId: String): String
}