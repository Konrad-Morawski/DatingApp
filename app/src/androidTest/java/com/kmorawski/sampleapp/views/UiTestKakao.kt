package com.kmorawski.sampleapp.views

import android.view.View
import com.agoda.kakao.KRecyclerItem
import com.agoda.kakao.KRecyclerView
import com.agoda.kakao.KTextView
import com.agoda.kakao.Screen
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.views.mocks.beforeLaunchingActivity
import com.kmorawski.sampleapp.views.mocks.mockedListConfigModule
import com.kmorawski.sampleapp.views.mocks.mockedPageCount
import com.kmorawski.sampleapp.views.mocks.mockedPageSize
import com.kmorawski.sampleapp.views.mocks.mockedProfilesRetrieverModule
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.koin.standalone.StandAloneContext.loadKoinModules

// region Kakao
class ProfileItem(parent: Matcher<View>) : KRecyclerItem<ProfileItem>(parent) {
    val name: KTextView = KTextView(parent) {
        withId(R.id.name)
    }
    val age: KTextView = KTextView(parent) {
        withId(R.id.age)
    }
}

class ProfilesListScreen : Screen<ProfilesListScreen>() {
    val profiles = KRecyclerView(
            {
                withId(R.id.profiles)
            },
            itemTypeBuilder = {
                itemType(::ProfileItem)
            })
}

class ProfileDetailsScreen : Screen<ProfileDetailsScreen>() {
    val name = KTextView {
        withId(R.id.txt_name)
    }
    val age = KTextView {
        withId(R.id.age)
    }
    val location = KTextView {
        withId(R.id.location)
    }
}
// endregion

/**
 * @see <a href="https://github.com/agoda-com/Kakao">github.com/agoda-com/Kakao</a>
 * @see <a href="https://proandroiddev.com/writing-ui-tests-with-espresso-and-kakao-409c95d325bf">proandroiddev.com/writing-ui-tests-with-espresso-and-kakao</a>
 */
class UiTestKakao {
    val listScreen = ProfilesListScreen()
    val detailsScreen = ProfileDetailsScreen()

    fun mockedProfilesRetriever() = MockedProfilesRetriever()

    @JvmField
    @Rule
    val useMockedProfilesRetrieverRule = beforeLaunchingActivity(ProfilesListActivity::class.java) {
        loadKoinModules(
                listOf(
                        mockedProfilesRetrieverModule(mockedProfilesRetriever()),
                        mockedListConfigModule))
    }

    @Test
    fun givenApiRetrievingMockedProfile_whenScreenIsOpen_profileIsDisplayedOnTheList() {
        listScreen {
            profiles {
                isDisplayed()
                firstChild<ProfileItem> {
                    isVisible()
                    name {
                        hasText("Name (1)")
                    }
                    age {
                        hasText("21 years")
                    }
                }
            }
        }
    }

    @Test
    fun givenListScreenWithAProfile_whenProfileIsClicked_detailedViewIsOpened() {
        listScreen {
            profiles {
                firstChild<ProfileItem> {
                    click()
                }
            }
        }

        detailsScreen {
            name {
                hasText("Name (1)")
            }
            age {
                hasText("21 years")
            }
            location {
                hasText("United States")
            }
        }
    }

    @Test
    fun givenListOfProfiles_whenScrolledToTheBottom_moreProfilesAreLoaded() = with(listScreen) {
        assert(mockedPageCount == 2) {
            "The test assumes the mocked page count is 2 - only then is the page loaded after a scrolldown the last one, without another loading indicator at the end."
        }
        assert(mockedPageSize == 20) {
            "The test assumes a single page has the size of 20. Only then will the last name be 'Name (40)'"
        }

        profiles.scrollToEnd()
        profiles {
            isDisplayed()
            lastChild<ProfileItem> {
                name {
                    hasText("Name (40)")
                }
            }
        }
    }
}