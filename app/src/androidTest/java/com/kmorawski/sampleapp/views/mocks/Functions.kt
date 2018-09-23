package com.kmorawski.sampleapp.views.mocks

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import com.kmorawski.sampleapp.api.ProfilesRetriever
import com.kmorawski.sampleapp.list.ListConfig
import org.koin.dsl.module.module

fun <T : Activity> beforeLaunchingActivity(activityClass: Class<T>, action: () -> Unit) =
        object : ActivityTestRule<T>(activityClass) {
            override fun beforeActivityLaunched() {
                super.beforeActivityLaunched()
                action()
            }
        }

fun mockedProfilesRetrieverModule(mockedProfilesRetriever: ProfilesRetriever) = module {
    single(override = true) {
        mockedProfilesRetriever
    }
}

const val mockedPageSize = 20
const val mockedPageCount = 2

val mockedListConfigModule = module {
    single(override = true) {
        ListConfig(mockedPageSize, mockedPageSize * mockedPageCount)
    }
}