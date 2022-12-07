package com.baarton.runweather

interface AppInfo {

    /**
     * An application id. Implementation is platform specific, but it is defined on project level in gradle.
     */
    val appId: String

    /**
     * A application version name used for UI purposes. Implementation is platform specific, but it is defined on project level in gradle.
     */
    val versionName: String

    /**
     * A application version code used for release purposes. Implementation is platform specific, but it is computed during build time via a gradle task.
     */
    val versionCode: Int

    /**
     * Denotes whether the application is or is not in DEBUG mode. Implementation is platform specific.
     */
    val debug: Boolean
}
