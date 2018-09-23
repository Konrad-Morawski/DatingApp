package com.kmorawski.sampleapp.views

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import com.kmorawski.sampleapp.InMemoryLogger
import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.api.ProfilesRetriever
import com.kmorawski.sampleapp.list.ErrorViewModel
import com.kmorawski.sampleapp.list.ListConfig
import com.kmorawski.sampleapp.list.LoadMoreIndicatorViewModel
import com.kmorawski.sampleapp.list.ProfileItem
import com.kmorawski.sampleapp.list.ProfileViewModel
import com.kmorawski.sampleapp.list.RetrievalResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

fun profilesOf(vararg profiles: Profile) = profiles.toList().let { Single.just(it) }

fun ProfileItem.fromProfile(value: ProfileViewModel.() -> String): String? =
        (this as? ProfileViewModel)?.value()

val corneliu = Profile("Corneliu", "Ciupe", "https://uinames.com/corneliu.jpg", 36, "Romania")
val patricia = Profile("Patricia", "Leitner", "https://uinames.com/patricia.jpg", 21, "Austria")
val kristine = Profile("Kristine", "Helland", "https://uinames.com/kristine.jpg", 24, "Norway")
val kanalas = Profile("Kanalas", "Róbert", "https://uinames.com/kanalas.jpg", 29, "Hungary")

class ProfilesListViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    val profilesRetriever = mock(ProfilesRetriever::class.java)
    var observer: (RetrievalResult) -> Unit = { }
    var owner: LifecycleOwner? = null

    val defaultAgeFormatter = object : AgeFormatter {
        override fun toReadableString(age: Int?): String? = age?.let { "$it years" }.orEmpty()
    }

    fun initiateViewModelUpAndRetrieveProfiles(listConfig: ListConfig) {
        val viewModel = ProfilesListViewModel(
                profilesRetriever,
                listConfig,
                defaultAgeFormatter,
                InMemoryLogger())
        viewModel.apply {
            retrieveMoreProfiles()
            profiles.observe(
                    owner!!,
                    Observer {
                        observer(it!!)
                    })
        }
    }

    @Before
    fun setUpMocks() {
        observer = mock()
        val registry = LifecycleRegistry(mock(LifecycleOwner::class.java)).apply {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        owner = LifecycleOwner { registry }
    }

    @Test
    fun `when API returns profiles - and ViewModel retrieves them - the result is a success with correctly converted profiles`() {
        val `five maximum items` = 5

        whenever(profilesRetriever.retrieve(any()))
                .thenReturn(
                        profilesOf(
                                corneliu,
                                patricia,
                                kristine))
        initiateViewModelUpAndRetrieveProfiles(ListConfig(pageSize = 3, maxItems = `five maximum items`))

        val captor = argumentCaptor<RetrievalResult>()
        verify(observer).invoke(captor.capture())

        assertEquals(1, captor.allValues.size)
        assertTrue(captor.firstValue is RetrievalResult.Success)

        with (captor.firstValue.items) {
            assertEquals("Corneliu", first().fromProfile { name })
            assertEquals("36 years", first().fromProfile { age })

            assertEquals("Patricia", get(1).fromProfile { name })
            assertEquals("21 years", get(1).fromProfile { age })

            assertEquals("Kristine", get(2).fromProfile { name })
            assertEquals("24 years", get(2).fromProfile { age })
        }
    }

    @Test
    fun `when API returns profiles - and ViewModel retrieves them while the config indicates there should be more to come - the last item should be a loading indicator`() {
        val `five maximum items` = 5
        whenever(profilesRetriever.retrieve(any()))
                .thenReturn(
                        profilesOf(
                                corneliu,
                                patricia,
                                kristine,
                                kanalas))
        initiateViewModelUpAndRetrieveProfiles(ListConfig(pageSize = 3, maxItems = `five maximum items`))

        val captor = argumentCaptor<RetrievalResult>()
        verify(observer).invoke(captor.capture())

        assertEquals(1, captor.allValues.size)
        captor.firstValue.items.apply {
            // we've only retrieved 3, and the maxItems is 5, so we should look forward to loading more
            assertTrue(last() === LoadMoreIndicatorViewModel)
        }
    }

    @Test
    fun `when API returns an error - and ViewModel attempts to retrieve profiles - an error indicator is returned`() {
        whenever(profilesRetriever.retrieve(any()))
                .thenReturn(Single.error(Exception("No internet for you!")))

        initiateViewModelUpAndRetrieveProfiles(ListConfig(pageSize = 1, maxItems = 1))

        verify(observer).let {
            it.invoke(argThat {
                this is RetrievalResult.Error
            })
            it.invoke(argThat {
                items.size == 1
            })
            it.invoke(argThat {
                items.first() is ErrorViewModel
            })
        }
    }
}