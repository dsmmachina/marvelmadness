package com.dsmmachina.marvelmadness

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.rule.ActivityTestRule
import com.dsmmachina.marvelmadness.activities.MainActivity
import com.dsmmachina.marvelmadness.api.Api
import com.dsmmachina.marvelmadness.models.ComicModel
import com.dsmmachina.marvelmadness.BuildConfig
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

/**
 * This test class uses an idling resource so that we can allow things to load for a short while before
 * continuing to the next step in the test. Because of time constraints for this assignment, I decided to
 * simply allow the tests to run in a 'production' environment rather than mocking the response.
 */
open class AutomatedTestClass {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    lateinit var context : Context

    @Before
    fun start() {
        context = activityRule.activity.applicationContext
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoSemaphore.idlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoSemaphore.idlingResource)
    }




    @Test
    fun testAppPackage() {
        assertNotEquals(context, null)
        assertTrue(Api.isAuthorized)
        assertEquals(BuildConfig.APPLICATION_ID, context.packageName)
    }

    @Test
    fun testMainActivityLaunch() {

        IdlingPolicies.setMasterPolicyTimeout(3, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES)

        val idlingResource = IdleManager(5000)
        Espresso.registerIdlingResources(idlingResource)

        Thread.sleep(5000)

        assertTrue(activityRule.activity.supportFragmentManager.fragments.isEmpty())
        assertTrue((activityRule.activity?.getComicAdapter()?.itemCount ?: 0) > 0)
    }

    @Test
    fun testComicInformationFragment() {

        IdlingPolicies.setMasterPolicyTimeout(3, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES)

        val idlingResource = IdleManager(5000)
        Espresso.registerIdlingResources(idlingResource)

        Thread.sleep(1000)

        activityRule.activity.ComicClickObserver().onNext(ComicModel())

        Thread.sleep(2000)

        assertEquals(activityRule.activity.supportFragmentManager.fragments.size, 1)

    }
}
