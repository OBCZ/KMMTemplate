package com.baarton.runweather

import app.cash.turbine.FlowTurbine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest


open class StateFlowTest {

    @BeforeTest
    open fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    open fun tearDown() {
        runBlocking {
            Dispatchers.resetMain()
        }
    }

    protected suspend fun <S> FlowTurbine<S>.awaitItemAfter(vararg items: S): S {
        var nextItem = awaitItem()
        for (item in items) {
            if (item == nextItem) {
                nextItem = awaitItem()
            }
        }
        return nextItem
    }

    protected suspend fun <S> FlowTurbine<S>.awaitItemAfterLast(item: S): S {
        val nextItem = awaitItem()
        if (item == nextItem) {
            awaitItemAfterLast(nextItem)
        }
        return awaitItem()
    }

}