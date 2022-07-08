package com.baarton.runweather.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.baarton.runweather.AppInfo
import com.baarton.runweather.android.BuildConfig
import com.baarton.runweather.initKoin
import com.baarton.runweather.models.BreedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }

                //TODO define network nad location platform specific definitions
                // single<PlatformNetworkManager> { AndroidNetworkManager(get()) }
                // single<PlatformLocationManager> { AndroidLocationManager(get()) }

                viewModel { BreedViewModel(get(), get { parametersOf("BreedViewModel") }) }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("RUNWEATHER_SETTINGS", Context.MODE_PRIVATE)
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
