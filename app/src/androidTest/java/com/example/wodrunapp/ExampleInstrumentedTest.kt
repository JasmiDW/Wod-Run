package com.example.wodrunapp


import androidx.test.espresso.Espresso.onView

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import java.util.regex.Pattern.matches

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(CrossfitActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }
    @Test
    fun testNavigationToRunActivity() {
        onView(withId(R.id.nav_run)).perform(click())
        intended(hasComponent(RunActivity::class.java.name))
    }

    @Test
    fun testNavigationToAddRecord() {
        onView(withId(R.id.button)).perform(click())
        intended(hasComponent(RecordActivity::class.java.name))
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.wodrunapp", appContext.packageName)
    }

    @After
    fun tearDown() {
        Intents.release()
    }
}