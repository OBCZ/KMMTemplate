package com.baarton.runweather.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp


object AndroidVector : PlatformVector<ImageVector> {

    override fun Vector.build(): ImageVector {
        return with(this) {
            ImageVector.Builder(
                name = name,
                defaultWidth = width.dp,
                defaultHeight = height.dp,
                viewportWidth = viewportWidth.toFloat(),
                viewportHeight = viewportHeight.toFloat(),
            ).addPath(
                // keep the stroke and fill at all times
                stroke = SolidColor(Color(color.light)),
                fill = SolidColor(Color(color.light)),
                pathData = addPathNodes(path)
            ).build()
        }
    }
}