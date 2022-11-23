package com.baarton.runweather

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.baarton.runweather.ktor.WeatherDataApi
import com.baarton.runweather.ktor.WeatherDataApiImpl
import com.baarton.runweather.network.NetworkManager
import com.baarton.runweather.repo.WeatherRepository
import com.baarton.runweather.sqldelight.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

//TODO migration plan:
// review all files (origin/contents, formatting, comments, naming variables/files, etc)
// fork to RunWeather GH - former repo
// create simple CI/CD
// verify Android release signature
// get iOS working + extracting/moving stuff/files further
// define simple formatting rules with ktlint?
// go through Medium bookmark tips and fine-tune the code
// go through and review TODOs and create Trello tickets from them

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule
        )
    }

    val koin = koinApplication.koin
    val kermit = koin.get<Logger> { parametersOf(null) }
    val appInfo = koin.get<AppInfo>()
    kermit.v {
        "Modules initialized.\n" +
            "App Id: ${appInfo.appId}\n" +
            "DEBUG: ${appInfo.debug}"
    }

    return koinApplication
}

private val coreModule = module {
    single {
        NetworkManager(
            get(),
            getWith(NetworkManager::class.simpleName)
        )
    }

    single {
        DatabaseHelper(
            get(),
            Dispatchers.Default,
            getWith(DatabaseHelper::class.simpleName)
        )
    }

    single<WeatherDataApi> {
        WeatherDataApiImpl(
            get(),
            getWith(WeatherDataApiImpl::class.simpleName)
        )
    }

    single {
        WeatherRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            getWith(WeatherRepository::class.simpleName)
        )
    }

    single<Clock> {
        Clock.System
    }

    single<Config> {
        AppConfig(get())
    }

    //TODO investigate
    // platformLogWriter() is a relatively simple config option, useful for local debugging. For production
    // uses you *may* want to have a more robust configuration from the native platform. In KaMP Kit,
    // that would likely go into platformModule expect/actual.
    // See https://github.com/touchlab/Kermit
    val baseLogger = Logger(config = StaticConfig(logWriterList = listOf(platformLogWriter())), APP_TAG)
    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }
}

inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module
