package com.baarton.runweather

import com.baarton.runweather.ktor.DogApi
import com.baarton.runweather.ktor.DogApiImpl
import com.baarton.runweather.models.WeatherRepository
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module


//TODO first make Android run
//TODO don't use gson for serialization
//TODO then start extracting business logic to common
//TODO then start duplicating/mimicking UI stuff, platform specific stuff
//TODO then review all files and its origin/contents
//TODO verify architecture somehow, review dependencies

//TODO after migration:
// review logging messages, exceptions, nullability, edge cases
// review all files (formatting, comments, naming variables/files, etc)
// extracting/moving stuff/files further
// review tests
// get iOS working

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule
        )
    }

    // Dummy initialization logic, making use of appModule declarations for demonstration purposes.
    val koin = koinApplication.koin
    // doOnStartup is a lambda which is implemented in Swift on iOS side
    val doOnStartup = koin.get<() -> Unit>()
    doOnStartup.invoke()

    val kermit = koin.get<Logger> { parametersOf(null) }
    // AppInfo is a Kotlin interface with separate Android and iOS implementations
    val appInfo = koin.get<AppInfo>()
    kermit.v { "App Id ${appInfo.appId}" }

    return koinApplication
}

private val coreModule = module {
    single {
        DatabaseHelper(
            get(),
            getWith("DatabaseHelper"),
            Dispatchers.Default
        )
    }
    single<DogApi> {
        DogApiImpl(
            getWith("DogApiImpl"),
            get()
        )
    }
    single<Clock> {
        Clock.System
    }

    //TODO investigate
    // platformLogWriter() is a relatively simple config option, useful for local debugging. For production
    // uses you *may* want to have a more robust configuration from the native platform. In KaMP Kit,
    // that would likely go into platformModule expect/actual.
    // See https://github.com/touchlab/Kermit
    val baseLogger = Logger(config = StaticConfig(logWriterList = listOf(platformLogWriter())), "RunWeather")
    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }

    single {
        WeatherRepository(
            get(),
            get(),
            get(),
            getWith("BreedRepository"),
            get()
        )
    }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

// Simple function to clean up the syntax a bit
fun KoinComponent.injectLogger(tag: String): Lazy<Logger> = inject { parametersOf(tag) }

expect val platformModule: Module
