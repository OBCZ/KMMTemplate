package com.baarton.runweather.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.baarton.runweather.AppInfo
import com.baarton.runweather.Config
import com.baarton.runweather.getWith
import com.baarton.runweather.initKoin
import com.baarton.runweather.model.viewmodel.SettingsViewModel
import com.baarton.runweather.model.viewmodel.WeatherViewModel
import com.baarton.runweather.android.network.AndroidNetwork
import com.baarton.runweather.network.PlatformNetwork
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }

                //TODO define network nad location platform specific definitions -> move to platformModule?
                // single<PlatformLocationManager> { AndroidLocationManager(get()) }

                single<PlatformNetwork> { AndroidNetwork(get(), getWith(AndroidNetwork::class.simpleName)) }

                viewModel { WeatherViewModel(get(), get(), get(), get(), get(), get { parametersOf(WeatherViewModel::class.simpleName) }) }
                viewModel { SettingsViewModel(get(), get(), get(), get { parametersOf(SettingsViewModel::class.simpleName) }) }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(get<Config>().preferences, Context.MODE_PRIVATE)
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Koin Startup", "Android modules initialized") }
                }
            }
        )
    }

}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
    override val versionName: String = BuildConfig.VERSION_NAME
    override val versionCode: Int = BuildConfig.VERSION_CODE
    override val debug: Boolean = BuildConfig.DEBUG
}
