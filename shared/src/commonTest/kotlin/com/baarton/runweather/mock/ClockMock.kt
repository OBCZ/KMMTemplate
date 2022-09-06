package com.baarton.runweather.mock

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


class ClockMock : Clock {

    private var mockedInstant: Instant? = null

    override fun now(): Instant = mockedInstant ?: Clock.System.now()

    fun mockClock(input: Instant?) {
        mockedInstant = input
    }

    fun getMockedClock(): Instant? {
        return mockedInstant
    }

}