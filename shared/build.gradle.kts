plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("dev.icerock.mobile.multiplatform-resources")
}

version = libs.versions.versionName.get()

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    //TODO review options here https://developer.android.com/reference/tools/gradle-api/4.1/com/android/build/api/dsl/LintOptions
    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
    namespace = "com.baarton.runweather"
}

//TODO verify function on iOS, see also
// https://proandroiddev.com/exposing-the-separate-resources-module-to-ios-target-using-moko-resources-in-kmm-76b9c3d533
// https://github.com/icerockdev/moko-resources
multiplatformResources {
    multiplatformResourcesPackage = "${libs.versions.appId.get()}.res" // required
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
}

kotlin {
    android()
    ios()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines.core)
                implementation(libs.sqlDelight.coroutinesExt)
                implementation(libs.bundles.ktor.common)
                implementation(libs.touchlab.stately)
                implementation(libs.multiplatformSettings.common)
                implementation(libs.kotlinx.dateTime)
                api(libs.touchlab.kermit)
                api(libs.mokoResources.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.shared.commonTest)
                implementation(libs.mokoResources.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.sqlDelight.android)
                implementation(libs.ktor.client.okHttp)
                api(libs.mokoResources.compose)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.bundles.shared.androidTest)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelight.native)
                implementation(libs.ktor.client.ios)
            }
        }
        val iosTest by getting
    }

    sourceSets.matching { it.name.endsWith("Test") }
        .configureEach {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

    cocoapods {
        summary = "RunWeather Android/iOS app"
        homepage = "https://github.com/OBCZ/RunWeatherKmmApp"
        framework {
            isStatic = false // SwiftUI preview requires dynamic framework
        }
        ios.deploymentTarget = "12.4"
        podfile = project.file("../ios/Podfile")
    }
}

sqldelight {
    database("RunWeatherDb") {
        packageName = "${libs.versions.appId.get()}.db"
    }
}
