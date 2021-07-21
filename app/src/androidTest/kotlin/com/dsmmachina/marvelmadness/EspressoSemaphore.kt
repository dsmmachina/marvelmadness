package com.dsmmachina.marvelmadness

import androidx.test.espresso.IdlingResource

object EspressoSemaphore {

    private val RESOURCE = "GLOBAL"

    private val mCountingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    val idlingResource: IdlingResource
        get() = mCountingIdlingResource

    fun lock() {

        if(mCountingIdlingResource.isIdleNow)
            mCountingIdlingResource.lock()
    }

    fun unlock() {

        if(!mCountingIdlingResource.isIdleNow)
            mCountingIdlingResource.unlock()
    }
}