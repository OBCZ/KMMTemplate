package com.baarton.runweather

import com.baarton.runweather.db.RunWeatherDb
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.ObservableSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            RunWeatherDb.Schema,
            get(),
            "RunWeatherDb"
        )
    }

    single<ObservableSettings> {
        SharedPreferencesSettings(get())
    }

    single {
        OkHttp.create()
    }
}
