package com.baarton.runweather.ui


private const val ICY_BLUE: Long = 0xFF3993DD
private const val YELLOWY: Long = 0xFFC5D86D
private const val ORANGEY: Long = 0xFFFF934F
private const val REDDISH: Long = 0xFFBA1200
private const val GREENY: Long = 0xFF4CB963

private const val BLACK: Long = 0xFF000000
private const val WHITE: Long = 0xFFFFFFFF
private const val RED: Long = 0xFFFF0000
private const val PATRIARCH: Long = 0xFF741085
private const val GLOSSY_GRAPE: Long = 0xFFA499BE
private const val STEEL_TEAL: Long = 0xFF488286
private const val GRANNY_SMITH_APPLE: Long = 0xFF88D18A
private const val ALICE_BLUE: Long = 0xFFECF0F8


enum class ThemedColor(val light: Long, val dark: Long = light) {
    PRIMARY(PATRIARCH),
    PRIMARY_VARIANT(GLOSSY_GRAPE),
    SECONDARY(STEEL_TEAL),
    SECONDARY_VARIANT(GRANNY_SMITH_APPLE),
    ON_PRIMARY(ALICE_BLUE),
    ON_SECONDARY(ALICE_BLUE),
    ERROR(RED),
    ON_ERROR(ALICE_BLUE),
    SURFACE(WHITE, BLACK),
    ON_SURFACE(BLACK, WHITE),
    BACKGROUND(WHITE, BLACK),
    ON_BACKGROUND(BLACK, WHITE),
    ICY_BLUE(com.baarton.runweather.ui.ICY_BLUE),
    YELLOWY(com.baarton.runweather.ui.YELLOWY),
    ORANGEY(com.baarton.runweather.ui.ORANGEY),
    REDDISH(com.baarton.runweather.ui.REDDISH),
    GREENY(com.baarton.runweather.ui.GREENY)
}


expect interface PlatformColor<T> {

    fun ThemedColor.light(): T
    fun ThemedColor.dark(): T

}