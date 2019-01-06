package com.kmorawski.sampleapp.views

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kmorawski.sampleapp.InMemoryLogger
import com.kmorawski.sampleapp.Logger
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.views.mocks.beforeLaunchingActivity
import com.kmorawski.sampleapp.views.mocks.mockedListConfigModule
import com.kmorawski.sampleapp.views.mocks.mockedProfilesRetrieverModule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

class UiTest {
    fun mockedProfilesRetriever() = MockedProfilesRetriever(logger)

    val logger = InMemoryLogger()

    @JvmField
    @Rule
    val useMockedProfilesRetrieverRule = beforeLaunchingActivity(ProfilesListActivity::class.java) {
        loadKoinModules(
                listOf(
                        mockedProfilesRetrieverModule(mockedProfilesRetriever()),
                        mockedListConfigModule,
                        module {
                            single<Logger>(override = true) {
                                logger
                            }
                        }))
    }

    @Before
    fun clearLog() = logger.clear()

    @JvmField
    @Rule
    val dumpLogOnFailureRule = DumpLogOnFailureRule { logger.contents }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> =
            object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("has item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView) =
                        view.findViewHolderForAdapterPosition(position)
                                ?.let {
                                    itemMatcher.matches(it.itemView)
                                }
                                ?: false
            }

    @Test
    fun givenApiRetrievingMockedProfile_whenScreenIsOpen_profileIsDisplayedOnTheList() {
        onView(withId(R.id.profiles))
                .check(
                        matches(
                                atPosition(
                                        0,
                                        hasDescendant(
                                                allOf(
                                                        withId(R.id.name),
                                                        withText("Name (1)"))))))
    }


    @Test
    fun givenListScreenWithAProfile_whenProfileIsClicked_detailedViewIsOpened() {
        onView(withId(R.id.profiles))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.txt_name))
                .check(matches(withText(("Name (1)"))))
        onView(withId(R.id.age))
                .check(matches(withText(("21 years"))))
        onView(withId(R.id.location))
                .check(matches(withText(("United States"))))
    }
}