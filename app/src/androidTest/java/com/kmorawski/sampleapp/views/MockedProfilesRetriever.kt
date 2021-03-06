package com.kmorawski.sampleapp.views

import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.api.ProfilesRetriever
import com.kmorawski.sampleapp.views.mocks.mockedPageSize
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MockedProfilesRetriever() : ProfilesRetriever {
    private var returnedSoFar = 0

    private fun mockedProfiles(count: Int) = (1..count)
            .map {
                returnedSoFar + it
            }
            .map(this::createProfile)

    private fun createProfile(it: Int) = Profile(
            name = "Name ($it)",
            surname = "Surname ($it)",
            photo = "https://uinames.com/api/photos/female/" + (it % mockedPageSize) + 1 + ".jpg",
            age = 20 + (it % 10),
            region = "United States")

    override fun retrieve(amount: Int) = Single
            .just(mockedProfiles(amount))
            .subscribeOn(Schedulers.io())
            .delay(3, TimeUnit.SECONDS)
            .doOnSuccess {
                returnedSoFar += amount
            }
}