Resources Structure
*******************

MOKO-Resources
--------------
- Strings
- PNGs, JPEGs

Internal expect-actual logic
----------------------------
- Rest is being handled by Kotlin (shared module, package com.baarton.runweather.ui):
    - SVGs
    - Colors
    - Dimens (Android DP and iOS PT are supposed to be equal)
- TODO App icon is not decided on yet


IOS dont forget to export moko resources for iOS, whatever that is, but SharedRes wont probably work without that (https://proandroiddev.com/exposing-the-separate-resources-module-to-ios-target-using-moko-resources-in-kmm-76b9c3d533)