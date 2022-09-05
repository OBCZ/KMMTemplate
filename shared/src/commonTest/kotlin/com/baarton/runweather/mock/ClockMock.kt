package com.baarton.runweather.mock

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ClockMock : Clock {

    var mockedInstant: Instant? = null

    override fun now(): Instant = mockedInstant ?: Clock.System.now()
}
