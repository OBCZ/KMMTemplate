package com.baarton.runweather

import com.squareup.sqldelight.db.SqlDriver

internal expect fun testDbConnection(): SqlDriver
