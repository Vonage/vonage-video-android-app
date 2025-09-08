package com.vonage.android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
abstract class CoroutineTest {

    protected val testScheduler = TestCoroutineScheduler()

    protected val testDispatcherWithScheduler = StandardTestDispatcher(testScheduler)

    protected fun setMainDispatcherToTestDispatcher() {
        Dispatchers.setMain(testDispatcherWithScheduler)
    }

    protected fun resetMain() {
        Dispatchers.resetMain()
    }
}
