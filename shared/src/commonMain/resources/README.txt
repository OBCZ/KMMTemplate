Resources Structure
*******************

MOKO-Resources
--------------
- For now, only Strings

Internal expect-actual logic
----------------------------
- Rest is being handled by Kotlin (shared module, package com.baarton.runweather.ui):
    - SVGs
    - Colors
    - Dimens (Android DP and iOS PT are supposed to be equal)
- TODO PNGs are not decided on yet
- TODO App icon is not decided on yet


TODO dont forget to export moko resources for iOS, whatever that is, but SharedRes wont probably work without that (https://proandroiddev.com/exposing-the-separate-resources-module-to-ios-target-using-moko-resources-in-kmm-76b9c3d533)