package com.dsmmachina.marvelmadness

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger


/**
 * An simple counter implementation of that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle.
 */
class SimpleCountingIdlingResource
/**
 * Creates a SimpleCountingIdlingResource
 * @param resourceName the resource name this resource should report to Espresso.
 */
    (resourceName: String) : IdlingResource {

    private val mResourceName: String

    private val counter = AtomicInteger(0)

    // written from main thread, read from any thread.
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        mResourceName = checkNotNull(resourceName)
    }

    override fun getName(): String {
        return mResourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun lock() {
        counter.getAndIncrement()
    }

    /**
     * @throws IllegalStateException if the counter is below 0.
     */
    fun unlock() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (null != resourceCallback) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}